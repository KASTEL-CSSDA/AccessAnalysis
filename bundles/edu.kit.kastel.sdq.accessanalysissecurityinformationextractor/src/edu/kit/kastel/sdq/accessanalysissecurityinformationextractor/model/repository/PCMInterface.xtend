package edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.repository

import java.util.Collection
import org.eclipse.xtend.lib.annotations.Data

@Data class PCMInterface {
	private final String name;
	private final String id;
	private final Collection<OperationSignature> signatures;
}
