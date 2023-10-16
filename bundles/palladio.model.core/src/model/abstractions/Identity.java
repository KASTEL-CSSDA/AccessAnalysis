package model.abstractions;

import java.util.Objects;
import java.util.UUID;

public class Identity {
    private String id;

    public static Identity CreateNew() {
    	return new Identity(UUID.randomUUID().toString());
    }
    
    public Identity(String id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    private Identity() {}
    
    public String getId() {
        return id;
    }

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Identity other = (Identity) obj;
		return Objects.equals(id, other.id);
	}
    
    
}
