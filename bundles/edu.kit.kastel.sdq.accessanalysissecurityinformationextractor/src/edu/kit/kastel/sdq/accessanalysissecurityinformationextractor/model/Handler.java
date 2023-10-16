package edu.kit.kastel.sdq.accessanalysissecurityinformationextractor.model;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

public class Handler extends AbstractHandler {

	private PCMModels model;
	
	/**
	 * This method is the entry point for the generation handler.
	 * To use the handler, the adversary, confidentiality, and repository files have to be selected.
	 */
	@Override
	public Object execute(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		Collection<IFile> list = getFilteredList(selection);
		
		model = PCMModels.generateFromFiles(list);
		
		System.out.println("loaded models!");
		
		var factory = new AccessAnalysisModelFactory(new PcmModelAdapter(model));
		var accessAnalysisModel = factory.create();
		
		PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder().build();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
		
		File outFile = new File("OUTPUT_FILE_PATH");
		try {
			var json = objectMapper.writeValueAsString(accessAnalysisModel);
			outFile.delete();
			outFile.createNewFile();
			FileWriter myWriter = new FileWriter(outFile);
		    myWriter.write(json);
		    myWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

	@SuppressWarnings("unchecked")
	public static Collection<IFile> getFilteredList(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;

			return (List<IFile>) structuredSelection.toList().stream().filter(file -> file instanceof IFile)
					.map(IFile.class::cast).collect(Collectors.toList());
		}
		return new ArrayList<IFile>();
	}
	
}