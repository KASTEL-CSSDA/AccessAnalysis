package edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model;

import java.util.HashMap;
import java.util.List;

import entities.AssemblyContext;
import entities.Component;
import entities.Interface;
import entities.OperationSignature;
import entities.informationflow.Information;
import model.Adversary;
import model.DataSet;
import model.Encryption;
import model.EnrichedResourceContainer;
import model.LinkingResource;
import model.Location;
import model.tamperprotections.LinkingResourceTamperProtection;
import model.tamperprotections.ResourceTamperprotection;

public interface IAccessAnalysisParsableModel {

	List<OperationSignature> getOperationSignatures();
	List<Interface> getInterfaces(HashMap<String,OperationSignature> operationSignatures);
	List<DataSet> getDataSets(HashMap<String,Information> information);
	List<Component> getComponents(HashMap<String,Interface> interfaces);
	List<AssemblyContext> getAssemblyContexts(HashMap<String,Component> components);
	List<ResourceTamperprotection> getResourceTamperProtections();
	List<EnrichedResourceContainer> getResourceContainers(HashMap<String, ResourceTamperprotection> resourceTamperProtections,
			HashMap<String, AssemblyContext> assemblyContexts);
	List<Location> getLocations(HashMap<String, EnrichedResourceContainer> resourceContainers);
	List<LinkingResourceTamperProtection> getLinkTamperProtections();
	List<LinkingResource> getLinkingResources(HashMap<String, LinkingResourceTamperProtection> linkTamperProtections,
			HashMap<String, EnrichedResourceContainer> resourceContainers, HashMap<String, Location> locations);
	List<Adversary> getAdversaries(HashMap<String, Location> locations,
			HashMap<String, LinkingResourceTamperProtection> linkTamperProtections,
			HashMap<String, ResourceTamperprotection> resourceTamperProtections, HashMap<String, DataSet> dataSets);
	List<Encryption> getEntryptions(HashMap<String, LinkingResource> locations, HashMap<String, DataSet> dataSets);
}
