package access.analysis.algorithmen.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import access.analysis.algorithmen.AccessabilityScanner;
import access.analysis.algorithmen.abstractions.AlgorithmConfiguration;
import model.DataSet;

public class DataInterceptionTests {
	
	@Test
	void household_getInterceptableInformationForGuest_ConsumptionDataAndBillingdata() {
		var model = HouseholdModel.create();
		var scanner = new AccessabilityScanner(model.environmentModel, AlgorithmConfiguration.GetKramerPaperStyle());
		var expectedAccessableDatasets = getExpectedAccessableDatasetsForGuest(model);
		
		var actualAnalysisResult = scanner.getInterceptableInformation(model.guest).
				map(accessResult -> accessResult.getAccessedElement()).
				distinct().
				toList();
		
		assertEquals(expectedAccessableDatasets, actualAnalysisResult);
	}
	
	private List<DataSet> getExpectedAccessableDatasetsForGuest(HouseholdModel model) {
		return Arrays.asList(model.consumptionData, model.billingdata);
	}
}
