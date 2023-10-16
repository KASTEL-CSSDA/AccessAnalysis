package access.analysis.io;

public interface ISerializer<TRepresentation> {
	TRepresentation Serialize(Object object);
	<TObject> TObject Deserialize(TRepresentation data, Class<TObject> targetClass);
}
