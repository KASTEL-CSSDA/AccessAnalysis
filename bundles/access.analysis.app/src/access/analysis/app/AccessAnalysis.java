package access.analysis.app;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Function;

import access.analysis.algorithmen.AccessAnalyser;
import access.analysis.algorithmen.abstractions.AlgorithmConfiguration;
import access.analysis.algorithmen.abstractions.AnalysisResult;
import access.analysis.algorithmen.abstractions.IAccessAnalyser;
import access.analysis.algorithmen.abstractions.IModelProvider;
import access.analysis.io.EnvironmentModelProvider;
import access.analysis.io.FileModelWriter;
import access.analysis.io.FlattenedAnalysisResult;
import access.analysis.io.JsonSerializer;

public class AccessAnalysis {

	private static final String FLATTEND_FLAG = "--flat";
	
	private static final String PAPER_STYLE_FLAG = "--paperstyle";
	
	private static final String GROUPED_FLAG = "--grouped";
	private static final String MAY_KNOW_ONE_FLAG = "--mayknowone";
	private static final String UNKNOWN_SETS_FLAG = "--unknownsets";
	
	public static void main(String[] args) {
		IModelProvider modelprovider = new EnvironmentModelProvider(
			new JsonSerializer(),
			Path.of(args[0])
		);
		
		IAccessAnalyser accessAnalyser = new AccessAnalyser(algorithmConfigurationFrom(args));
		
		if(flattendRequested(args)) {
			runAnalysis(args[1], accessAnalyser, modelprovider, result -> new FlattenedAnalysisResult(result));
		}else {
			runAnalysis(args[1], accessAnalyser, modelprovider, result -> result);
		}
	}
	
	private static boolean flattendRequested(String[] args) {
		return Arrays.stream(args).anyMatch(arg -> arg.equals(FLATTEND_FLAG));
	}
	
	private static AlgorithmConfiguration algorithmConfigurationFrom(String[] args) {
		var argList = Arrays.asList(args);
		
		if(argList.contains(PAPER_STYLE_FLAG)) {
			return AlgorithmConfiguration.GetKramerPaperStyle();
		}
		
		return new AlgorithmConfiguration(
			argList.contains(MAY_KNOW_ONE_FLAG),
			argList.contains(GROUPED_FLAG),
			argList.contains(UNKNOWN_SETS_FLAG)
		);
	}
	
	private static <T> void runAnalysis(String outputFile, IAccessAnalyser accessAnalyser, IModelProvider modelprovider, Function<AnalysisResult, T> converter){
		var analysisResultHandler = new FileModelWriter<T>(
			new JsonSerializer(),
			Path.of(outputFile)
		);
		
		var analysisResult = accessAnalyser.Analyse(modelprovider.getEnvironmentModel());
		System.out.println("Found " + analysisResult.getSecurityThreats().count() + " threat(s)");
		
		analysisResultHandler.Handle(converter.apply(analysisResult));
	}

}
