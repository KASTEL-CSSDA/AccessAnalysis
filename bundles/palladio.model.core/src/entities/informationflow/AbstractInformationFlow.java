package entities.informationflow;

import java.util.List;
import java.util.stream.Stream;

import model.abstractions.Identifyable;
import model.abstractions.Identity;


public abstract class AbstractInformationFlow implements Identifyable {
    private List<Information> information;
    private Identity id;
    
    protected AbstractInformationFlow(List<Information> information, Identity id) {
		super();
		this.information = information;
		this.id = id;
	}
    
    protected AbstractInformationFlow() {}

	@Override
    public Identity getId() {
        return id;
    }
    
    public Stream<Information> getInformation() {
        return information.stream();
    }
}
