package model;

import java.util.List;
import java.util.stream.Stream;

import model.abstractions.Identifyable;
import model.abstractions.Identity;
import model.abstractions.Nameable;
import model.tamperprotections.LinkingResourceTamperProtection;

public class LinkingResource implements Identifyable, Nameable {
    private List<EnrichedResourceContainer> connectedResourceContainers;
    private List<LinkingResourceTamperProtection> tamperprotections;
    private List<Location> spannedLocations;
    private Identity id;
    private String name;

    public static LinkingResource CreateNewFrom(String name, List<EnrichedResourceContainer> connectedResourceContainers,
			List<LinkingResourceTamperProtection> tamperprotections, List<Location> spannedLocations) {
    	return new LinkingResource(connectedResourceContainers, tamperprotections, spannedLocations, Identity.CreateNew(), name);
    }
    
    public LinkingResource(List<EnrichedResourceContainer> connectedResourceContainers,
			List<LinkingResourceTamperProtection> tamperprotections, List<Location> spannedLocations, Identity id, String name) {
		super();
		this.connectedResourceContainers = connectedResourceContainers;
		this.tamperprotections = tamperprotections;
		this.id = id;
		this.name = name;
		this.spannedLocations = spannedLocations;
	}

	@Override
    public Identity getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
    
    public Stream<LinkingResourceTamperProtection> getTamperprotections() {
        return tamperprotections.stream();
    }
    
    public Stream<EnrichedResourceContainer> getConnectedResourceContainers() {
        return connectedResourceContainers.stream();
    }
    
    public Stream<Location> getspannedLocations() {
        return spannedLocations.stream();
    }
}
