package access.analysis.io;

import java.util.List;
import java.util.stream.Stream;

import access.analysis.algorithmen.abstractions.SecurityThreat;

public class FlattenedSecurityThreat {

	private String adversaryName;
	private String accessedDataSetName;
	private List<String> trace;
	
	public FlattenedSecurityThreat(SecurityThreat threat) {
		this.adversaryName = threat.getCorrespondingAdversary().getName();
		this.accessedDataSetName = threat.getAccessedDataSet().getName();
		this.trace = threat.getTrace().getAsString();
	}

	public Stream<String> getTrace() {
		return trace.stream();
	}

	public String getAccessedDataSetName() {
		return accessedDataSetName;
	}

	public String getAdversaryName() {
		return adversaryName;
	}

}
