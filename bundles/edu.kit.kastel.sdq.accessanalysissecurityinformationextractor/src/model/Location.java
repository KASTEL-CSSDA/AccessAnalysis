package model;

import java.util.List;
import java.util.stream.Stream;

import model.abstractions.Identifyable;
import model.abstractions.Identity;
import model.abstractions.Nameable;

public class Location implements Identifyable, Nameable {
	private List<EnrichedResourceContainer> resourceContainers;
    private Identity id;
    private String name;

    public static Location CreateNew(String name, List<EnrichedResourceContainer> resourceContainers) {
    	return new Location(resourceContainers, Identity.CreateNew(), name);
    }
    
    public Location(List<EnrichedResourceContainer> resourceContainers, Identity id, String name) {
		super();
		this.resourceContainers = resourceContainers;
		this.id = id;
		this.name = name;
	}

	public Stream<EnrichedResourceContainer> getResourceContainers() {
		return resourceContainers.stream();
	}

	@Override
    public Identity getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
}
