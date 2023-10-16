package edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.modelextractor

import org.palladiosimulator.pcm.repository.Repository
import org.palladiosimulator.pcm.repository.BasicComponent
import java.util.Collection
import org.palladiosimulator.pcm.repository.OperationInterface
import org.modelversioning.emfprofileapplication.StereotypeApplication
import org.modelversioning.emfprofileapplication.ProfileApplication
import edu.kit.kastel.scbs.confidentiality.repository.ParametersAndDataPair
import edu.kit.ipd.sdq.commons.util.org.palladiosimulator.mdsdprofiles.api.StereotypeAPIUtil
import java.util.List
import org.palladiosimulator.pcm.repository.OperationSignature
import org.palladiosimulator.pcm.repository.OperationProvidedRole
import org.palladiosimulator.pcm.repository.RepositoryComponent

class ExtractorUtils {
	
	
	static def Collection<BasicComponent> getBasicComponents(Repository repository){
		return repository.components__Repository.filter(BasicComponent).toList;
	}
	
	static def Collection<OperationInterface> getOperationInterfaces(Repository repository){
		return repository.interfaces__Repository.filter(OperationInterface).toList;
	}
	
	static def Collection<StereotypeApplication> getInformationFlowAnnotations(ProfileApplication application){
		return application.getStereotypeApplications().filter[stereotypeApplication | stereotypeApplication.stereotype.name.equals("InformationFlow")].toList;
	}
	
	static def Collection<StereotypeApplication> getInformationFlowAnnotationsFor(OperationSignature signature, ProfileApplication application){
		val informationFlowAnnotations = getInformationFlowAnnotations(application);
		
		return informationFlowAnnotations.filter[stereotypeApplication | stereotypeApplication.appliedTo.equals(signature)].toList;
	}
	
	static def List<ParametersAndDataPair> getParameterAndDataPairFrom(Collection<StereotypeApplication> stereotypeApplications){
		return StereotypeAPIUtil.getTaggedValues(stereotypeApplications, "parametersAndDataPairs", typeof(ParametersAndDataPair));
	}
	
	static def Collection<OperationProvidedRole> getOperationProvidedRoles(RepositoryComponent component){
		return component.providedRoles_InterfaceProvidingEntity.filter(OperationProvidedRole).toList;
	}
}