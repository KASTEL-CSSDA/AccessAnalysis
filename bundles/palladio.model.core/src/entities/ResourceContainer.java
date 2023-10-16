package entities;

import java.util.List;
import java.util.stream.Stream;

import model.abstractions.Identifyable;
import model.abstractions.Identity;
import model.abstractions.Nameable;

public class ResourceContainer implements Identifyable, Nameable {
    private List<AssemblyContext> assemblyContexts;
    private Identity id;
    private String name;
   
    public ResourceContainer(List<AssemblyContext> assemblyContexts, Identity id, String name) {
		super();
		this.assemblyContexts = assemblyContexts;
		this.id = id;
		this.name = name;
	}
    
	protected ResourceContainer() {
    	
    }
	
	@Override
    public Identity getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
    
    public Stream<AssemblyContext> getAssemblyContexts() {
        return assemblyContexts.stream();
    }

}
