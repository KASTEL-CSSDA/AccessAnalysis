package model;

import java.util.List;
import java.util.stream.Stream;

public class EnvironmentModel {
	private List<Adversary> adversaries;
	private List<Location> locations;
	private List<LinkingResource> links;
	private List<DataSet> dataSets;
	private List<Encryption> encryptions;
	
	public EnvironmentModel(
		List<Adversary> adversaries,
		List<Location> locations,
		List<LinkingResource> links,
		List<DataSet> dataSets,
		List<Encryption> encryptions
	) {
		super();
		this.adversaries = adversaries;
		this.locations = locations;
		this.links = links;
		this.dataSets = dataSets;
		this.encryptions = encryptions;
	}
	
	@SuppressWarnings("unused")
	private EnvironmentModel() {
		
	}
	
	public Stream<Adversary> getAdversaries(){
		return adversaries.stream();
	}
	
	public Stream<Encryption> getEncryptions(){
		return encryptions.stream();
	}
	
	public Stream<Location> getLocations(){
		return locations.stream();
	}
	
	public Stream<LinkingResource> getLinkingResources(){
		return links.stream();
	}
	
	public Stream<DataSet> getDataSets(){
		return dataSets.stream();
	}
}