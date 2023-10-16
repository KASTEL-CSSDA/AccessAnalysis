package access.analysis.algorithmen.abstractions;

import java.util.Objects;

import model.abstractions.Identifyable;
import model.abstractions.Nameable;

public class TraceStep {
	private Identifyable passedElement;
	private String reason;
	
	public TraceStep(Identifyable passedElement, String reason) {
		super();
		this.passedElement = passedElement;
		this.reason = reason;
	}
	public Identifyable getPassedElement() {
		return passedElement;
	}
	public String getReason() {
		return reason;
	}
	
	@Override
	public String toString() {
		return ElementToString(passedElement) + "\""+ reason +"\"";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(passedElement.getId().getId(), reason);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TraceStep other = (TraceStep) obj;
		return Objects.equals(passedElement.getId().getId(), other.passedElement.getId().getId()) && Objects.equals(reason, other.reason);
	}
	
	private static String ElementToString(Identifyable identifyable) {
		String name;
		String type = identifyable.getClass().getSimpleName();
		
		if (identifyable instanceof Nameable) {
            name = ((Nameable) identifyable).getName();
        }else {
        	name = identifyable.getId().getId();
        }
		
		return name + " (" + type + ")";
	}
}
