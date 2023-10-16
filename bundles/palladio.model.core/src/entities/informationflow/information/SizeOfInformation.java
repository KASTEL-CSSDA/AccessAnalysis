package entities.informationflow.information;

import entities.informationflow.Information;
import model.abstractions.Identity;

public class SizeOfInformation extends Information {
	
    protected SizeOfInformation(Identity id, String name, SizeableInformation referencedInformation) {
		super(id, name);
	}
    
    public static SizeOfInformation CreateNew(String name, SizeableInformation referencedInformation) {
    	return new SizeOfInformation(Identity.CreateNew(), name, referencedInformation);
    }
    
    @SuppressWarnings("unused")
    private SizeOfInformation() {}

	private SizeableInformation referencedInformation;

    public SizeableInformation getReferencedInformation() {
        return referencedInformation;
    }
}
