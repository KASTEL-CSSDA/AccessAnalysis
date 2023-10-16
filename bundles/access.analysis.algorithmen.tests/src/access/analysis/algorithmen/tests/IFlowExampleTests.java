package access.analysis.algorithmen.tests;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import access.analysis.algorithmen.AccessabilityScanner;
import access.analysis.algorithmen.abstractions.AccessabilityResult;
import access.analysis.algorithmen.abstractions.AlgorithmConfiguration;
import access.analysis.algorithmen.abstractions.IModelProvider;
import access.analysis.io.EnvironmentModelProvider;
import access.analysis.io.JsonSerializer;
import model.Adversary;
import model.EnrichedResourceContainer;
import model.EnvironmentModel;

public class IFlowExampleTests {
	private final String modelPath = "models\\iflow.json";
	private final EnvironmentModel model;
	
	public IFlowExampleTests() {
		model = loadModel();
	}
	
	private EnvironmentModel loadModel() {
		IModelProvider modelprovider = new EnvironmentModelProvider(
			new JsonSerializer(),
			Path.of(modelPath)
		);
		return modelprovider.getEnvironmentModel();
	}
	
	private Adversary getUser() {
		return model.getAdversaries().filter(adv -> adv.getName().equals("User")).findFirst().get();
	}
	
	@Test
	void PasserBy_getInterceptableInformation_ConsumptionDataAndBillingData() {
		
		var scanner = new AccessabilityScanner(model, AlgorithmConfiguration.GetKramerPaperStyle());
		var mobile = model.getLocations().flatMap(loc -> loc.getResourceContainers()).filter(cont -> cont.getName().equals("MobilePhone")).findAny().get();
		
		List<EnrichedResourceContainer> accessableContainers = scanner.getAccessableContainers(getUser()).
			map(AccessabilityResult::getAccessedElement).distinct().toList();
		
		TestUtil.assertListEqualsUnordered("", List.of(mobile), accessableContainers);
	}
}
