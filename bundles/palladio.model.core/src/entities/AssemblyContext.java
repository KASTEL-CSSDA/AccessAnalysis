package entities;

import model.abstractions.Identifyable;
import model.abstractions.Identity;
import model.abstractions.Nameable;

public class AssemblyContext implements Identifyable, Nameable {
    private Component realisingComponent;
    private Identity id;
    private String name;

    public static AssemblyContext CreateNew(String name, Component realisingComponent) {
    	return new AssemblyContext(realisingComponent, Identity.CreateNew(), name);
    }
    
    public AssemblyContext(Component realisingComponent, Identity id, String name) {
		super();
		this.realisingComponent = realisingComponent;
		this.id = id;
		this.name = name;
	}
    
    @SuppressWarnings("unused")
	private AssemblyContext() {
    	
    }

	@Override
    public Identity getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
    	return name + " holding " + realisingComponent.getName();
    }
    
    public Component getRealisingComponent() {
        return realisingComponent;
    }
}
