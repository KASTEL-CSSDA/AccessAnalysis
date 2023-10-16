package edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.modelextractor

import java.util.Collection

import org.palladiosimulator.pcm.repository.Repository
import edu.kit.kastel.scbs.confidentiality.ConfidentialitySpecification
import org.palladiosimulator.pcm.repository.OperationInterface
import java.util.ArrayList
import edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.repository.PCMInterface
import edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.repository.Component
import edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.repository.OperationSignature
import edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.repository.Parameter
import org.modelversioning.emfprofileapplication.ProfileApplication
import edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.accessanalysis.DataSet
import java.util.HashMap
import java.util.Set
import java.util.HashSet
import edu.kit.kastel.scbs.confidentiality.repository.ParametersAndDataPair
import java.util.Map
import org.palladiosimulator.pcm.repository.BasicComponent

class PCM2ModelExtractor {

	private Collection<PCMInterface> interfaces;
	private Collection<Component> components;
	private Collection<DataSet> dataSets;

	val ACCESS_ANALYSIS_RETURN_TOKEN = "\\return"
	val ACCESS_ANALYSIS_CALL_TOKEN = "\\call"
	val ACCESS_ANALYSIS_SIZEOF_TOKEN = "\\sizeOf"

	public static val MODEL_RETURN_TOKEN = "return"
	public static val MODEL_CALL_TOKEN = "call"
	public static val MODEL_SIZEOF_TOKEN = "sizeOf"

	new() {
		interfaces = new ArrayList<PCMInterface>();
		components = new ArrayList<Component>();
		dataSets = new ArrayList<DataSet>();
	}

	def Collection<Component> getExtractedComponents() {
		return components;
	}

	public def extractRepository(Repository repository, ConfidentialitySpecification spec,
		ProfileApplication profileApplication) {

		spec.dataIdentifier.filter(DataSet).forEach[dataSet|dataSets.add(new DataSet(dataSet.name, dataSet.id))];

		ExtractorUtils.getOperationInterfaces(repository).forEach [ iface |
			interfaces.add(extractInterfaceModel(iface, profileApplication))
		];
		

		ExtractorUtils.getBasicComponents(repository).forEach[component | components.add(extractComponentModel(component))];
		
		
	}
	
	def Component extractComponentModel(BasicComponent component) {
		val name = component.entityName;
		val id = component.id;
		
		val componentModel = new Component(name,id);
		
		ExtractorUtils.getOperationProvidedRoles(component).forEach[providedRole | componentModel.addProvidedInterface(resolveInterface(providedRole.providedInterface__OperationProvidedRole))]
		
		return componentModel;
	}
	
	def PCMInterface resolveInterface(OperationInterface interface1) {
		return interfaces.findFirst[iface | iface.id.equals(interface1.id)];
	}

	private def extractInterfaceModel(OperationInterface operationInterface, ProfileApplication profileApplication) {

		val name = operationInterface.entityName;
		val id = operationInterface.id;
		val operationSignatures = new ArrayList<OperationSignature>();

		operationInterface.signatures__OperationInterface.forEach [ signature |
			operationSignatures.add(extractOperationSignatureModel(signature, profileApplication))
		];

		return new PCMInterface(name, id, operationSignatures);
	}

	def OperationSignature extractOperationSignatureModel(
		org.palladiosimulator.pcm.repository.OperationSignature signature, ProfileApplication profileApplication) {
		val name = signature.entityName;
		val id = signature.id;
		val parameters = new ArrayList<Parameter>();
		val annotatedSizeOf = new HashMap<String, Set<DataSet>>();

		signature.parameters__OperationSignature.forEach[parameter|parameters.add(extractParameterModel(parameter))];

		val stereotypes = ExtractorUtils.getInformationFlowAnnotationsFor(signature, profileApplication);
		val paramAndData = ExtractorUtils.getParameterAndDataPairFrom(stereotypes);

		val paramAndDataPairAssignments = new HashMap<String, Set<DataSet>>();

		for (paramAndDataPair : paramAndData) {
			calculateDataAssignments(paramAndDataPair, paramAndDataPairAssignments)
		}

		return new OperationSignature(name, id, parameters, paramAndDataPairAssignments, annotatedSizeOf);
}

	def private calculateDataAssignments(ParametersAndDataPair pair, Map<String, Set<DataSet>> map) {
		val paramSources = pair.parameterSources;
		val c4cbseDataSets = pair.dataTargets.filter(edu.kit.kastel.scbs.confidentiality.data.DataSet).toList;

		val dataSets = getDataSets(c4cbseDataSets);

		for (paramSource : paramSources) {
			switch (paramSource) {
				case ACCESS_ANALYSIS_RETURN_TOKEN: {
					if (!map.containsKey(MODEL_RETURN_TOKEN)) {
						map.put(MODEL_RETURN_TOKEN, new HashSet<DataSet>());
					}

					map.get(MODEL_RETURN_TOKEN).addAll(dataSets);
				}
				case ACCESS_ANALYSIS_CALL_TOKEN: {
					if (!map.containsKey(MODEL_CALL_TOKEN)) {
						map.put(MODEL_CALL_TOKEN, new HashSet<DataSet>());
					}

					map.get(MODEL_CALL_TOKEN).addAll(dataSets);
				}
				case paramSource.contains(ACCESS_ANALYSIS_SIZEOF_TOKEN): {
					// TODO: Handlde SizeOF
				}
				default: {
					if (!map.containsKey(paramSource)) {
						map.put(paramSource, new HashSet<DataSet>())
					}

					map.get(paramSource).addAll(dataSets);
				}
			}
		}
	}

	def Parameter extractParameterModel(org.palladiosimulator.pcm.repository.Parameter parameter) {
		val name = parameter.parameterName;

		return new Parameter(name, new HashSet<DataSet>());
	}

	def DataSet getDataSet(edu.kit.kastel.scbs.confidentiality.data.DataSet dataSet) {
		return dataSets.findFirst[ds|ds.id.equals(dataSet.id)];
	}

	def Collection<DataSet> getDataSets(Collection<edu.kit.kastel.scbs.confidentiality.data.DataSet> dataSets) {

		val foundDataSets = new HashSet<DataSet>();

		dataSets.forEach [ ds |
			if (getDataSet(ds) !== null) {
				foundDataSets.add(getDataSet(ds))
			}
		];

		return foundDataSets;
	}

}
