package access.analysis.algorithmen.tests;

import static org.junit.Assert.*;

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
import model.DataSet;
import model.EnvironmentModel;
import model.LinkingResource;

public class PaperExampleTests {
	private final String modelPath = "models\\paperexample.json";
	private final EnvironmentModel model;
	
	public PaperExampleTests() {
		model = loadModel();
	}
	
	private EnvironmentModel loadModel() {
		IModelProvider modelprovider = new EnvironmentModelProvider(
			new JsonSerializer(),
			Path.of(modelPath)
		);
		return modelprovider.getEnvironmentModel();
	}
	
	private Adversary getPasserBy() {
		return model.getAdversaries().filter(adv -> adv.getName().equals("passer-by-adversary")).findFirst().get();
	}
	
	@Test
	void EnergyVisualization_getDataSetFrom_3Paths() {
		var scanner = new AccessabilityScanner(model, AlgorithmConfiguration.GetKramerPaperStyle());
		var energyVisualisation = model.getLocations().
			flatMap(loc -> loc.getResourceContainers()).
			flatMap(cont -> cont.getAssemblyContexts()).
			flatMap(assembly -> assembly.getRealisingComponent().getProvidedInterfaces()).
			filter(interf -> interf.getName().equals("EnergyVisualization")).findAny().get();
		
		var actual = scanner.getDataSetFrom(AccessabilityResult.getNew(energyVisualisation, "")).toList();
		
		assertEquals("return, call, sizeof(return)", 3, actual.size());
	}
	
	@Test
	void PasserBy_getInterceptableInformation_ConsumptionDataAndBillingData() {
		
		var scanner = new AccessabilityScanner(model, AlgorithmConfiguration.GetKramerPaperStyle());
		var consumptionData = model.getDataSets().filter(ds -> ds.getName().equals("consumptiondata")).findFirst().get();
		var billingData = model.getDataSets().filter(ds -> ds.getName().equals("billingdata")).findFirst().get();
		
		List<DataSet> accessableContainers = scanner.getInterceptableInformation(getPasserBy()).
				map(AccessabilityResult::getAccessedElement).distinct().toList();

		TestUtil.assertListEqualsUnordered("Transfered without encryption or protection", List.of(consumptionData, billingData, DataSet.GetUnknownDataSet()), accessableContainers);
	}
	
	@Test
	void PasserBy_getAccessableLinkingResources_Wireless() {
		
		var scanner = new AccessabilityScanner(model, AlgorithmConfiguration.GetKramerPaperStyle());
		var wireless = model.getLinkingResources().findFirst().get();
		
		var accessableLiningResourcesForPasserBy = scanner.getAccessableLinkingResources(getPasserBy()).
				map(AccessabilityResult::getAccessedElement).toArray();
		
		assertArrayEquals("Wireless is reachable outsides", new LinkingResource[] {wireless}, accessableLiningResourcesForPasserBy);
	}
	
	@Test
	void PasserBy_canAccessLinkData_Wireless() {
		
		var scanner = new AccessabilityScanner(model, AlgorithmConfiguration.GetKramerPaperStyle());
		var wireless = model.getLinkingResources().findFirst().get();
		
		var passerByCanAccessWirelessIfReachable = scanner.canAccessLinkData(getPasserBy(), wireless);
		
		assertTrue("Wireless is not protected", passerByCanAccessWirelessIfReachable);
	}

}
