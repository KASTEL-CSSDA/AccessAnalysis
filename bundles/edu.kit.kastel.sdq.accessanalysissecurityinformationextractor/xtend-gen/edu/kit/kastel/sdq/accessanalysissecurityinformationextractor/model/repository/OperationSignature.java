package edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.repository;

import com.google.common.base.Objects;
import edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.accessanalysis.DataSet;
import edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.modelextractor.PCM2ModelExtractor;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@Data
@SuppressWarnings("all")
public class OperationSignature {
  private final String name;

  private final String id;

  private final Collection<Parameter> parameters;

  private final Set<DataSet> callDataSets;

  private final Set<DataSet> returnDataSets;

  private final Map<String, Set<DataSet>> sizeOfDataSets;

  public OperationSignature(final String name, final String id, final Collection<Parameter> parameters, final Map<String, Set<DataSet>> dataSetAssignments, final Map<String, Set<DataSet>> sizeOfDataSets) {
    this.name = name;
    this.id = id;
    this.parameters = parameters;
    HashSet<DataSet> _hashSet = new HashSet<DataSet>();
    this.callDataSets = _hashSet;
    HashSet<DataSet> _hashSet_1 = new HashSet<DataSet>();
    this.returnDataSets = _hashSet_1;
    HashMap<String, Set<DataSet>> _hashMap = new HashMap<String, Set<DataSet>>();
    this.sizeOfDataSets = _hashMap;
    Set<Map.Entry<String, Set<DataSet>>> _entrySet = dataSetAssignments.entrySet();
    for (final Map.Entry<String, Set<DataSet>> entry : _entrySet) {
      String _key = entry.getKey();
      boolean _matched = false;
      if (Objects.equal(_key, PCM2ModelExtractor.MODEL_CALL_TOKEN)) {
        _matched=true;
        this.callDataSets.addAll(entry.getValue());
      }
      if (!_matched) {
        if (Objects.equal(_key, PCM2ModelExtractor.MODEL_RETURN_TOKEN)) {
          _matched=true;
          this.returnDataSets.addAll(entry.getValue());
        }
      }
      if (!_matched) {
        {
          final Function1<Parameter, Boolean> _function = new Function1<Parameter, Boolean>() {
            public Boolean apply(final Parameter param) {
              return Boolean.valueOf(param.getName().equals(entry.getKey()));
            }
          };
          final Parameter parameter = IterableExtensions.<Parameter>findFirst(parameters, _function);
          parameter.getAnnotatedDataSets().addAll(entry.getValue());
        }
      }
    }
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.id== null) ? 0 : this.id.hashCode());
    result = prime * result + ((this.parameters== null) ? 0 : this.parameters.hashCode());
    result = prime * result + ((this.callDataSets== null) ? 0 : this.callDataSets.hashCode());
    result = prime * result + ((this.returnDataSets== null) ? 0 : this.returnDataSets.hashCode());
    return prime * result + ((this.sizeOfDataSets== null) ? 0 : this.sizeOfDataSets.hashCode());
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    OperationSignature other = (OperationSignature) obj;
    if (this.name == null) {
      if (other.name != null)
        return false;
    } else if (!this.name.equals(other.name))
      return false;
    if (this.id == null) {
      if (other.id != null)
        return false;
    } else if (!this.id.equals(other.id))
      return false;
    if (this.parameters == null) {
      if (other.parameters != null)
        return false;
    } else if (!this.parameters.equals(other.parameters))
      return false;
    if (this.callDataSets == null) {
      if (other.callDataSets != null)
        return false;
    } else if (!this.callDataSets.equals(other.callDataSets))
      return false;
    if (this.returnDataSets == null) {
      if (other.returnDataSets != null)
        return false;
    } else if (!this.returnDataSets.equals(other.returnDataSets))
      return false;
    if (this.sizeOfDataSets == null) {
      if (other.sizeOfDataSets != null)
        return false;
    } else if (!this.sizeOfDataSets.equals(other.sizeOfDataSets))
      return false;
    return true;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("name", this.name);
    b.add("id", this.id);
    b.add("parameters", this.parameters);
    b.add("callDataSets", this.callDataSets);
    b.add("returnDataSets", this.returnDataSets);
    b.add("sizeOfDataSets", this.sizeOfDataSets);
    return b.toString();
  }

  @Pure
  public String getName() {
    return this.name;
  }

  @Pure
  public String getId() {
    return this.id;
  }

  @Pure
  public Collection<Parameter> getParameters() {
    return this.parameters;
  }

  @Pure
  public Set<DataSet> getCallDataSets() {
    return this.callDataSets;
  }

  @Pure
  public Set<DataSet> getReturnDataSets() {
    return this.returnDataSets;
  }

  @Pure
  public Map<String, Set<DataSet>> getSizeOfDataSets() {
    return this.sizeOfDataSets;
  }
}
