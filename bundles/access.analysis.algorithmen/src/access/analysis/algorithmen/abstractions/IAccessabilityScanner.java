package access.analysis.algorithmen.abstractions;

import java.util.stream.Stream;

import model.Adversary;
import model.DataSet;

public interface IAccessabilityScanner {
	Stream<AccessabilityResult<DataSet>> getDataSetsAccessableBy(Adversary adversary);
}
