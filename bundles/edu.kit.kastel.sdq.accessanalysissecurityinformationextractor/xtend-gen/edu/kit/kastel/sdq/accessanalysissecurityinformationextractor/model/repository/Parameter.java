package edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.repository;

import edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.accessanalysis.DataSet;
import java.util.Set;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@Data
@SuppressWarnings("all")
public class Parameter {
  private final String name;

  private final Set<DataSet> annotatedDataSets;

  public Parameter(final String name, final Set<DataSet> annotatedDataSets) {
    super();
    this.name = name;
    this.annotatedDataSets = annotatedDataSets;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    return prime * result + ((this.annotatedDataSets== null) ? 0 : this.annotatedDataSets.hashCode());
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
    Parameter other = (Parameter) obj;
    if (this.name == null) {
      if (other.name != null)
        return false;
    } else if (!this.name.equals(other.name))
      return false;
    if (this.annotatedDataSets == null) {
      if (other.annotatedDataSets != null)
        return false;
    } else if (!this.annotatedDataSets.equals(other.annotatedDataSets))
      return false;
    return true;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("name", this.name);
    b.add("annotatedDataSets", this.annotatedDataSets);
    return b.toString();
  }

  @Pure
  public String getName() {
    return this.name;
  }

  @Pure
  public Set<DataSet> getAnnotatedDataSets() {
    return this.annotatedDataSets;
  }
}
