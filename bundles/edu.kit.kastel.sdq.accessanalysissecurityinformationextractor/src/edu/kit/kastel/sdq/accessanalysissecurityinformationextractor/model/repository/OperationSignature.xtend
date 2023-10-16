package edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.repository

import org.eclipse.xtend.lib.annotations.Data
import java.util.Collection
import java.util.HashMap
import edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.accessanalysis.DataSet
import java.util.Set
import java.util.Map
import java.util.HashSet
import edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.modelextractor.PCM2ModelExtractor

@Data class OperationSignature {

	private final String name;
	private final String id;
	private final Collection<Parameter> parameters
	private final Set<DataSet> callDataSets;
	private final Set<DataSet> returnDataSets;
	private final Map<String, Set<DataSet>> sizeOfDataSets;

	new(String name, String id, Collection<Parameter> parameters, Map<String, Set<DataSet>> dataSetAssignments, Map<String, Set<DataSet>> sizeOfDataSets) {
		this.name = name;
		this.id = id;
		this.parameters = parameters;
		this.callDataSets = new HashSet<DataSet>();
		this.returnDataSets = new HashSet<DataSet>();
		this.sizeOfDataSets = new HashMap<String, Set<DataSet>>();

		for (entry : dataSetAssignments.entrySet) {
			switch (entry.key) {
				case PCM2ModelExtractor.MODEL_CALL_TOKEN: {
					this.callDataSets.addAll(entry.value);
				}
				case PCM2ModelExtractor.MODEL_RETURN_TOKEN: {
					this.returnDataSets.addAll(entry.value);
				}
				default: {
					val parameter = parameters.findFirst[param | param.name.equals(entry.key)]
					parameter.annotatedDataSets.addAll(entry.value);
					
				}
			}
		}
	}

}
