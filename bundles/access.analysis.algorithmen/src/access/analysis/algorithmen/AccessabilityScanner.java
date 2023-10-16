package access.analysis.algorithmen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import access.analysis.algorithmen.abstractions.AccessabilityResult;
import access.analysis.algorithmen.abstractions.AlgorithmConfiguration;
import access.analysis.algorithmen.abstractions.IAccessabilityScanner;
import entities.AssemblyContext;
import entities.Component;
import entities.Interface;
import entities.informationflow.Information;
import model.Adversary;
import model.ConnectionType;
import model.DataSet;
import model.EnrichedResourceContainer;
import model.EnvironmentModel;
import model.LinkingResource;
import model.Location;
import model.SharingType;
import model.tamperprotections.AbstractTamperprotection;
import model.tamperprotections.ResourceTamperprotection;

/*
 * This class provides all access traces of an adversary to a dataset
 */
public class AccessabilityScanner implements IAccessabilityScanner {

	private final EnvironmentModel environmentModel;
	private final AlgorithmConfiguration config;
	
	public AccessabilityScanner(EnvironmentModel environmentModel, AlgorithmConfiguration config) {
		this.environmentModel = environmentModel;
		this.config = config;
	}

	@Override
	public Stream<AccessabilityResult<DataSet>> getDataSetsAccessableBy(Adversary adversary) {
		return Stream.concat(getContainerAccessableInformation(adversary), getInterceptableInformation(adversary));
	}

	public Stream<AccessabilityResult<EnrichedResourceContainer>> getAccessableContainers(Adversary adversary){
		var directlyAccessable = adversary.getAccessableLocations().
				flatMap(location -> accessResourceContainersOf(location, adversary));
		
		return directlyAccessable;
	}
	
	public Stream<AccessabilityResult<DataSet>> getContainerAccessableInformation(Adversary adversary){
		return getReachableContainers(adversary).
				flatMap(containerAccess -> getInformationAvailableAt(containerAccess));
	}
	
	private Stream<AccessabilityResult<EnrichedResourceContainer>> getReachableContainers(Adversary adversary){
		return adversary.getAccessableLocations().
				flatMap(loc -> accessResourceContainersOf(loc, adversary));
	}
	
	public Stream<AccessabilityResult<DataSet>> getInterceptableInformation(Adversary adversary){
		return getAccessableLinkingResources(adversary).
			flatMap(ar -> getUnencryptedInformationFlowingOver(ar));
	}
	
	public Stream<AccessabilityResult<DataSet>> getUnencryptedInformationFlowingOver(AccessabilityResult<LinkingResource> link){
		return link.getAccessedElement().
			getConnectedResourceContainers().
			map(rc -> link.ThenAccess(rc, "container connected to link")).
			flatMap(rc -> getInformationSent(rc, link.getAccessedElement())).
			filter(dataSet -> !isEncryptedOn(dataSet.getAccessedElement(), link.getAccessedElement())); //SizeOf should maybe still be visible
	}
	
	private Stream<AccessabilityResult<DataSet>> getInformationSent(AccessabilityResult<EnrichedResourceContainer> sendingContainer, LinkingResource link){
		return sendingContainer.ThenAccessAll(EnrichedResourceContainer::getAssemblyContexts, "assembly located in container").
			map(assemblyAccess -> assemblyAccess.ThenAccessBy(AssemblyContext::getRealisingComponent, "component realizes assembly")).
			flatMap(componentAccess -> componentAccess.ThenAccessAll(Component::getProvidedInterfaces, "interface is provided by component and communicates over link unencrypted")).
			filter(interfaceAccess -> interfaceCommunicatesOver(interfaceAccess.getAccessedElement(), link)).
			flatMap(interfaceAccess -> getDataSetFrom(interfaceAccess));
	}
	
	private boolean interfaceCommunicatesOver(Interface interf, LinkingResource link) {
		var requiredContainer = link.
			getConnectedResourceContainers().
			filter(container -> InterfaceIsRequiredInContainer(container, interf)).
			findAny();
		
		var providingContainer = link.
			getConnectedResourceContainers().
			filter(container -> InterfaceIsProvidingInContainer(container, interf)).
			findAny();
		
		if(requiredContainer.isEmpty() || providingContainer.isEmpty()) return false;
		
		return !providingContainer.get().getId().equals(requiredContainer.get().getId());
	}
	
	private boolean InterfaceIsRequiredInContainer(EnrichedResourceContainer container, Interface interf) {
		return container.
			getAssemblyContexts().
			map(assembly -> assembly.getRealisingComponent()).
			flatMap(component -> component.getRequiredInterfaces()).
			anyMatch(reqInterf -> reqInterf.getId().equals(interf.getId()));
	}
	
	private boolean InterfaceIsProvidingInContainer(EnrichedResourceContainer container, Interface interf) {
		return container.
			getAssemblyContexts().
			map(assembly -> assembly.getRealisingComponent()).
			flatMap(component -> component.getProvidedInterfaces()).
			anyMatch(reqInterf -> reqInterf.getId().equals(interf.getId()));
	}
	
	private Stream<AccessabilityResult<DataSet>> getInformationAvailableAt(AccessabilityResult<EnrichedResourceContainer> container){
		List<AccessabilityResult<Component>> envolvedComponents = container.
			ThenAccessAll(EnrichedResourceContainer::getAssemblyContexts, "assembly located in container").
			map(assemblyAccess -> assemblyAccess.ThenAccessBy(AssemblyContext::getRealisingComponent, "component realizes assembly")).
			toList();
		
		Stream<AccessabilityResult<DataSet>> fromProided = envolvedComponents.stream().
			flatMap(component -> 
				component.
				getAccessedElement().
				getProvidedInterfaces().
				map(provI -> component.ThenAccess(provI, "is a provided interface at the container"))).
			flatMap(interf -> getDataSetFrom(interf));
			
		Stream<AccessabilityResult<DataSet>> fromRequired = envolvedComponents.stream().
			flatMap(component -> 
				component.
				getAccessedElement().
				getRequiredInterfaces().
				map(reqI -> component.ThenAccess(reqI, "is a required interface at the container"))).
			flatMap(interf -> getDataSetFrom(interf));
		
		return Stream.concat(fromProided, fromRequired).distinct();
	}
	
	private boolean isEncryptedOn(DataSet ds, LinkingResource link) {
		return environmentModel.
			getEncryptions().
			anyMatch(enc -> 
					enc.getEncrypDataSet().getId().equals(ds.getId()) &&
					enc.getEncryptedLinkingResource().getId().equals(link.getId())
			);
	}
	
	public Stream<AccessabilityResult<DataSet>> getDataSetFrom(AccessabilityResult<Interface> interfaceAccess){
		return interfaceAccess.ThenAccessAll(interf -> interf.getOperationSignatures(), "operation is contained in interface").
			flatMap(opSigAccess -> opSigAccess.ThenAccessAll(opSig -> opSig.getInformationFlow().getInformation(), "Information of Signature")).
			flatMap(infoAccess -> infoAccess.ThenAccessAll(info -> getDataSetsContaining(info), "info belongs to dataset"));
	}
	
	public Stream<DataSet> getDataSetsContaining(Information information){
		var containingDataSets = environmentModel.
		getDataSets().
		filter(ds -> ds.getContainedInformation().anyMatch(dataSetInfo -> information.getId().equals(dataSetInfo.getId()))).toList();
		
		if(config.UNKNOWN_DATASET_IS_ACCESS_VIOLATION && containingDataSets.size() == 0) {
			return List.of(DataSet.GetUnknownDataSet()).stream();	
		}
		
		return containingDataSets.stream();
	}
	
	public Stream<AccessabilityResult<LinkingResource>> getAccessableLinkingResources(Adversary adversary){
		return adversary.getAccessableLocations().
				flatMap(location -> accessLinksAt(location)).
				filter(linkAccess -> canAccessLinkData(adversary, linkAccess.getAccessedElement()));
	}
	
	public boolean canAccessLinkData(Adversary adversary, LinkingResource link) {
		return link.getTamperprotections().
				allMatch(tamperProtection -> adversary.getBypassableTamperprotections().
						anyMatch(tamperable -> tamperable.getId().equals(tamperProtection.getId())));
	}
	
	private Stream<LinkingResource> getLinksAt(Location location){
		return environmentModel.getLinkingResources().
				filter(
					link -> link.getspannedLocations().anyMatch(spannedLocaion -> spannedLocaion.getId().equals(location.getId()))
				);
	}
	
	private Stream<AccessabilityResult<LinkingResource>> accessLinksAt(Location location){
		return getLinksAt(location).
				map(link ->  AccessabilityResult.getNew(location, "can access loaction").ThenAccess(link, "link is accessable at location"));
	}
	
	private Stream<AccessabilityResult<EnrichedResourceContainer>> accessResourceContainersOf(Location location, Adversary adversary){
		return location.getResourceContainers().
				flatMap(resourceContainer -> tryAccessContainer(AccessabilityResult.getNew(location, "can access loaction"), resourceContainer, adversary));
	}
	
	private Stream<AccessabilityResult<EnrichedResourceContainer>> tryAccessContainer(AccessabilityResult<Location> location, EnrichedResourceContainer resourceContainer, Adversary adversary){
		var accessabilityResults = new ArrayList<AccessabilityResult<EnrichedResourceContainer>>();
		
		var tamperedAccess = !resourceContainer.hasTamperprotections() || 
				resourceContainer.getTamperprotections().
					allMatch(tamperprotection -> isByPassable(tamperprotection, adversary));
		
		if(tamperedAccess) {
			var names = resourceContainer.hasTamperprotections() ?
					String.join(", ", resourceContainer.getTamperprotections().map(ResourceTamperprotection::getName).toList()) :
					"no protections";
			accessabilityResults.add(location.ThenAccess(resourceContainer, "the adversary has bypassed the tamperprotections (" + names + ")"));
		}
		
		if(resourceContainer.getSharingType() == SharingType.Shared && resourceContainer.getConnectionType() == ConnectionType.Existing) {
			accessabilityResults.add(location.ThenAccess(resourceContainer, "sharing type is \"Shared\" and  connection type is \"Existing\""));
		}
		
		if(resourceContainer.getSharingType() == SharingType.Shared && resourceContainer.getConnectionType() == ConnectionType.Possible) {
			accessabilityResults.add(location.ThenAccess(resourceContainer, "sharing type is \"Shared\" and  connection type is \"Possible\""));
		}
		
		return accessabilityResults.stream();
	}
	
	private boolean isByPassable(AbstractTamperprotection tamperprotection, Adversary adversary) {
		return adversary.getBypassableTamperprotections().
				anyMatch(bypassableTamperprotection -> bypassableTamperprotection.getId().equals(tamperprotection.getId()));
	}
}
