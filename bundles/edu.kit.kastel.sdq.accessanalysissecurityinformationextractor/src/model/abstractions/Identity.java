package model.abstractions;

import java.util.UUID;

public class Identity {
    private String id;

    public static Identity CreateNew() {
    	return new Identity(UUID.randomUUID().toString());
    }
    
    public Identity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
