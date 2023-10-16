package access.analysis.algorithmen.abstractions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import model.abstractions.Identifyable;
import model.abstractions.Nameable;

public class AccessTrace {
	private List<TraceStep> passedIdentifiables;
	
	/*
	 * Creates a new AccessTrace with an initial trace step
	 */
	public AccessTrace(TraceStep traceStep) {
		this(Arrays.asList(traceStep));
	}
	
	private AccessTrace(List<TraceStep> passedIdentifiables) {
		super();
		this.passedIdentifiables = passedIdentifiables;
	}

	/*
	 * Returns a new Trace that is extended by the newly accessed identifyable 
	 */
	public AccessTrace access(Identifyable identifyable, String reason) {
		return new AccessTrace(Stream.concat(passedIdentifiables.stream(), Stream.of(new TraceStep(identifyable, reason))).toList());
	}
	
	/*
	 * Returns a copy of the trace without the last accessed element 
	 */
	public AccessTrace getPopped() {
		return new AccessTrace(passedIdentifiables.stream().limit(passedIdentifiables.size()-1).toList());
	}
	
	/*
	 * Provides a string representation where every trace step is a string in the returned list
	 */
	public List<String> getAsString(){
		return passedIdentifiables.stream().map(step -> "at " + bestEffortName(step.getPassedElement()) + " because " + step.getReason()).toList();
	}
	
	/*
	 * Gets a human readable name of an identifyable. This contains the name (if its also a Nameable provided by that interface),
	 * the id and the class type
	 */
	private String bestEffortName(Identifyable identifyable) {
		String name;
		
		if(identifyable instanceof Nameable) {
			name = ((Nameable) identifyable).getName();
		}else {
			name = identifyable.toString();
		}
		
		return name + " (id:" + identifyable.getId().getId() + ", " + identifyable.getClass().getSimpleName() + ")";
	}
	
	@Override
	public String toString() {
		return String.join(", ", passedIdentifiables.stream().map(Object::toString).toArray(String[]::new));
	}

	@Override
	public int hashCode() {
		var hashCode = 1;
		var i = passedIdentifiables.iterator();
		while (i.hasNext()) {
			Object obj = i.next();
			hashCode = 31*hashCode + (obj==null ? 0 : obj.hashCode());
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccessTrace other = (AccessTrace) obj;
		
		if(passedIdentifiables.size() != other.passedIdentifiables.size()) {
			return false;
		}
		
		for(int i = 0; i < passedIdentifiables.size(); i++) {
			if(passedIdentifiables.get(i).equals(other.passedIdentifiables.get(i))) {
				return false;
			}
		}
		
		return true;
	}
	
}
