package entities;

import entities.informationflow.SignatureInformationFlow;
import model.abstractions.Identifyable;
import model.abstractions.Identity;
import model.abstractions.Nameable;

public class OperationSignature implements Identifyable, Nameable {
    private Identity id;
    private String name;
    private SignatureInformationFlow informationFlow;

    public static OperationSignature CreateNew(String name, SignatureInformationFlow informationFlow) {
    	return new OperationSignature(informationFlow, Identity.CreateNew(), name);
    }
    
    public OperationSignature(SignatureInformationFlow informationFlow, Identity id, String name) {
		super();
		this.id = id;
		this.name = name;
		this.informationFlow = informationFlow;
	}
    
    public SignatureInformationFlow getInformationFlow(){
    	return informationFlow;
    }

	@Override
    public Identity getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name + " if: " + informationFlow.toString();
    }
}
