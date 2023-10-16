package entities.informationflow.information;

import entities.informationflow.Information;
import model.abstractions.Identity;

public class SizeOfInformation extends Information {
	
    protected SizeOfInformation(Identity id, String name) {
		super(id, name);
	}

	private SizeableInformation referencedInformation;

    public SizeableInformation getReferencedInformation() {
        return referencedInformation;
    }
}
