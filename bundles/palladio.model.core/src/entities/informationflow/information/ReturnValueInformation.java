package entities.informationflow.information;

import entities.informationflow.Information;
import model.abstractions.Identity;

public class ReturnValueInformation extends Information implements SizeableInformation {

	public static ReturnValueInformation CreateNew() {
		return new ReturnValueInformation(Identity.CreateNew());
	}
	
	private ReturnValueInformation(Identity id) {
		super(id, "Return value information");
	}
	
	private ReturnValueInformation() {}
    
}
