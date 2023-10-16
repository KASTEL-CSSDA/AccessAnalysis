package edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.mdsdprofiles.api.StereotypeAPI;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RequiredRole;

import edu.kit.ipd.sdq.commons.util.org.palladiosimulator.mdsdprofiles.api.StereotypeAPIUtil;
import edu.kit.kastel.scbs.confidentiality.data.DataIdentifying;
import edu.kit.kastel.scbs.confidentiality.repository.ParametersAndDataPair;
import edu.kit.kastel.scbs.confidentiality.resources.LocationsAndTamperProtectionsPair;
import edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.modelextractor.ExtractorUtils;
import entities.AssemblyContext;
import entities.Component;
import entities.Interface;
import entities.OperationSignature;
import entities.informationflow.Information;
import entities.informationflow.SignatureInformationFlow;
import entities.informationflow.information.CallInformation;
import entities.informationflow.information.ParameterInformation;
import entities.informationflow.information.ReturnValueInformation;
import entities.informationflow.information.SizeOfInformation;
import entities.informationflow.information.SizeableInformation;
import model.Adversary;
import model.ConnectionType;
import model.DataSet;
import model.Encryption;
import model.EnrichedResourceContainer;
import model.LinkingResource;
import model.Location;
import model.SharingType;
import model.abstractions.Identity;
import model.tamperprotections.AbstractTamperprotection;
import model.tamperprotections.LinkingResourceTamperProtection;
import model.tamperprotections.ResourceTamperprotection;

public class PcmModelAdapter implements IAccessAnalysisParsableModel {
	
	private PCMModels model;
	private HashMap<String, List<Information>> informationInDataSets;
	
	public PcmModelAdapter(PCMModels model) {
		this.model = model;
		this.informationInDataSets = new HashMap<>();
	}
	
	@Override
	public List<OperationSignature> getOperationSignatures() {
		return ExtractorUtils.getOperationInterfaces(model.getRepository()).stream().
			flatMap(inter -> inter.getSignatures__OperationInterface().stream()).
			map(opSig -> 
				new OperationSignature(
					InformationFlowOf(opSig),
					new Identity(opSig.getId()),
					opSig.getEntityName()
				)
			).
			collect(Collectors.toList());
	}
	
	private SignatureInformationFlow InformationFlowOf(org.palladiosimulator.pcm.repository.OperationSignature operationSignature) {
		var stereoTypeApplications = model.getProfile().
				getStereotypeApplications(operationSignature).
				stream().
				collect(Collectors.toList());
		
		var parameterAndDataSetPairs = StereotypeAPIUtil.getTaggedValues(
				stereoTypeApplications,
				"parametersAndDataPairs",
				ParametersAndDataPair.class
		);
		
		var returnValue = operationSignature.getReturnType__OperationSignature();
		var parameters = operationSignature.getParameters__OperationSignature();
		var information = generateInformationFrom(parameterAndDataSetPairs, parameters, returnValue != null);
		
		return SignatureInformationFlow.CreateNew(information); 
	}
	
	private static final String callParameter = "\\call";
	private static final String returnParameter = "\\return";
	private static final String sizeOfParameterPrefix = "sizeOf";
	private static final String sizeOfAll = sizeOfParameterPrefix + "(*)";
	private static String getSizeKey(String name) {
		return sizeOfParameterPrefix + "(" + name + ")";
	}
	
	private List<Information> generateInformationFrom(
		List<edu.kit.kastel.scbs.confidentiality.repository.ParametersAndDataPair> parameterAndDataSetPairs,
		EList<org.palladiosimulator.pcm.repository.Parameter> paramerts,
		boolean hasReturnValue
	){
		var infos = new HashSet<Information>();
		var infoLookup = new HashMap<String, Information>();
		
		var callInformation = CallInformation.CreateNew();
		
		var allSizeablesShouldBeInDatasets = new ArrayList<DataIdentifying>();
		var sizeableInformation = new HashSet<SizeOfInformation>();
		
		for(var param : paramerts) {
			var key = param.getParameterName();
			var newInformation = parameterSourceToInformation(key, infoLookup);
			infos.add(newInformation);
			
			var infoSizeKey = getSizeKey(key);
			var infoSize = (SizeOfInformation) infoLookup.computeIfAbsent(
				infoSizeKey, 
				(k) -> SizeOfInformation.CreateNew(getSizeKey(key), (SizeableInformation) newInformation)
			);
			sizeableInformation.add(infoSize);
			infos.add(infoSize);
		}
		
		if(hasReturnValue) {
			var returnInformation = ReturnValueInformation.CreateNew();
			var returnInformationSizeOf = SizeOfInformation.CreateNew(getSizeKey(returnParameter), returnInformation);
			
			infoLookup.put(returnParameter, returnInformation);
			infos.add(returnInformation);
		
			infoLookup.put(getSizeKey(returnParameter), returnInformationSizeOf);
			infos.add(returnInformationSizeOf);
			sizeableInformation.add(returnInformationSizeOf);
		}
		
		infoLookup.put(callParameter, callInformation);
		infos.add(callInformation);
		
		for (var pair : parameterAndDataSetPairs) {
			for (var parameterSource : pair.getParameterSources()) {
				
				if(!hasReturnValue && parameterSource.equals(returnParameter)) {
					continue;
				}
				
				if(parameterSource.equals(sizeOfAll)) {
					allSizeablesShouldBeInDatasets.addAll(pair.getDataTargets());
					continue;
				}
				
				var newInformation = parameterSourceToInformation(parameterSource, infoLookup);
				infos.add(newInformation);
				
				if(newInformation instanceof SizeableInformation) {
					var infoSizeKey = getSizeKey(parameterSource);
					var infoSize = (SizeOfInformation) infoLookup.computeIfAbsent(
						infoSizeKey, 
						(key) -> SizeOfInformation.CreateNew(getSizeKey(parameterSource), (SizeableInformation) newInformation)
					);
					sizeableInformation.add(infoSize);
					infos.add(infoSize);
				}
				
				for (var dataSet : pair.getDataTargets()) {
					informationInDataSets.computeIfAbsent(dataSet.getId(), (key) -> new ArrayList<Information>());
					informationInDataSets.get(dataSet.getId()).add(newInformation);
				}
			}
		}
		
		for(var dataId : allSizeablesShouldBeInDatasets) {
			informationInDataSets.computeIfAbsent(dataId.getId(), (key) -> new ArrayList<Information>());
			for(var sizable : sizeableInformation) {
				informationInDataSets.get(dataId.getId()).add(sizable);
			}
		}
		
		return new ArrayList<Information>(infos);
	}
	
	private Information parameterSourceToInformation(
		String parameterSource,
		HashMap<String, Information> infoLookup
	) {
		if(infoLookup.containsKey(parameterSource)) {
			return infoLookup.get(parameterSource);
		}
		
		return ParameterInformation.CreateNew(parameterSource);
	}

	@Override
	public List<Interface> getInterfaces(HashMap<String, OperationSignature> operationSignatures) {
		return ExtractorUtils.getOperationInterfaces(model.getRepository()).
			stream().
			map(inter -> 
				new Interface(
					getOperationSignaturesFor(inter, operationSignatures),
					null,
					new Identity(inter.getId()),
					inter.getEntityName()
				)
			).
			collect(Collectors.toList());
	}
	
	private List<OperationSignature> getOperationSignaturesFor(
		org.palladiosimulator.pcm.repository.OperationInterface interf,
		HashMap<String, OperationSignature> operationSignatures
	){
		return interf.getSignatures__OperationInterface().
			stream().
			map(opSig -> opSig.getId()).
			map(opSigId -> operationSignatures.get(opSigId)).
			collect(Collectors.toList());
	}

	@Override
	public List<DataSet> getDataSets(HashMap<String, Information> information) {
		return model.getConfidentiality().getParametersAndDataPairs().
			stream().
			flatMap(pair -> pair.getDataTargets().stream()).
			map(dataSet -> (edu.kit.kastel.scbs.confidentiality.data.impl.DataSetImpl)dataSet).
			map(dataSet -> 
				new DataSet(
					getInformationFlowsIn(dataSet),
					null, // TODO get InformationFlowParameters
					new Identity(dataSet.getId()),
					dataSet.getName()
				)
			).collect(Collectors.toList());
	}
	
	private List<Information> getInformationFlowsIn(edu.kit.kastel.scbs.confidentiality.data.impl.DataSetImpl dataSet){
		informationInDataSets.putIfAbsent(dataSet.getId(), new ArrayList<Information>());
		return informationInDataSets.get(dataSet.getId());
	}

	@Override
	public List<Component> getComponents(HashMap<String, Interface> interfaces) {
		return ExtractorUtils.getBasicComponents(model.getRepository()).
			stream().
			map(comp -> new Component(
				getInterfacesFrom(comp.getProvidedRoles_InterfaceProvidingEntity().stream().map(this::GetIdOfInterfaceFrom).collect(Collectors.toList()), interfaces),
				getInterfacesFrom(comp.getRequiredRoles_InterfaceRequiringEntity().stream().map(this::GetIdOfInterfaceFrom).collect(Collectors.toList()), interfaces),
				new Identity(comp.getId()),
				comp.getEntityName()
			)).
			collect(Collectors.toList());
	}
	
	private String GetIdOfInterfaceFrom(ProvidedRole prov) {
		var cast = (org.palladiosimulator.pcm.repository.impl.OperationProvidedRoleImpl)prov;
		return cast.getProvidedInterface__OperationProvidedRole().getId();
	}
	
	private String GetIdOfInterfaceFrom(RequiredRole prov) {
		var cast = (org.palladiosimulator.pcm.repository.impl.OperationRequiredRoleImpl)prov;
		return cast.getRequiredInterface__OperationRequiredRole().getId();
	}

	private List<Interface> getInterfacesFrom(List<String> ids, HashMap<String, Interface> interfaces){
		return ids.stream().map(id -> interfaces.get(id)).collect(Collectors.toList());
	}
	
	@Override
	public List<AssemblyContext> getAssemblyContexts(HashMap<String, Component> components) {
		return model.getAllocations().getAllocationContexts_Allocation().stream().
			map(allocContext -> allocContext.getAssemblyContext_AllocationContext()).
			filter( assemblyContext -> assemblyContext.getEncapsulatedComponent__AssemblyContext() != null).
			map(assemblyContext -> 
				new AssemblyContext(
					components.get(assemblyContext.getEncapsulatedComponent__AssemblyContext().getId()),
					new Identity(assemblyContext.getId()),
					assemblyContext.getEntityName()
				)
			).
			collect(Collectors.toList());
	}

	@Override
	public List<ResourceTamperprotection> getResourceTamperProtections() {
		return model.getConfidentiality().
			getTamperProtections().
			stream().
			filter(this::isResourceTamperprotection).
			map(tamperProt -> new ResourceTamperprotection(new Identity(tamperProt.getId()), tamperProt.getName())).
			collect(Collectors.toList());
	}

	private boolean isResourceTamperprotection(edu.kit.kastel.scbs.confidentiality.resources.TamperProtection tamperProt) {
		return model.getResourceEnv().
				getResourceContainer_ResourceEnvironment().
				stream().
				anyMatch(container -> getTamperProtectionIdsFor(container).anyMatch(tamperId -> tamperProt.getId().equals(tamperId)));
	}
	
	@Override
	public List<EnrichedResourceContainer> getResourceContainers(
		HashMap<String, ResourceTamperprotection> resourceTamperProtections,
		HashMap<String, AssemblyContext> assemblyContexts
	) {
		return model.getResourceEnv().
			getResourceContainer_ResourceEnvironment().
			stream().
			map(container -> 
				new EnrichedResourceContainer(
						getTamperProtectionsFor(container, resourceTamperProtections),
						getAssemblyContextsIn(container, assemblyContexts),
						getSharingTypeOf(container),
						getConnectionTypeOf(container),
						new Identity(container.getId()),
						container.getEntityName()
				)
			).
			collect(Collectors.toList());
	}
	
	private Stream<String> getTamperProtectionIdsFor(
			org.palladiosimulator.pcm.resourceenvironment.ResourceContainer container
	) {
		var pairs = StereotypeAPI.<List<LocationsAndTamperProtectionsPair>>getTaggedValueSafe(container, "locationsAndTamperProtectionsPairs", "LocationAndTamperProtection");
    	if (!pairs.isPresent()) {
    		return new ArrayList<String>().stream();
    	}
    	return pairs.get().stream().
    			flatMap(prot -> prot.getTamperProtections().stream()).
    			map(prot -> prot.getId());
	}
	
	private List<ResourceTamperprotection> getTamperProtectionsFor(
			org.palladiosimulator.pcm.resourceenvironment.ResourceContainer container,
			HashMap<String, ResourceTamperprotection> resourceTamperProtections
	) {
		return getTamperProtectionIdsFor(container).
    			map(id -> resourceTamperProtections.get(id)).
    			collect(Collectors.toList());
	}
	
	private SharingType getSharingTypeOf(org.palladiosimulator.pcm.resourceenvironment.ResourceContainer container) {
		var sharingType = StereotypeAPI.<edu.kit.kastel.scbs.confidentiality.resources.SharingType>getTaggedValueSafe(container, "sharing", "Sharing");
    	if (!sharingType.isPresent()) {
    		throw new IllegalArgumentException("No sharing type defined");
    	}
    	
    	switch (sharingType.get()) {
			case EXCLUSIVE: return SharingType.Exclusive;
			case SHARED: return SharingType.Shared;
			default: throw new IllegalArgumentException("Unexpected value: " + sharingType.get());
    	}
	}
	
	private ConnectionType getConnectionTypeOf(org.palladiosimulator.pcm.resourceenvironment.ResourceContainer container) {
		var connectionType = StereotypeAPI.<edu.kit.kastel.scbs.confidentiality.resources.ConnectionType>getTaggedValueSafe(container, "connectionType", "FurtherPhysicalConnections");
		if (!connectionType.isPresent()) {
			throw new IllegalArgumentException("No connection type defined");
    	}
		
		switch (connectionType.get()) {
			case COMPLETE: return ConnectionType.Complete;
			case EXISTING: return ConnectionType.Existing;
			case POSSIBLE: return ConnectionType.Possible;
			default: throw new IllegalArgumentException("Unexpected value: " + connectionType.get());
		}
	}
	
	private List<AssemblyContext> getAssemblyContextsIn(
		org.palladiosimulator.pcm.resourceenvironment.ResourceContainer container,
		HashMap<String, AssemblyContext> assemblyContexts
	){
		return model.getAllocations().getAllocationContexts_Allocation().stream().
			filter(alloc -> alloc.getResourceContainer_AllocationContext().getId().equals(container.getId())).
			map(alloc -> alloc.getAssemblyContext_AllocationContext().getId()).
			map(assemblyId -> assemblyContexts.get(assemblyId)).
			collect(Collectors.toList());
	}

	@Override
	public List<Location> getLocations(HashMap<String, EnrichedResourceContainer> resourceContainers) {
		return model.getConfidentiality().
			getLocations().
			stream().
			map(loc -> 
				new Location(
					getresourceContainersIn(loc, resourceContainers),
					new Identity(loc.getId()),
					loc.getName()
				)
			).
			collect(Collectors.toList());
	}
	
	private List<EnrichedResourceContainer> getresourceContainersIn(
			edu.kit.kastel.scbs.confidentiality.resources.Location location,
			HashMap<String, EnrichedResourceContainer> resourceContainers
	){
		return model.getResourceEnv().
			getResourceContainer_ResourceEnvironment().
			stream().
			filter(container -> ContainerIsAtLocation(container, location)).
			map(container -> resourceContainers.get(container.getId())).
			collect(Collectors.toList());
	}
	
	private boolean ContainerIsAtLocation(
			org.palladiosimulator.pcm.resourceenvironment.ResourceContainer container,
			edu.kit.kastel.scbs.confidentiality.resources.Location location
	) {
		var pairs = StereotypeAPI.<List<LocationsAndTamperProtectionsPair>>getTaggedValueSafe(container, "locationsAndTamperProtectionsPairs", "LocationAndTamperProtection");
    	if (!pairs.isPresent()) {
    		return false;
    	}
    	return pairs.get().stream().
    			flatMap(prot -> prot.getLocations().stream()).
    			anyMatch(loc -> loc.getId().equals(location.getId()));
	}

	@Override
	public List<LinkingResourceTamperProtection> getLinkTamperProtections() {
		return model.getConfidentiality().
			getTamperProtections().
			stream().
			filter(this::isLinkTamperprotection).
			map(tamperProt -> 
				new LinkingResourceTamperProtection(
					new Identity(tamperProt.getId()),
					tamperProt.getName()
				)
			).
			collect(Collectors.toList());
	}

	@Override
	public List<LinkingResource> getLinkingResources(
		HashMap<String, LinkingResourceTamperProtection> linkTamperProtections,
		HashMap<String, EnrichedResourceContainer> resourceContainers,
		HashMap<String, Location> locations
	) {
		return model.getResourceEnv().
			getLinkingResources__ResourceEnvironment().
			stream().
			map(link -> 
				new LinkingResource(
					getResourceContainersSpannedBy(link, resourceContainers),
					getTamperprotectionsFor(link, linkTamperProtections),
					getSpannedLocationOf(link, locations),
					new Identity(link.getId()),
					link.getEntityName()
				)
			).
			collect(Collectors.toList());
	}
	
	private List<Location> getSpannedLocationOf(
		org.palladiosimulator.pcm.resourceenvironment.LinkingResource linkingResource,
		HashMap<String, Location> locations
	){
		var chars = StereotypeAPI.<List<LocationsAndTamperProtectionsPair>>getTaggedValueSafe(linkingResource, "locationsAndTamperProtectionsPairs", "LocationAndTamperProtection");
    	if (!chars.isPresent()) {
    		return List.of();
    	}
    	
    	return chars.get().stream().
			flatMap(prot -> prot.getLocations().stream()).
			map(prot -> locations.get(prot.getId())).
			collect(Collectors.toList());
	}
	
	private Stream<String> getTamperprotectionIdsFor(
			org.palladiosimulator.pcm.resourceenvironment.LinkingResource linkingResource
	){
		var chars = StereotypeAPI.<List<LocationsAndTamperProtectionsPair>>getTaggedValueSafe(linkingResource, "locationsAndTamperProtectionsPairs", "LocationAndTamperProtection");
    	if (!chars.isPresent()) {
    		return new ArrayList<String>().stream();
    	}
    	
    	return chars.get().stream().
			flatMap(prot -> prot.getTamperProtections().stream()).
			map(prot -> prot.getId());
	}
	
	private boolean isLinkTamperprotection(edu.kit.kastel.scbs.confidentiality.resources.TamperProtection tamperProt) {
		return model.getResourceEnv().
				getLinkingResources__ResourceEnvironment().
				stream().
				anyMatch(link -> getTamperprotectionIdsFor(link).anyMatch(tamperId -> tamperProt.getId().equals(tamperId)));
	}
	
	private List<LinkingResourceTamperProtection> getTamperprotectionsFor(
		org.palladiosimulator.pcm.resourceenvironment.LinkingResource linkingResource,
		HashMap<String, LinkingResourceTamperProtection> linkTamperProtections
	){
		return getTamperprotectionIdsFor(linkingResource).
			map(protId -> linkTamperProtections.get(protId)).
			collect(Collectors.toList());
	}
	
	private List<EnrichedResourceContainer> getResourceContainersSpannedBy(
		org.palladiosimulator.pcm.resourceenvironment.LinkingResource link,
		HashMap<String, EnrichedResourceContainer> resourceContainers
	){
		return link.getConnectedResourceContainers_LinkingResource().
			stream().
			map(container -> container.getId()).
			map(containerId -> resourceContainers.get(containerId)).
			collect(Collectors.toList());
	}

	@Override
	public List<Adversary> getAdversaries(
		HashMap<String, Location> locations,
		HashMap<String, LinkingResourceTamperProtection> linkTamperProtections,
		HashMap<String, ResourceTamperprotection> resourceTamperProtections,
		HashMap<String, DataSet> dataSets
	) {
		
		return model.getAdversaries().
			getAdversaries().
			stream().
			map(adv -> 
				new Adversary(
					getMayKnowsFor(adv, dataSets),
					getCanBypassTamperprotectionsFor(adv, linkTamperProtections, resourceTamperProtections),
					getAccessableLocationsFor(adv, locations),
					new Identity(adv.getId()),
					adv.getName()
				)
			).collect(Collectors.toList());
	}
	
	private List<DataSet> getMayKnowsFor(edu.kit.kastel.scbs.confidentiality.adversary.Adversary adv, HashMap<String, DataSet> dataSets){
		return adv.getMayKnowData().
			stream().
			map(mayKnow -> dataSets.get(mayKnow.getId())).
			collect(Collectors.toList());
	}
	
	private List<AbstractTamperprotection> getCanBypassTamperprotectionsFor(
		edu.kit.kastel.scbs.confidentiality.adversary.Adversary adv,
		HashMap<String, LinkingResourceTamperProtection> linkTamperProtections,
		HashMap<String, ResourceTamperprotection> resourceTamperProtections
	){
		return adv.getLocationsAndTamperProtectionsPairs().
			stream().
			flatMap(pair -> pair.getTamperProtections().stream()).
			map(tamperProt -> 
				resourceTamperProtections.containsKey(tamperProt.getId()) ? 
					resourceTamperProtections.get(tamperProt.getId()) :
					linkTamperProtections.get(tamperProt.getId())
			).
			collect(Collectors.toList());
	}
	
	private List<Location> getAccessableLocationsFor(edu.kit.kastel.scbs.confidentiality.adversary.Adversary adv, HashMap<String, Location> locations){
		return adv.getLocationsAndTamperProtectionsPairs().
			stream().
			flatMap(pair -> pair.getLocations().stream()).
			map(location -> location.getId()).
			map(locationId -> locations.get(locationId)).
			collect(Collectors.toList());
	}

	@Override
	public List<Encryption> getEntryptions(HashMap<String, LinkingResource> linkingResources,
			HashMap<String, DataSet> dataSets) {
		return model.getResourceEnvProfile().getStereotypeApplications().stream().
			filter(item -> item.getStereotype().getName().equals("Encryption") && item.getAppliedTo() instanceof org.palladiosimulator.pcm.resourceenvironment.LinkingResource).
			map(item -> (org.palladiosimulator.pcm.resourceenvironment.LinkingResource) item.getAppliedTo()).
			map(link -> link.getId()).map(linkId -> linkingResources.get(linkId)).
			flatMap(link -> dataSets.values().stream().map(ds -> new Encryption(link, ds, Identity.CreateNew(), "Enryption"))).
			collect(Collectors.toList());
	}

}
