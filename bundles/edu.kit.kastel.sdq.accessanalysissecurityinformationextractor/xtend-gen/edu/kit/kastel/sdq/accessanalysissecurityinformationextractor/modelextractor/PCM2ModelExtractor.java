package edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.modelextractor;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import edu.kit.kastel.scbs.confidentiality.ConfidentialitySpecification;
import edu.kit.kastel.scbs.confidentiality.repository.ParametersAndDataPair;
import edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.accessanalysis.DataSet;
import edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.repository.Component;
import edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.repository.OperationSignature;
import edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.repository.PCMInterface;
import edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.repository.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.modelversioning.emfprofileapplication.ProfileApplication;
import org.modelversioning.emfprofileapplication.StereotypeApplication;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.Repository;

@SuppressWarnings("all")
public class PCM2ModelExtractor {
  private Collection<PCMInterface> interfaces;

  private Collection<Component> components;

  private Collection<DataSet> dataSets;

  private final String ACCESS_ANALYSIS_RETURN_TOKEN = "\\return";

  private final String ACCESS_ANALYSIS_CALL_TOKEN = "\\call";

  private final String ACCESS_ANALYSIS_SIZEOF_TOKEN = "\\sizeOf";

  public static final String MODEL_RETURN_TOKEN = "return";

  public static final String MODEL_CALL_TOKEN = "call";

  public static final String MODEL_SIZEOF_TOKEN = "sizeOf";

  public PCM2ModelExtractor() {
    ArrayList<PCMInterface> _arrayList = new ArrayList<PCMInterface>();
    this.interfaces = _arrayList;
    ArrayList<Component> _arrayList_1 = new ArrayList<Component>();
    this.components = _arrayList_1;
    ArrayList<DataSet> _arrayList_2 = new ArrayList<DataSet>();
    this.dataSets = _arrayList_2;
  }

  public Collection<Component> getExtractedComponents() {
    return this.components;
  }

  public void extractRepository(final Repository repository, final ConfidentialitySpecification spec, final ProfileApplication profileApplication) {
    final Consumer<DataSet> _function = new Consumer<DataSet>() {
      public void accept(final DataSet dataSet) {
        String _name = dataSet.getName();
        String _id = dataSet.getId();
        DataSet _dataSet = new DataSet(_name, _id);
        PCM2ModelExtractor.this.dataSets.add(_dataSet);
      }
    };
    Iterables.<DataSet>filter(spec.getDataIdentifier(), DataSet.class).forEach(_function);
    final Consumer<OperationInterface> _function_1 = new Consumer<OperationInterface>() {
      public void accept(final OperationInterface iface) {
        PCM2ModelExtractor.this.interfaces.add(PCM2ModelExtractor.this.extractInterfaceModel(iface, profileApplication));
      }
    };
    ExtractorUtils.getOperationInterfaces(repository).forEach(_function_1);
    final Consumer<BasicComponent> _function_2 = new Consumer<BasicComponent>() {
      public void accept(final BasicComponent component) {
        PCM2ModelExtractor.this.components.add(PCM2ModelExtractor.this.extractComponentModel(component));
      }
    };
    ExtractorUtils.getBasicComponents(repository).forEach(_function_2);
  }

  public Component extractComponentModel(final BasicComponent component) {
    final String name = component.getEntityName();
    final String id = component.getId();
    final Component componentModel = new Component(name, id);
    final Consumer<OperationProvidedRole> _function = new Consumer<OperationProvidedRole>() {
      public void accept(final OperationProvidedRole providedRole) {
        componentModel.addProvidedInterface(PCM2ModelExtractor.this.resolveInterface(providedRole.getProvidedInterface__OperationProvidedRole()));
      }
    };
    ExtractorUtils.getOperationProvidedRoles(component).forEach(_function);
    return componentModel;
  }

  public PCMInterface resolveInterface(final OperationInterface interface1) {
    final Function1<PCMInterface, Boolean> _function = new Function1<PCMInterface, Boolean>() {
      public Boolean apply(final PCMInterface iface) {
        return Boolean.valueOf(iface.getId().equals(interface1.getId()));
      }
    };
    return IterableExtensions.<PCMInterface>findFirst(this.interfaces, _function);
  }

  private PCMInterface extractInterfaceModel(final OperationInterface operationInterface, final ProfileApplication profileApplication) {
    final String name = operationInterface.getEntityName();
    final String id = operationInterface.getId();
    final ArrayList<OperationSignature> operationSignatures = new ArrayList<OperationSignature>();
    final Consumer<org.palladiosimulator.pcm.repository.OperationSignature> _function = new Consumer<org.palladiosimulator.pcm.repository.OperationSignature>() {
      public void accept(final org.palladiosimulator.pcm.repository.OperationSignature signature) {
        operationSignatures.add(PCM2ModelExtractor.this.extractOperationSignatureModel(signature, profileApplication));
      }
    };
    operationInterface.getSignatures__OperationInterface().forEach(_function);
    return new PCMInterface(name, id, operationSignatures);
  }

  public OperationSignature extractOperationSignatureModel(final org.palladiosimulator.pcm.repository.OperationSignature signature, final ProfileApplication profileApplication) {
    final String name = signature.getEntityName();
    final String id = signature.getId();
    final ArrayList<Parameter> parameters = new ArrayList<Parameter>();
    final HashMap<String, Set<DataSet>> annotatedSizeOf = new HashMap<String, Set<DataSet>>();
    final Consumer<org.palladiosimulator.pcm.repository.Parameter> _function = new Consumer<org.palladiosimulator.pcm.repository.Parameter>() {
      public void accept(final org.palladiosimulator.pcm.repository.Parameter parameter) {
        parameters.add(PCM2ModelExtractor.this.extractParameterModel(parameter));
      }
    };
    signature.getParameters__OperationSignature().forEach(_function);
    final Collection<StereotypeApplication> stereotypes = ExtractorUtils.getInformationFlowAnnotationsFor(signature, profileApplication);
    final List<ParametersAndDataPair> paramAndData = ExtractorUtils.getParameterAndDataPairFrom(stereotypes);
    final HashMap<String, Set<DataSet>> paramAndDataPairAssignments = new HashMap<String, Set<DataSet>>();
    for (final ParametersAndDataPair paramAndDataPair : paramAndData) {
      this.calculateDataAssignments(paramAndDataPair, paramAndDataPairAssignments);
    }
    return new OperationSignature(name, id, parameters, paramAndDataPairAssignments, annotatedSizeOf);
  }

  private void calculateDataAssignments(final ParametersAndDataPair pair, final Map<String, Set<DataSet>> map) {
    final EList<String> paramSources = pair.getParameterSources();
    final List<edu.kit.kastel.scbs.confidentiality.data.DataSet> c4cbseDataSets = IterableExtensions.<edu.kit.kastel.scbs.confidentiality.data.DataSet>toList(Iterables.<edu.kit.kastel.scbs.confidentiality.data.DataSet>filter(pair.getDataTargets(), edu.kit.kastel.scbs.confidentiality.data.DataSet.class));
    final Collection<DataSet> dataSets = this.getDataSets(c4cbseDataSets);
    for (final String paramSource : paramSources) {
      boolean _matched = false;
      if (Objects.equal(paramSource, this.ACCESS_ANALYSIS_RETURN_TOKEN)) {
        _matched=true;
        boolean _containsKey = map.containsKey(PCM2ModelExtractor.MODEL_RETURN_TOKEN);
        boolean _not = (!_containsKey);
        if (_not) {
          HashSet<DataSet> _hashSet = new HashSet<DataSet>();
          map.put(PCM2ModelExtractor.MODEL_RETURN_TOKEN, _hashSet);
        }
        map.get(PCM2ModelExtractor.MODEL_RETURN_TOKEN).addAll(dataSets);
      }
      if (!_matched) {
        if (Objects.equal(paramSource, this.ACCESS_ANALYSIS_CALL_TOKEN)) {
          _matched=true;
          boolean _containsKey_1 = map.containsKey(PCM2ModelExtractor.MODEL_CALL_TOKEN);
          boolean _not_1 = (!_containsKey_1);
          if (_not_1) {
            HashSet<DataSet> _hashSet_1 = new HashSet<DataSet>();
            map.put(PCM2ModelExtractor.MODEL_CALL_TOKEN, _hashSet_1);
          }
          map.get(PCM2ModelExtractor.MODEL_CALL_TOKEN).addAll(dataSets);
        }
      }
      if (!_matched) {
        boolean _contains = paramSource.contains(this.ACCESS_ANALYSIS_SIZEOF_TOKEN);
        if (_contains) {
          _matched=true;
        }
      }
      if (!_matched) {
        {
          boolean _containsKey_2 = map.containsKey(paramSource);
          boolean _not_2 = (!_containsKey_2);
          if (_not_2) {
            HashSet<DataSet> _hashSet_2 = new HashSet<DataSet>();
            map.put(paramSource, _hashSet_2);
          }
          map.get(paramSource).addAll(dataSets);
        }
      }
    }
  }

  public Parameter extractParameterModel(final org.palladiosimulator.pcm.repository.Parameter parameter) {
    final String name = parameter.getParameterName();
    HashSet<DataSet> _hashSet = new HashSet<DataSet>();
    return new Parameter(name, _hashSet);
  }

  public DataSet getDataSet(final edu.kit.kastel.scbs.confidentiality.data.DataSet dataSet) {
    final Function1<DataSet, Boolean> _function = new Function1<DataSet, Boolean>() {
      public Boolean apply(final DataSet ds) {
        return Boolean.valueOf(ds.getId().equals(dataSet.getId()));
      }
    };
    return IterableExtensions.<DataSet>findFirst(this.dataSets, _function);
  }

  public Collection<DataSet> getDataSets(final Collection<edu.kit.kastel.scbs.confidentiality.data.DataSet> dataSets) {
    final HashSet<DataSet> foundDataSets = new HashSet<DataSet>();
    final Consumer<edu.kit.kastel.scbs.confidentiality.data.DataSet> _function = new Consumer<edu.kit.kastel.scbs.confidentiality.data.DataSet>() {
      public void accept(final edu.kit.kastel.scbs.confidentiality.data.DataSet ds) {
        DataSet _dataSet = PCM2ModelExtractor.this.getDataSet(ds);
        boolean _tripleNotEquals = (_dataSet != null);
        if (_tripleNotEquals) {
          foundDataSets.add(PCM2ModelExtractor.this.getDataSet(ds));
        }
      }
    };
    dataSets.forEach(_function);
    return foundDataSets;
  }
}
