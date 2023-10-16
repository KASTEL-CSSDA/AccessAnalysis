package model;

import java.util.List;
import java.util.stream.Stream;

import model.abstractions.Identifyable;
import model.abstractions.Identity;
import model.abstractions.Nameable;
import model.tamperprotections.AbstractTamperprotection;

public class Adversary implements Identifyable, Nameable {

    private List<DataSet> allowedToKnowDataSets;
    private List<AbstractTamperprotection> canBypassTamperprotections;
    private List<Location> accessableLocations;
    private Identity id;
    private String name;

    public static Adversary CreateNewFrom(String name, List<DataSet> allowedToKnowDataSets, List<AbstractTamperprotection> canBypassTamperprotections,
			List<Location> accessableLocations) {
    	return new Adversary(allowedToKnowDataSets, canBypassTamperprotections, accessableLocations, Identity.CreateNew(), name);
    }
    
    public Adversary(List<DataSet> allowedToKnowDataSets, List<AbstractTamperprotection> canBypassTamperprotections,
			List<Location> accessableLocations, Identity id, String name) {
		super();
		this.allowedToKnowDataSets = allowedToKnowDataSets;
		this.canBypassTamperprotections = canBypassTamperprotections;
		this.accessableLocations = accessableLocations;
		this.id = id;
		this.name = name;
	}

	@Override
    public Identity getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public Stream<Location> getAccessableLocations() {
        return accessableLocations.stream();
    }

    public Stream<AbstractTamperprotection> getBypassableTamperprotections() {
        return canBypassTamperprotections.stream();
    }

    public Stream<DataSet> getAllowedToKnowDataSets() {
        return allowedToKnowDataSets.stream();
    }

} 