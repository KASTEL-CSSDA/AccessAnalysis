package access.analysis.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileModelWriter<T> {

	private ISerializer<String> serializer;
	private Path path;
	
	public FileModelWriter(ISerializer<String> serializer, Path path) {
		super();
		this.serializer = serializer;
		this.path = path;
	}

	public void Handle(T analysisResult) {
		var serializedData = serializer.Serialize(analysisResult);
		try {
			Files.write(path, serializedData.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
