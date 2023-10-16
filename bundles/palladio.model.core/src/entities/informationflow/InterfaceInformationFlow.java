package entities.informationflow;

import java.util.List;

import model.abstractions.Identity;

public class InterfaceInformationFlow extends AbstractInformationFlow {

	protected InterfaceInformationFlow(List<Information> information, Identity id, String name) {
		super(information, id);
	}
	
	protected InterfaceInformationFlow() {}

}
