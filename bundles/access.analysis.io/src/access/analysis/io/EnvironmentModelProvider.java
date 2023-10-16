package access.analysis.io;

import java.nio.file.Path;

import access.analysis.algorithmen.abstractions.IModelProvider;
import model.EnvironmentModel;

public class EnvironmentModelProvider implements IModelProvider {

	private ISerializer<String> serializer;
	private Path path;
	
	public EnvironmentModelProvider(ISerializer<String> serializer, Path path) {
		super();
		this.serializer = serializer;
		this.path = path;
	}
	
	@Override
	public EnvironmentModel getEnvironmentModel() {
		var reader = new FileModelReader<EnvironmentModel>(
			serializer,
			EnvironmentModel.class,
			path
		);
		return reader.Read();
	}

}
