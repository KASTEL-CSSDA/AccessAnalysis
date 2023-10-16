package access.analysis.algorithmen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import access.analysis.algorithmen.abstractions.AccessabilityResult;
import access.analysis.algorithmen.abstractions.AlgorithmConfiguration;
import access.analysis.algorithmen.abstractions.AnalysisResult;
import access.analysis.algorithmen.abstractions.IAccessAnalyser;
import access.analysis.algorithmen.abstractions.IAccessabilityScanner;
import access.analysis.algorithmen.abstractions.SecurityThreat;
import model.Adversary;
import model.DataSet;
import model.EnvironmentModel;

public class AccessAnalyser implements IAccessAnalyser {

	private IAccessabilityScanner accessabilityScanner;
	private final AlgorithmConfiguration config;
	
	public AccessAnalyser(AlgorithmConfiguration config) {
		this.config = config;
	}
	
	@Override
	public AnalysisResult Analyse(EnvironmentModel model) {
		this.accessabilityScanner = new AccessabilityScanner(model, config);
		
		List<SecurityThreat> threats = model.getAdversaries().
				flatMap(adversary -> getThreatsFor(adversary)).
				toList();
		
		return new AnalysisResult(threats);
	}
	
	/*
	 * Returns all threats in respect to a given adversary.
	 * The actual algorithm depends on configuration.
	 */
	private Stream<SecurityThreat> getThreatsFor(Adversary adversary){
		List<AccessabilityResult<DataSet>> accesses = accessabilityScanner.getDataSetsAccessableBy(adversary).distinct().toList();

		if(config.ITS_OKAY_IF_ADVERSARY_MAY_KNOW_ONE) {
			accesses = filterOutAccessesThatAreAllowedFor_MayKnowOne(adversary, accesses);	
		}
		
		accesses = filterOutGenerallyAllowedAccesses(adversary, accesses);
		
		if(config.GROUP_BY_TRACE_AND_INFORMATION) {
			accesses = groupByTraceExceptDataSet(accesses).stream().
					map(list -> unify(list)).toList();
		}
		
		return generateThreatsFrom(adversary, accesses);
	}

	private Stream<SecurityThreat> generateThreatsFrom(Adversary adversary, List<AccessabilityResult<DataSet>> accesses) {
		return accesses.stream().map(dataSetTrace -> new SecurityThreat(adversary, dataSetTrace.getTrace(), dataSetTrace.getAccessedElement()));
	}

	private List<AccessabilityResult<DataSet>> filterOutGenerallyAllowedAccesses(
		Adversary adversary,
		List<AccessabilityResult<DataSet>> accesses
	) {
		return accesses.stream().filter(accessResult -> !isAllowedToAccess(adversary, accessResult.getAccessedElement())).toList();
	}

	private List<AccessabilityResult<DataSet>> filterOutAccessesThatAreAllowedFor_MayKnowOne(
		Adversary adversary,
		List<AccessabilityResult<DataSet>> accesses
	) {
		var allowedToKnowGroups = groupByTraceExceptDataSet(accesses).stream().
				filter(
					list -> list.size() > 1 && 
					list.stream().anyMatch(access -> isAllowedToAccess(adversary, access.getAccessedElement()))
				).toList();
		
		var allowedToKnowAccesses = allowedToKnowGroups.stream().flatMap(list -> list.stream()).toList();
		
		return accesses.stream().filter(item -> !allowedToKnowAccesses.contains(item)).toList();
	}
	
	/*
	 * This function takes a list of accesses with the same trace but different accessed datasets and returns one access that has the same trace and
	 * a message with all the datasets.
	 * Note that the accessed element is no longer set for the dataset as there would be multiple! 
	*/
	private AccessabilityResult<DataSet> unify(List<AccessabilityResult<DataSet>> accesses){
		var allNames = String.join(", ", accesses.stream().map(ds -> ds.getAccessedElement().getName()).toList());
		return new AccessabilityResult<>(accesses.get(0).getPoppedTrace(), null).
				ThenAccess(DataSet.CreateNew(allNames, null), "this (these) dataSets contain this information");
	}
	
	/*
	 * Groups the accessabilityResults so that those that have the same trace to information but for different dataSets are grouped
	 */
	private Collection<List<AccessabilityResult<DataSet>>> groupByTraceExceptDataSet(List<AccessabilityResult<DataSet>> accesses){
		var sets = new HashMap<Integer, List<AccessabilityResult<DataSet>>>();
		for(var access : accesses) {
			var key = access.getPoppedTrace().hashCode();
			sets.computeIfAbsent(key, (k) -> new ArrayList<AccessabilityResult<DataSet>>()).add(access);
		}
		return sets.values();
	}
	
	/*
	 * Checks if an adversary is allowed to access a given dataset
	 */
	private boolean isAllowedToAccess(Adversary adversary, DataSet dataSet) {
		return adversary.getAllowedToKnowDataSets().
				anyMatch(allowedDs -> allowedDs != null && allowedDs.getId().equals(dataSet.getId()));
	}
}
