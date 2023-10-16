package model.tamperprotections;

import model.abstractions.Identity;


public class LinkingResourceTamperProtection extends AbstractTamperprotection {
    
    public static LinkingResourceTamperProtection CreateNew(String name) {
		return new LinkingResourceTamperProtection(Identity.CreateNew(), name);
	}
	
    @SuppressWarnings("unused")
	private LinkingResourceTamperProtection() {
    	
    }
    
	public LinkingResourceTamperProtection(Identity id, String name) {
		super(id, name);
	}
}
