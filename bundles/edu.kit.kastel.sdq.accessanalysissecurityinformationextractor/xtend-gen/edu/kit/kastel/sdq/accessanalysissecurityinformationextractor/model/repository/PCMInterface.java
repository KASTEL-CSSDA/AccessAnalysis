package edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.repository;

import java.util.Collection;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@Data
@SuppressWarnings("all")
public class PCMInterface {
  private final String name;

  private final String id;

  private final Collection<OperationSignature> signatures;

  public PCMInterface(final String name, final String id, final Collection<OperationSignature> signatures) {
    super();
    this.name = name;
    this.id = id;
    this.signatures = signatures;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.id== null) ? 0 : this.id.hashCode());
    return prime * result + ((this.signatures== null) ? 0 : this.signatures.hashCode());
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
    PCMInterface other = (PCMInterface) obj;
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
    if (this.signatures == null) {
      if (other.signatures != null)
        return false;
    } else if (!this.signatures.equals(other.signatures))
      return false;
    return true;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("name", this.name);
    b.add("id", this.id);
    b.add("signatures", this.signatures);
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
  public Collection<OperationSignature> getSignatures() {
    return this.signatures;
  }
}
