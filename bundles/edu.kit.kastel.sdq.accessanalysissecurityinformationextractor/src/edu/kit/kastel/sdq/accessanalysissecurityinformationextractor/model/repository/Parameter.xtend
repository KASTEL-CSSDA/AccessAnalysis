package edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.repository

import org.eclipse.xtend.lib.annotations.Data
import edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.accessanalysis.DataSet
import java.util.Set

@Data class Parameter {
	private final String name;
	private final Set<DataSet> annotatedDataSets;
	
}
