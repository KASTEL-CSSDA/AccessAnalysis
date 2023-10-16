package model.tamperprotections;

import model.abstractions.Identifyable;
import model.abstractions.Identity;
import model.abstractions.Nameable;

public abstract class AbstractTamperprotection implements Identifyable, Nameable {
    private Identity id;
    private String name;
    
    protected AbstractTamperprotection(Identity id, String name) {
		super();
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
}
