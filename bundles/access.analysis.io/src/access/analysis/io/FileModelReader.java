package access.analysis.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileModelReader<TObject> {
	private ISerializer<String> serializer;
	private Path path;
	private Class<TObject> targetClass;
	
	public FileModelReader(ISerializer<String> serializer, Class<TObject> targetClass, Path path) {
		super();
		this.serializer = serializer;
		this.path = path;
		this.targetClass = targetClass;
	}

	public TObject Read() {
		try {
			return serializer.Deserialize(Files.readString(path), targetClass);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
