package entities;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import entities.informationflow.InterfaceInformationFlow;
import model.abstractions.Identifyable;
import model.abstractions.Identity;
import model.abstractions.Nameable;

public class Interface implements Identifyable, Nameable {
    private List<OperationSignature> operationSignatures;
    private List<InterfaceInformationFlow> informationFlows;
    private Identity id;
    private String name;

    public static Interface CreateNew(String name, List<OperationSignature> operationSignatures,
    		List<InterfaceInformationFlow> informationFlows) {
    	return new Interface(operationSignatures, informationFlows, Identity.CreateNew(), name);
    }
    
    public Interface(List<OperationSignature> operationSignatures, List<InterfaceInformationFlow> informationFlows,
			Identity id, String name) {
		super();
		this.operationSignatures = operationSignatures;
		this.informationFlows = informationFlows;
		this.id = id;
		this.name = name;
	}
	@Override
    public Identity getId() {
        return id;
    }
    @Override
    public String getName() {
        return name;
    }
    
    public Stream<OperationSignature> getOperationSignatures() {
        return operationSignatures.stream();
    }
    
    public Stream<InterfaceInformationFlow> getInformationFlows() {
        return informationFlows.stream();
    }
    
    @Override
    public String toString() {
        return name + " funcs:" + String.join(",", operationSignatures.stream().map(OperationSignature::getName).collect(Collectors.toList()));
    }
}
