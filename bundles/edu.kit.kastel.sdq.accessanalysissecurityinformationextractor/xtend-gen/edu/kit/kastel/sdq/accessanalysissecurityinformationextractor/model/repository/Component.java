package edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.repository;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@Data
@SuppressWarnings("all")
public class Component {
  private final String name;

  private final String id;

  private final Collection<PCMInterface> providedInterfaces;

  private final Collection<PCMInterface> requiredInterfaces;

  public Component(final String name, final String id) {
    this.name = name;
    this.id = id;
    ArrayList<PCMInterface> _arrayList = new ArrayList<PCMInterface>();
    this.providedInterfaces = _arrayList;
    ArrayList<PCMInterface> _arrayList_1 = new ArrayList<PCMInterface>();
    this.requiredInterfaces = _arrayList_1;
  }

  public void addProvidedInterface(final PCMInterface iface) {
    this.providedInterfaces.add(iface);
  }

  public void addRequiredInterface(final PCMInterface iface) {
    this.requiredInterfaces.add(iface);
  }

  public Collection<OperationSignature> getProvidedOperationSignatures() {
    final Function1<PCMInterface, Collection<OperationSignature>> _function = new Function1<PCMInterface, Collection<OperationSignature>>() {
      public Collection<OperationSignature> apply(final PCMInterface x) {
        return x.getSignatures();
      }
    };
    return IterableExtensions.<OperationSignature>toList(IterableExtensions.<PCMInterface, OperationSignature>flatMap(this.providedInterfaces, _function));
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.id== null) ? 0 : this.id.hashCode());
    result = prime * result + ((this.providedInterfaces== null) ? 0 : this.providedInterfaces.hashCode());
    return prime * result + ((this.requiredInterfaces== null) ? 0 : this.requiredInterfaces.hashCode());
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
    Component other = (Component) obj;
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
    if (this.providedInterfaces == null) {
      if (other.providedInterfaces != null)
        return false;
    } else if (!this.providedInterfaces.equals(other.providedInterfaces))
      return false;
    if (this.requiredInterfaces == null) {
      if (other.requiredInterfaces != null)
        return false;
    } else if (!this.requiredInterfaces.equals(other.requiredInterfaces))
      return false;
    return true;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("name", this.name);
    b.add("id", this.id);
    b.add("providedInterfaces", this.providedInterfaces);
    b.add("requiredInterfaces", this.requiredInterfaces);
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
  public Collection<PCMInterface> getProvidedInterfaces() {
    return this.providedInterfaces;
  }

  @Pure
  public Collection<PCMInterface> getRequiredInterfaces() {
    return this.requiredInterfaces;
  }
}
