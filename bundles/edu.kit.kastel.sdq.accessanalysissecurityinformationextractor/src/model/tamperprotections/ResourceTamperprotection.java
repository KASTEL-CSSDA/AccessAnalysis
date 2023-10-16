package model.tamperprotections;

import model.abstractions.Identity;

public class ResourceTamperprotection extends AbstractTamperprotection {

	public static ResourceTamperprotection CreateNew(String name) {
		return new ResourceTamperprotection(Identity.CreateNew(), name);
	}
	
	public ResourceTamperprotection(Identity id, String name) {
		super(id, name);
	}
    
}
