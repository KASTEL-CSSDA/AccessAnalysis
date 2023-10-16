package access.analysis.algorithmen.tests;

import java.util.ArrayList;
import java.util.Arrays;

import entities.AssemblyContext;
import entities.Component;
import entities.Interface;
import entities.OperationSignature;
import entities.informationflow.Information;
import entities.informationflow.SignatureInformationFlow;
import entities.informationflow.information.AllInformation;
import entities.informationflow.information.ParameterInformation;
import entities.informationflow.information.ReturnValueInformation;
import model.Adversary;
import model.ConnectionType;
import model.DataSet;
import model.Encryption;
import model.EnrichedResourceContainer;
import model.EnvironmentModel;
import model.LinkingResource;
import model.Location;
import model.SharingType;
import model.abstractions.Identity;
import model.tamperprotections.AbstractTamperprotection;
import model.tamperprotections.LinkingResourceTamperProtection;
import model.tamperprotections.ResourceTamperprotection;

class HouseholdModel {
	
	public Adversary guest;
	public Adversary inhabitant;
	public Adversary passerBy;
	public EnvironmentModel environmentModel;
	public DataSet billingdata;
	public DataSet consumptionData;
	public EnrichedResourceContainer energyVisualisationContainer;
	public LinkingResource wireless;
	
	private HouseholdModel(
		EnvironmentModel environmentModel,
		Adversary guest,
		Adversary inhabitant,
		Adversary passerBy,
		DataSet billingdata,
		DataSet consumptionData,
		EnrichedResourceContainer energyVisualisationContainer,
		LinkingResource wireless
	) {
		super();
		this.guest = guest;
		this.environmentModel = environmentModel;
		this.billingdata = billingdata;
		this.consumptionData = consumptionData;
		this.energyVisualisationContainer = energyVisualisationContainer;
		this.inhabitant = inhabitant;
		this.passerBy = passerBy;
		this.wireless = wireless;
	}
	
	public static HouseholdModel create() {
		
		var energyVisualizationOp1 = OperationSignature.CreateNew(
			"image drawEnergyConsumptionGraph()",
			SignatureInformationFlow.CreateNew(ReturnValueInformation.CreateNew())
		);
		var energyVisualization = Interface.CreateNew(
			"Energy visualisation interface",
			Arrays.asList(energyVisualizationOp1),
			null
		);
		
		var databaseInterfaceOp1 = OperationSignature.CreateNew(
			"int[] getValues(int start, int end)",
			SignatureInformationFlow.CreateNew(AllInformation.CreateNew())
		);
		var databaseInterfaceOp2 = OperationSignature.CreateNew(
			"storeValue(int timestamp, int value)",
			SignatureInformationFlow.CreateNew(
				Arrays.asList(
					ParameterInformation.CreateNew("timestamp"), 
					ParameterInformation.CreateNew("value")
				)
			)
		);
		var databaseInterface = Interface.CreateNew(
			"Database interface",
			Arrays.asList(databaseInterfaceOp1, databaseInterfaceOp2),
			null
		);
		
		var energyMeasurementInterfaceOp1 = OperationSignature.CreateNew(
			"int getEnergyValue()",
			SignatureInformationFlow.CreateNew(ReturnValueInformation.CreateNew())
		);
		var energyMeasurementInterfaceOp2 = OperationSignature.CreateNew(
			"int getCustomerId()",
			SignatureInformationFlow.CreateNew(ReturnValueInformation.CreateNew())
		);
		var energyMeasurementInterface = Interface.CreateNew(
			"Database interface",
			Arrays.asList(energyMeasurementInterfaceOp1, energyMeasurementInterfaceOp2),
			null
		);
		
		var billingdata = DataSet.CreateNew(
			"Billingdata",
			energyMeasurementInterfaceOp2.getInformationFlow().getInformation().toList()
		);
		
		var consumtionDataInformation = new ArrayList<Information>();
		consumtionDataInformation.addAll(energyVisualizationOp1.getInformationFlow().getInformation().toList());
		consumtionDataInformation.addAll(databaseInterfaceOp1.getInformationFlow().getInformation().toList());
		consumtionDataInformation.addAll(databaseInterfaceOp2.getInformationFlow().getInformation().toList());
		consumtionDataInformation.addAll(energyMeasurementInterfaceOp1.getInformationFlow().getInformation().toList());
		
		var consumptiondata = DataSet.CreateNew(
			"Consumptiondata",
			consumtionDataInformation
		);
		
		var energyMeterComponent = Component.CreateNew(
			"Energy Meter",
			Arrays.asList(energyMeasurementInterface),
			new ArrayList<Interface>()
		);
		var dbMsComponent = Component.CreateNew(
			"DataBase",
			Arrays.asList(databaseInterface),
			new ArrayList<Interface>()
		);
		var energyVisualizationComponent = Component.CreateNew(
			"Energy Visualisation",
			Arrays.asList(energyVisualization),
			Arrays.asList(databaseInterface, energyMeasurementInterface)
		);
		
		var energyMeterAssemblyContext = AssemblyContext.CreateNew("Energy Meter", energyMeterComponent);
		var databaseAssemblyContext = AssemblyContext.CreateNew("Energy Meter", dbMsComponent);
		var energyVisualizationAssemblyContext = AssemblyContext.CreateNew("Energy Meter", energyVisualizationComponent);
		
		var seal = ResourceTamperprotection.CreateNew("basic seal");
		
		var energyVisualizationContainer = new EnrichedResourceContainer(
			new ArrayList<ResourceTamperprotection>(),
			Arrays.asList(databaseAssemblyContext, energyVisualizationAssemblyContext),
			SharingType.Shared,
			ConnectionType.Possible,
			Identity.CreateNew(),
			"Energy Visualization"
		);
		
		var energyMeterContainer = new EnrichedResourceContainer(
			Arrays.asList(seal),
			Arrays.asList(energyMeterAssemblyContext),
			SharingType.Exclusive,
			ConnectionType.Complete,
			Identity.CreateNew(),
			"Energy Meter"
		);
		
		var livingRoom = Location.CreateNew("living room", Arrays.asList(energyVisualizationContainer));
		var utilityRoom =  Location.CreateNew("utility room", Arrays.asList(energyMeterContainer));
		var outDoors =  Location.CreateNew("out doors", new ArrayList<EnrichedResourceContainer>());
		
		var wireless = LinkingResource.CreateNewFrom(
			"wireless",
			Arrays.asList(energyVisualizationContainer, energyMeterContainer),
			new ArrayList<LinkingResourceTamperProtection>(),
			Arrays.asList(livingRoom, utilityRoom, outDoors)
		);
		
		var passerBy = Adversary.CreateNewFrom(
			"passer-by",
			new ArrayList<DataSet>(),
			new ArrayList<AbstractTamperprotection>(),
			Arrays.asList(outDoors)
		);
		var guest = Adversary.CreateNewFrom(
				"guest",
				Arrays.asList(consumptiondata),
				new ArrayList<AbstractTamperprotection>(),
				Arrays.asList(outDoors, livingRoom)
			);
		var inhabitant = Adversary.CreateNewFrom(
			"inhabitant",
			Arrays.asList(consumptiondata, billingdata),
			new ArrayList<AbstractTamperprotection>(),
			Arrays.asList(outDoors, livingRoom, utilityRoom)
		);
		
		var environmentModel = new EnvironmentModel(
			Arrays.asList(passerBy, guest, inhabitant),
			Arrays.asList(livingRoom, utilityRoom, outDoors),
			Arrays.asList(wireless),
			Arrays.asList(consumptiondata, billingdata),
			new ArrayList<Encryption>()
		);
		
		return new HouseholdModel(
			environmentModel,
			guest,
			inhabitant,
			passerBy,
			billingdata,
			consumptiondata,
			energyVisualizationContainer,
			wireless
		);
	}
	
}
