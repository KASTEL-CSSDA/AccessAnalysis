package edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.modelextractor;

import com.google.common.collect.Iterables;
import edu.kit.ipd.sdq.commons.util.org.palladiosimulator.mdsdprofiles.api.StereotypeAPIUtil;
import edu.kit.kastel.scbs.confidentiality.repository.ParametersAndDataPair;
import java.util.Collection;
import java.util.List;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.modelversioning.emfprofileapplication.ProfileApplication;
import org.modelversioning.emfprofileapplication.StereotypeApplication;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;

@SuppressWarnings("all")
public class ExtractorUtils {
  public static Collection<BasicComponent> getBasicComponents(final Repository repository) {
    return IterableExtensions.<BasicComponent>toList(Iterables.<BasicComponent>filter(repository.getComponents__Repository(), BasicComponent.class));
  }

  public static Collection<OperationInterface> getOperationInterfaces(final Repository repository) {
    return IterableExtensions.<OperationInterface>toList(Iterables.<OperationInterface>filter(repository.getInterfaces__Repository(), OperationInterface.class));
  }

  public static Collection<StereotypeApplication> getInformationFlowAnnotations(final ProfileApplication application) {
    final Function1<StereotypeApplication, Boolean> _function = new Function1<StereotypeApplication, Boolean>() {
      public Boolean apply(final StereotypeApplication stereotypeApplication) {
        return Boolean.valueOf(stereotypeApplication.getStereotype().getName().equals("InformationFlow"));
      }
    };
    return IterableExtensions.<StereotypeApplication>toList(IterableExtensions.<StereotypeApplication>filter(application.getStereotypeApplications(), _function));
  }

  public static Collection<StereotypeApplication> getInformationFlowAnnotationsFor(final OperationSignature signature, final ProfileApplication application) {
    final Collection<StereotypeApplication> informationFlowAnnotations = ExtractorUtils.getInformationFlowAnnotations(application);
    final Function1<StereotypeApplication, Boolean> _function = new Function1<StereotypeApplication, Boolean>() {
      public Boolean apply(final StereotypeApplication stereotypeApplication) {
        return Boolean.valueOf(stereotypeApplication.getAppliedTo().equals(signature));
      }
    };
    return IterableExtensions.<StereotypeApplication>toList(IterableExtensions.<StereotypeApplication>filter(informationFlowAnnotations, _function));
  }

  public static List<ParametersAndDataPair> getParameterAndDataPairFrom(final Collection<StereotypeApplication> stereotypeApplications) {
    return StereotypeAPIUtil.<ParametersAndDataPair>getTaggedValues(stereotypeApplications, "parametersAndDataPairs", ParametersAndDataPair.class);
  }

  public static Collection<OperationProvidedRole> getOperationProvidedRoles(final RepositoryComponent component) {
    return IterableExtensions.<OperationProvidedRole>toList(Iterables.<OperationProvidedRole>filter(component.getProvidedRoles_InterfaceProvidingEntity(), OperationProvidedRole.class));
  }
}
