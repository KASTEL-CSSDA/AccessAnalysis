package access.analysis.io;

import java.util.List;
import java.util.stream.Stream;

import access.analysis.algorithmen.abstractions.AnalysisResult;

public class FlattenedAnalysisResult {

	private List<FlattenedSecurityThreat> securityThreats;
	
	public FlattenedAnalysisResult(AnalysisResult result) {
		securityThreats = result.getSecurityThreats().map(threat -> new FlattenedSecurityThreat(threat)).toList();
	}

	public Stream<FlattenedSecurityThreat> getSecurityThreats() {
		return securityThreats.stream();
	}
	
}
