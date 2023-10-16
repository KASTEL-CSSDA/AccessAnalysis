package edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.modelversioning.emfprofileapplication.ProfileApplication;
import org.palladiosimulator.pcm.allocation.impl.AllocationImpl;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.impl.ResourceEnvironmentImpl;
import org.palladiosimulator.pcm.system.impl.SystemImpl;

import edu.kit.kastel.scbs.confidentiality.ConfidentialitySpecification;
import edu.kit.kastel.scbs.confidentiality.adversary.impl.AdversariesImpl;

public class PCMModels {
	private static final String PCM_REPOSITORY_FILE_ENDING = "repository";
	private static final String ACCESS_ANALYSIS_CONFIDENTIALITY_FILE_ENDING = "confidentiality";
	private static final String ACCESS_ANALYSIS_ADVERSARY_FILE_ENDING = "adversary";
	private static final String PCM_ALLOCATION_FILE_ENDING = "allocation";
	private static final String PCM_RESOURCE_ENVIRONMENT_FILE_ENDING = "resourceenvironment";
	private static final String PCM_SYSTEM_FILE_ENDING = "system";
	
	private final Repository repository;
	private final ProfileApplication profile;
	private final ConfidentialitySpecification confidentiality;
	private final AdversariesImpl adversaries;
	private final AllocationImpl allocations;
	private final ResourceEnvironmentImpl resourceEnv;
	private final ProfileApplication resourceEnvProfile;
	private final SystemImpl system;
	
	
	
	public PCMModels(Repository repository, ProfileApplication profile, ConfidentialitySpecification confidentiality, AdversariesImpl adversaries, SystemImpl system, ResourceEnvironmentImpl resourceEnv, AllocationImpl allocations, ProfileApplication resourceEnvProfile) {
		this.repository = repository;
		this.profile = profile;
		this.confidentiality = confidentiality;
		this.adversaries = adversaries;
		this.allocations = allocations;
		this.resourceEnv = resourceEnv;
		this.resourceEnvProfile = resourceEnvProfile;
		this.system = system;
	}
	
	public AdversariesImpl getAdversaries() {
		return adversaries;
	}

	public Repository getRepository() {
		return repository;
	}

	public ProfileApplication getProfile() {
		return profile;
	}

	public ConfidentialitySpecification getConfidentiality() {
		return confidentiality;
	}
	
	public AllocationImpl getAllocations() {
		return allocations;
	}

	public ResourceEnvironmentImpl getResourceEnv() {
		return resourceEnv;
	}

	public SystemImpl getSystem() {
		return system;
	}
	
	public static PCMModels generateFromFiles(Collection<IFile> files) {
		IPath repositoryPath = null;
		IPath confidentialityPath = null;
		IPath adversaryPath = null;
		IPath allocationPath = null;
		IPath resourceEnvPath = null;
		IPath systemPath = null;
		
		for (IFile file : files) {
			switch (file.getFileExtension()) {
			case PCM_REPOSITORY_FILE_ENDING:
				repositoryPath = file.getLocation();
				break;
			case ACCESS_ANALYSIS_CONFIDENTIALITY_FILE_ENDING:
				confidentialityPath = file.getLocation();
				break;
			case ACCESS_ANALYSIS_ADVERSARY_FILE_ENDING:
				adversaryPath = file.getLocation();
				break;
			case PCM_ALLOCATION_FILE_ENDING:
				allocationPath = file.getLocation();
			case PCM_RESOURCE_ENVIRONMENT_FILE_ENDING:
				resourceEnvPath = file.getLocation();
			case PCM_SYSTEM_FILE_ENDING:
				systemPath = file.getLocation();
			}
		}

		ResourceSetImpl resSet = new ResourceSetImpl();

		URI repositoryUri = URI.createFileURI(repositoryPath.toString());
		URI confidentialityUri = URI.createFileURI(confidentialityPath.toString());
		URI adversaryUri = URI.createFileURI(adversaryPath.toString());
		URI allocationURI = URI.createFileURI(allocationPath.toString());
		URI resourceEnvURI = URI.createFileURI(resourceEnvPath.toString());
		URI systemURI = URI.createFileURI(systemPath.toString());
		
		Resource resourceRepository = resSet.getResource(repositoryUri, true);
		Resource resourceConfidentiality = resSet.getResource(confidentialityUri, true);
		Resource resourceAdversary = resSet.getResource(adversaryUri, true);
		Resource resourceAllocation = resSet.getResource(allocationURI, true);
		Resource resourceResourceEnv = resSet.getResource(resourceEnvURI, true);
		Resource resourceSystem = resSet.getResource(systemURI, true);
		
		EcoreUtil.resolveAll(resourceRepository);
		EcoreUtil.resolveAll(resourceConfidentiality);
		EcoreUtil.resolveAll(resourceAdversary);
		EcoreUtil.resolveAll(resourceAllocation);
		EcoreUtil.resolveAll(resourceResourceEnv);
		EcoreUtil.resolveAll(resourceSystem);

	
			try {
				resourceRepository.load(null);
				resourceConfidentiality.load(null);
				resourceAdversary.load(null);
				resourceAllocation.load(null);
				resourceResourceEnv.load(null);
				resourceSystem.load(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
	
		Repository repository = (Repository) resourceRepository.getContents().get(0);
		ProfileApplication repoProfile = (ProfileApplication) resourceRepository.getContents().get(1);
		ConfidentialitySpecification confidentiality = (ConfidentialitySpecification) resourceConfidentiality
				.getContents().get(0);
		AdversariesImpl adversaries = (AdversariesImpl) resourceAdversary.getContents().get(0);
		AllocationImpl allocations = (AllocationImpl) resourceAllocation.getContents().get(0);
		ResourceEnvironmentImpl resourceEnv = (ResourceEnvironmentImpl) resourceResourceEnv.getContents().get(0);
		ProfileApplication resourceEnvProfile = (ProfileApplication) resourceResourceEnv.getContents().get(1);
		SystemImpl system = (SystemImpl) resourceSystem.getContents().get(0);
		
		return new PCMModels(repository, repoProfile, confidentiality, adversaries, system, resourceEnv, allocations, resourceEnvProfile);
	}

	public ProfileApplication getResourceEnvProfile() {
		return resourceEnvProfile;
	}
}
