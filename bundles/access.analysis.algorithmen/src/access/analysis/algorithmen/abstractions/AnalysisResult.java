package access.analysis.algorithmen.abstractions;

import java.util.List;
import java.util.stream.Stream;

public class AnalysisResult {
	
	private List<SecurityThreat> securityThreats;
	
	public AnalysisResult(List<SecurityThreat> securityThreats) {
		super();
		this.securityThreats = securityThreats;
	}

	public Stream<SecurityThreat> getSecurityThreats() {
		return securityThreats.stream();
	}
	
	public boolean hasSecurityThreats() {
		return !securityThreats.isEmpty();
	}
}
