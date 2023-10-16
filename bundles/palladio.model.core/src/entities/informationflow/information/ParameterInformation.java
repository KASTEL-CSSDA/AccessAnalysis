package entities.informationflow.information;

import entities.informationflow.Information;
import entities.informationflow.InformationFlowParameter;
import model.abstractions.Identity;

public class ParameterInformation extends Information implements SizeableInformation {
    
	public static ParameterInformation CreateNew(String name) {
		return new ParameterInformation(Identity.CreateNew(), name);
	}
	
	private ParameterInformation(Identity id, String name) {
		super(id, name);
	}
	
	private ParameterInformation() {}

	private InformationFlowParameter referencedInformationFlowParameter;

    public InformationFlowParameter getReferencedInformationFlowParameter() {
        return referencedInformationFlowParameter;
    }
}
