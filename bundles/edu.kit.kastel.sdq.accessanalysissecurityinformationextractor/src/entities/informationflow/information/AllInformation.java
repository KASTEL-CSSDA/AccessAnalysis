package entities.informationflow.information;

import entities.informationflow.Information;
import model.abstractions.Identity;

public class AllInformation extends Information {

	public static AllInformation CreateNew() {
		return new AllInformation(Identity.CreateNew());
	}
	
	private AllInformation(Identity id) {
		super(id, "All information");
	}
    
}
