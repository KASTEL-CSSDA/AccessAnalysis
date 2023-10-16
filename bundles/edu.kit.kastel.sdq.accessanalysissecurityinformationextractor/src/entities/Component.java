package entities;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import model.abstractions.Identifyable;
import model.abstractions.Identity;
import model.abstractions.Nameable;

public class Component implements Identifyable, Nameable {
    private List<Interface> providedInterfaces;
    private List<Interface> requiredInterfaces;
    private Identity id;
    private String name;
    
    public static Component CreateNew(String name, List<Interface> providedInterfaces, List<Interface> requiredInterfaces) {
    	return new Component(providedInterfaces, requiredInterfaces, Identity.CreateNew(), name);
    }

    public Component(List<Interface> providedInterfaces, List<Interface> requiredInterfaces, Identity id,
			String name) {
		super();
		this.providedInterfaces = providedInterfaces;
		this.requiredInterfaces = requiredInterfaces;
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
    
    public Stream<Interface> getRequiredInterfaces() {
        return requiredInterfaces.stream();
    }

    public Stream<Interface> getProvidedInterfaces() {
        return providedInterfaces.stream();
    }
    
    @Override
    public String toString() {
        return name + " prov:" + 
        		String.join(",", providedInterfaces.stream().map(Interface::getName).collect(Collectors.toList())) + " req:" + 
        		String.join(",", requiredInterfaces.stream().map(Interface::getName).collect(Collectors.toList()));
    }
}
