package entities.informationflow.information;

import entities.informationflow.Information;
import model.abstractions.Identity;

public class CallInformation extends Information {

	protected CallInformation(Identity id) {
		super(id, "Call information");
	}
	
	public static CallInformation CreateNew() {
		return new CallInformation(Identity.CreateNew());
	}
    
}
