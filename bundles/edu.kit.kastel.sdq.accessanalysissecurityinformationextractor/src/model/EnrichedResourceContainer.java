package model;

import java.util.List;
import java.util.stream.Stream;

import entities.AssemblyContext;
import entities.ResourceContainer;

import model.abstractions.Identity;
import model.tamperprotections.ResourceTamperprotection;

public class EnrichedResourceContainer extends ResourceContainer {
	private List<ResourceTamperprotection> tamperprotections;
    private SharingType sharingType;
    private ConnectionType connectionType;
    
    public EnrichedResourceContainer(
		List<ResourceTamperprotection> tamperprotections,
		List<AssemblyContext> assemblyContexts, 
		SharingType sharingType, 
		ConnectionType connectionType, 
		Identity id,
		String name
	)
    {
		super(assemblyContexts, id, name);
		this.tamperprotections = tamperprotections;
		this.sharingType = sharingType;
		this.connectionType = connectionType;
	}
    
    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public SharingType getSharingType() {
        return sharingType;
    }
    public Stream<ResourceTamperprotection> getTamperprotections() {
        return tamperprotections.stream();
    }
    
    public boolean hasTamperprotections() {
    	return !tamperprotections.isEmpty();
    }
}
