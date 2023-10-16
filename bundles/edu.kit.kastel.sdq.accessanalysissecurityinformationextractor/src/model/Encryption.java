package model;

import model.abstractions.Identifyable;
import model.abstractions.Identity;
import model.abstractions.Nameable;

public class Encryption implements Identifyable, Nameable {
    
    private LinkingResource encryptedLinkingResource;
    private DataSet encrypDataSet;
    private Identity id;
    private String name;

    @Override
    public Identity getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public LinkingResource getEncryptedLinkingResource() {
        return encryptedLinkingResource;
    }

    public DataSet getEncrypDataSet() {
        return encrypDataSet;
    }

}
