package access.analysis.algorithmen.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import access.analysis.algorithmen.AccessabilityScanner;
import access.analysis.algorithmen.abstractions.AlgorithmConfiguration;
import model.EnrichedResourceContainer;

public class ResourceContainerAccessabilityTests {
	
	@Test
	void household_getAccessableContainersForGuest_energyVisualisationContainer() {
		var model = HouseholdModel.create();
		var scanner = new AccessabilityScanner(model.environmentModel, AlgorithmConfiguration.GetKramerPaperStyle());
		var expectedAccessableContainers = getExpectedAccessableContainersForGuest(model);
		
		var actualAnalysisResult = scanner.getAccessableContainers(model.guest).
				map(accessResult -> accessResult.getAccessedElement()).distinct().
				toList();
		
		assertEquals(expectedAccessableContainers, actualAnalysisResult);
	}

	@Test
	void household_getAccessableContainersForInhabitant_allContainers() {
		var model = HouseholdModel.create();
		var scanner = new AccessabilityScanner(model.environmentModel, AlgorithmConfiguration.GetKramerPaperStyle());
		var expectedAccessableContainers = getExpectedAccessableContainersForInhabitant(model);
		
		var actualAnalysisResult = scanner.getAccessableContainers(model.inhabitant).
				map(accessResult -> accessResult.getAccessedElement()).distinct().
				toList();
		
		assertEquals(expectedAccessableContainers, actualAnalysisResult);
	}
	
	@Test
	void household_getAccessableContainersForPasserBy_allContainers() {
		var model = HouseholdModel.create();
		var scanner = new AccessabilityScanner(model.environmentModel, AlgorithmConfiguration.GetKramerPaperStyle());
		var expectedAccessableContainers = getExpectedAccessableContainersForPasserBy(model);
		
		var actualAnalysisResult = scanner.getAccessableContainers(model.passerBy).
				map(accessResult -> accessResult.getAccessedElement()).distinct().
				toList();
		
		assertEquals(expectedAccessableContainers, actualAnalysisResult);
	}
	
	private List<EnrichedResourceContainer> getExpectedAccessableContainersForGuest(HouseholdModel model) {
		return Arrays.asList(model.energyVisualisationContainer);
	}

	private List<EnrichedResourceContainer> getExpectedAccessableContainersForInhabitant(HouseholdModel model) {
		return Arrays.asList(model.energyVisualisationContainer);
	}
	
	private List<EnrichedResourceContainer> getExpectedAccessableContainersForPasserBy(HouseholdModel model) {
		return new ArrayList<>();
	}
	
}
