package access.analysis.algorithmen.abstractions;

public class AlgorithmConfiguration {
	/*
	 * This is the style of the paper where accessing information does not result in a threat,
	 * if this information is tagged by one dataSet, that the adversary may know, even if there
	 * are others that they are not allowed to know.
	 */
	public final boolean ITS_OKAY_IF_ADVERSARY_MAY_KNOW_ONE;
	
	/*
	 * If set to true threats with the same trace to information will be grouped so that all dataSets that
	 * the information is contained in are all returned as one threat. 
	 */
	public final boolean GROUP_BY_TRACE_AND_INFORMATION;
	
	/*
	 * If information is not tagged by any dataSet it is tagged with "unknownDataSet" and if an adversary accesses it
	 * it results in a threat. If this option is set to false, accessing unknownDataSets don't result in threats.
	 */
	public final boolean UNKNOWN_DATASET_IS_ACCESS_VIOLATION;

	public AlgorithmConfiguration(
			boolean iTS_OKAY_IF_ADVERSARY_MAY_KNOW_ONE,
			boolean gROUP_BY_TRACE_AND_INFORMATION,
			boolean uNKNOWN_DATASET_IS_ACCESS_VIOLATION
	) {
		super();
		ITS_OKAY_IF_ADVERSARY_MAY_KNOW_ONE = iTS_OKAY_IF_ADVERSARY_MAY_KNOW_ONE;
		GROUP_BY_TRACE_AND_INFORMATION = gROUP_BY_TRACE_AND_INFORMATION;
		UNKNOWN_DATASET_IS_ACCESS_VIOLATION = uNKNOWN_DATASET_IS_ACCESS_VIOLATION;
	}
	
	public static AlgorithmConfiguration GetKramerPaperStyle() {
		return new AlgorithmConfiguration(true, false, true);
	}
	
}
