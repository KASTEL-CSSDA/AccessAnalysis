package access.analysis.algorithmen.abstractions;

import model.Adversary;
import model.DataSet;

public class SecurityThreat {
	
	public static SecurityThreat CreateWithoutTraceFrom(Adversary adversary, DataSet accessedDataSet) {
		return new SecurityThreat(adversary, null, accessedDataSet);
	}
	
	public SecurityThreat(Adversary adversary, AccessTrace trace, DataSet accessedDataSet) {
		super();
		this.adversary = adversary;
		this.trace = trace;
		this.accessedDataSet = accessedDataSet;
	}

	private Adversary adversary;
	private AccessTrace trace;
	private DataSet accessedDataSet;
	
	public AccessTrace getTrace() {
		return trace;
	}
	
	public Adversary getCorrespondingAdversary() {
		return adversary;
	}
	
	public DataSet getAccessedDataSet() {
		return accessedDataSet;
	}
	
	@Override
	public String toString() {
		return adversary.getName() +
				" accessed " +
				accessedDataSet.getName() +
				" following " +
				trace.toString();
	}
}
