package access.analysis.algorithmen.tests;

import access.analysis.algorithmen.abstractions.AnalysisResult;
import access.analysis.algorithmen.abstractions.IAccessAnalyser;
import model.EnvironmentModel;

public class DummyAccessAnalyser implements IAccessAnalyser {

	@Override
	public AnalysisResult Analyse(EnvironmentModel model) {
		return null;
	}

}
