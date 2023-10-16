package entities.informationflow;

import model.abstractions.Identifyable;
import model.abstractions.Identity;
import model.abstractions.Nameable;

public class InformationFlowParameter implements Identifyable, Nameable {
    private AbstractInformationFlow abstractInformationFlow;
    private Identity id;
    private String name;

    public InformationFlowParameter(AbstractInformationFlow abstractInformationFlow, Identity id, String name) {
		super();
		this.abstractInformationFlow = abstractInformationFlow;
		this.id = id;
		this.name = name;
	}

	@SuppressWarnings("unused")
    private InformationFlowParameter() {}
    
    @Override
    public Identity getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public AbstractInformationFlow getAbstractInformationFlow() {
        return abstractInformationFlow;
    }
}
