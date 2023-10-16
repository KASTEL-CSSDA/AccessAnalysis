package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import entities.informationflow.Information;
import entities.informationflow.InformationFlowParameter;
import model.abstractions.Identifyable;
import model.abstractions.Identity;
import model.abstractions.Nameable;

public class DataSet implements Identifyable, Nameable {
    private List<Information> containedInformationFlows;
    private List<InformationFlowParameter> informationFlowParameters;
    private Identity id;
    private String name;

    public static DataSet CreateNew(String name, List<Information> containedInformationFlows) {
    	return new DataSet(containedInformationFlows, new ArrayList<>(), Identity.CreateNew(), name);
    }
    
    public static DataSet GetUnknownDataSet() {
    	return new DataSet(new ArrayList<>(), new ArrayList<>(), new Identity("unknownDataSet"), "unknownDataSet");
    }
    
    public DataSet(List<Information> containedInformationFlows,
			List<InformationFlowParameter> informationFlowParameters, Identity id, String name) {
		super();
		this.containedInformationFlows = containedInformationFlows;
		this.informationFlowParameters = informationFlowParameters;
		this.id = id;
		this.name = name;
	}
    
    @SuppressWarnings("unused")
	private DataSet() {
		
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
    	return name;
    }
    
    public Stream<InformationFlowParameter> getInformationFlowParameters() {
        return informationFlowParameters.stream();
    }

    public Stream<Information> getContainedInformation() {
        return containedInformationFlows.stream();
    }

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataSet other = (DataSet) obj;
		return Objects.equals(id, other.id);
	}
}
