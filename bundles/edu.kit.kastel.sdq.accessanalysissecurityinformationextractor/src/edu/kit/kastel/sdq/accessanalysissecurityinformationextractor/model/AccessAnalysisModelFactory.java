package edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import model.Encryption;
import model.EnvironmentModel;
import model.abstractions.Identifyable;

public class AccessAnalysisModelFactory {

	private IAccessAnalysisParsableModel model;
	
	public AccessAnalysisModelFactory(IAccessAnalysisParsableModel model) {
		this.model = model;
	}
	
	public EnvironmentModel create() {
		var operationSignatures = toHashMap(model.getOperationSignatures());
		print(operationSignatures, "operationSignatures");
		
		var interfaces = toHashMap(model.getInterfaces(operationSignatures));
		print(interfaces, "interfaces");
		
		var informationList = operationSignatures.values().stream().flatMap(opSig -> opSig.getInformationFlow().getInformation()).collect(Collectors.toList());
		var informationMap = toHashMap(informationList);
		var dataSets = toHashMap(model.getDataSets(informationMap));
		print(dataSets, "dataSets");
		
		var components = toHashMap(model.getComponents(interfaces));
		print(components, "components");
		
		var assemblyContexts = toHashMap(model.getAssemblyContexts(components));
		print(assemblyContexts, "assemblyContexts");
		
		var resourceTamperProtections = toHashMap(model.getResourceTamperProtections());
		print(resourceTamperProtections, "resourceTamperProtections");
		
		var resourceContainers = toHashMap(model.getResourceContainers(resourceTamperProtections, assemblyContexts));
		print(resourceContainers, "resourceContainers");
		
		var locations = toHashMap(model.getLocations(resourceContainers));
		print(locations, "locations");
		
		var linkTamperProtections = toHashMap(model.getLinkTamperProtections());
		print(linkTamperProtections, "linkTamperProtections");
		
		var linkingResources = toHashMap(model.getLinkingResources(linkTamperProtections, resourceContainers, locations));
		print(linkingResources, "linkingResources");
		
		var adversaries = toHashMap(model.getAdversaries(locations, linkTamperProtections, resourceTamperProtections, dataSets));
		print(adversaries, "adversaries");
		
		var encryptions = toHashMap(model.getEntryptions(linkingResources, dataSets));
		print(encryptions, "encryptions");
		
		return new EnvironmentModel(
			new ArrayList<>(adversaries.values()),
			new ArrayList<>(locations.values()),
			new ArrayList<>(linkingResources.values()),
			new ArrayList<>(dataSets.values()),
			new ArrayList<>(encryptions.values())
		);
	}
	
	private static <V> void print(HashMap<String,V> map, String name){
		System.out.println("Printing " + name);
		map.values().stream().forEach(System.out::println);
		System.out.println();
	}
	
	private static <V extends Identifyable> HashMap<String,V> toHashMap(List<V> values){
		return toHashMap(values, (value)-> value.getId().getId());
	}
	
	private static <K,V> HashMap<K,V> toHashMap(List<V> values, Function<V,K> keySelector){
		var map = new HashMap<K,V>();
		if(values == null) return map;
		for(var value : values) {
			map.put(keySelector.apply(value), value);
		}
		return map;
	}
	
}