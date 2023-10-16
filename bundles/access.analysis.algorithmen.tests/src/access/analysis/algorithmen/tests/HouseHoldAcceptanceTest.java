package access.analysis.algorithmen.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import access.analysis.algorithmen.AccessAnalyser;
import access.analysis.algorithmen.abstractions.AlgorithmConfiguration;
import access.analysis.algorithmen.abstractions.AnalysisResult;
import access.analysis.algorithmen.abstractions.IAccessAnalyser;
import access.analysis.algorithmen.abstractions.SecurityThreat;

class HouseHoldAcceptanceTest {

	private IAccessAnalyser analyser = new AccessAnalyser(AlgorithmConfiguration.GetKramerPaperStyle());
	
	@Test
	void household() {
		var model = HouseholdModel.create();
		
		var expectedAnalysisResult = getExpectedHouseholdAnalysisResult(model);
		
		var actualAnalysisResult = analyser.Analyse(model.environmentModel);
		
		var expectedThreats = expectedAnalysisResult.getSecurityThreats().toList();
		var actualThreads = actualAnalysisResult.getSecurityThreats().toList();
		
		for (SecurityThreat actualThreat : actualThreads) {
			var found = expectedThreats.stream().anyMatch(threat -> 
				threat.getAccessedDataSet().equals(actualThreat.getAccessedDataSet()) &&
				threat.getCorrespondingAdversary().equals(actualThreat.getCorrespondingAdversary())
			);
			
			if(!found) {
				fail("found unexpected threat: " + actualThreat.toString());
			}
		}
		
		for (SecurityThreat expectedThreat : expectedThreats) {
			var found = actualThreads.stream().anyMatch(threat -> 
				threat.getAccessedDataSet().equals(expectedThreat.getAccessedDataSet()) &&
				threat.getCorrespondingAdversary().equals(expectedThreat.getCorrespondingAdversary())
			);
			
			if(!found) {
				fail(" could not find a threat: " + expectedThreat.toString());
			}
		}
	}
	
	private static AnalysisResult getExpectedHouseholdAnalysisResult(HouseholdModel householdModel) {
		var threats = Arrays.asList(
			SecurityThreat.CreateWithoutTraceFrom(
					householdModel.guest, householdModel.billingdata),
			SecurityThreat.CreateWithoutTraceFrom(
					householdModel.passerBy, householdModel.consumptionData),
			SecurityThreat.CreateWithoutTraceFrom(
					householdModel.passerBy, householdModel.billingdata)
		);
		
		return new AnalysisResult(threats);
	}
	
}
