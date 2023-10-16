package access.analysis.algorithmen.abstractions;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import model.abstractions.Identifyable;

public class AccessabilityResult<T> {
	private AccessTrace trace;
	private T accessedElement;
	
	public static <T extends Identifyable> AccessabilityResult<T> getNew(T accessedElement, String reason) {
		return new AccessabilityResult<>(new AccessTrace(new TraceStep(accessedElement, reason)), accessedElement);
	}
	
	public AccessabilityResult(AccessTrace trace, T accessedElement) {
		super();
		this.trace = trace;
		this.accessedElement = accessedElement;
	}

	public AccessTrace getTrace() {
		return trace;
	}

	public T getAccessedElement() {
		return accessedElement;
	}
	
	public AccessTrace getPoppedTrace() {
		return trace.getPopped();
	}
	
	public <TNext extends Identifyable> AccessabilityResult<TNext> ThenAccess(TNext nextElement, String reason){
		return new AccessabilityResult<TNext>(trace.access(nextElement, reason), nextElement);
	}
	
	public <TNext extends Identifyable> AccessabilityResult<TNext> ThenAccessBy(Function<T, TNext> nextSelector, String reason){
		var nextElement = nextSelector.apply(accessedElement);
		return ThenAccess(nextElement, reason);
	}
	
	public <TNext extends Identifyable> Stream<AccessabilityResult<TNext>> ThenAccessAll(Function<T, Stream<TNext>> nextSelector, String reason){
		return nextSelector.apply(accessedElement).map(nextElement -> ThenAccess(nextElement, reason));
	}

	@Override
	public int hashCode() {
		return Objects.hash(accessedElement, trace);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		var other = (AccessabilityResult<?>) obj;
		return Objects.equals(accessedElement, other.accessedElement) && Objects.equals(trace, other.trace);
	}
	

}
