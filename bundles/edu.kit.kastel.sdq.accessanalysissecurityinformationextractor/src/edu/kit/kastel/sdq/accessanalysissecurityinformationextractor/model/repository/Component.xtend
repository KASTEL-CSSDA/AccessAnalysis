package edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model.repository

import org.eclipse.xtend.lib.annotations.Data
import java.util.Collection
import java.util.ArrayList

@Data class Component {
	
	private final String name;
	private final String id;
	private final Collection<PCMInterface> providedInterfaces;
	private final Collection<PCMInterface> requiredInterfaces;
	
	new(String name, String id) {
		this.name  = name; 
		this.id = id;
		this.providedInterfaces = new ArrayList<PCMInterface>();
		this.requiredInterfaces = new ArrayList<PCMInterface>();
	}
	
	def public void addProvidedInterface(PCMInterface iface){
		providedInterfaces.add(iface);
	}
	
	def public void addRequiredInterface(PCMInterface iface){
		requiredInterfaces.add(iface);
	}
	
	def public Collection<OperationSignature> getProvidedOperationSignatures(){
		return providedInterfaces.flatMap[x | x.signatures].toList;
	}
	
}
