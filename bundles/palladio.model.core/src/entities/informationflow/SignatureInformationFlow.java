package entities.informationflow;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.abstractions.Identity;

public class SignatureInformationFlow extends AbstractInformationFlow {

	public static SignatureInformationFlow CreateNew(Information information) {
		return SignatureInformationFlow.CreateNew(Arrays.asList(information));
	}
	
	public static SignatureInformationFlow CreateNew(List<Information> information) {
		return new SignatureInformationFlow(information, Identity.CreateNew());
	}
	
	private SignatureInformationFlow(List<Information> information, Identity id) {
		super(information, id);
	}
	
	private SignatureInformationFlow() {}

	public String toString() {
		return String.join(",", getInformation().map(Object::toString).collect(Collectors.toList()));
	}
}
