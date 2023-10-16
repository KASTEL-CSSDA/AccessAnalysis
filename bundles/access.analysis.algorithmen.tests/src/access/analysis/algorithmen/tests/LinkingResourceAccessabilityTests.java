package access.analysis.algorithmen.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import access.analysis.algorithmen.AccessabilityScanner;
import access.analysis.algorithmen.abstractions.AlgorithmConfiguration;
import model.LinkingResource;

public class LinkingResourceAccessabilityTests {
	
	@Test
	void household_getAccessableLinksForGuest_Wireless() {
		var model = HouseholdModel.create();
		var scanner = new AccessabilityScanner(model.environmentModel, AlgorithmConfiguration.GetKramerPaperStyle());
		var expectedAccessableContainers = getExpectedAccessableLinksForGuest(model);
		
		var actualAnalysisResult = scanner.getAccessableLinkingResources(model.guest).
				map(accessResult -> accessResult.getAccessedElement()).
				distinct().
				toList();
		
		assertEquals(expectedAccessableContainers, actualAnalysisResult);
	}

	@Test
	void household_getAccessableLinksForInhabitant_Wireless() {
		var model = HouseholdModel.create();
		var scanner = new AccessabilityScanner(model.environmentModel, AlgorithmConfiguration.GetKramerPaperStyle());
		var expectedAccessableContainers = getExpectedAccessableLinksForInhabitant(model);
		
		var actualAnalysisResult = scanner.getAccessableLinkingResources(model.inhabitant).
				map(accessResult -> accessResult.getAccessedElement()).
				distinct().
				toList();
		
		assertEquals(expectedAccessableContainers, actualAnalysisResult);
	}
	
	@Test
	void household_getAccessableLinksForPasserBy_Wireless() {
		var model = HouseholdModel.create();
		var scanner = new AccessabilityScanner(model.environmentModel, AlgorithmConfiguration.GetKramerPaperStyle());
		var expectedAccessableContainers = getExpectedAccessableLinksForPasserBy(model);
		
		var actualAnalysisResult = scanner.getAccessableLinkingResources(model.passerBy).
				map(accessResult -> accessResult.getAccessedElement()).
				distinct().
				toList();
		
		assertEquals(expectedAccessableContainers, actualAnalysisResult);
	}
	
	private List<LinkingResource> getExpectedAccessableLinksForGuest(HouseholdModel model) {
		return Arrays.asList(model.wireless);
	}

	private List<LinkingResource> getExpectedAccessableLinksForInhabitant(HouseholdModel model) {
		return Arrays.asList(model.wireless);
	}
	
	private List<LinkingResource> getExpectedAccessableLinksForPasserBy(HouseholdModel model) {
		return Arrays.asList(model.wireless);
	}
}
