package access.analysis.algorithmen.abstractions;

import model.EnvironmentModel;

public interface IAccessAnalyser {
	AnalysisResult Analyse(EnvironmentModel model);
}
