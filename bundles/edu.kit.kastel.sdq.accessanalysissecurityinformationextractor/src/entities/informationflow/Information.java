package entities.informationflow;

import model.abstractions.Identifyable;
import model.abstractions.Identity;
import model.abstractions.Nameable;

public class Information implements Identifyable, Nameable {
    private Identity id;
    private String name;
    
    protected Information(Identity id, String name) {
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
