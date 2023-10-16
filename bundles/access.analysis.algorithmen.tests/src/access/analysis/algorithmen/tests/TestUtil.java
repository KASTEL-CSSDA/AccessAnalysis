package access.analysis.algorithmen.tests;

import static org.junit.Assert.fail;

import java.util.Collection;

import model.abstractions.Identifyable;

public class TestUtil {
	
	public static <T extends Identifyable> void assertListEqualsUnordered(String reason, Collection<T> expected, Collection<T> actual) {
		for (T expetedElement : expected) {
			if(!actual.stream().anyMatch(item -> item.getId().equals(expetedElement.getId()))) {
				fail("The list does not contain a expected element: " + expetedElement.toString());
			}
		}
		
		if(actual.size() > expected.size()) {
			var missingCount = actual.size() - expected.size();
			fail(missingCount + "The actual collection contains " + missingCount + " items more then the expected");
		}
	}
}
