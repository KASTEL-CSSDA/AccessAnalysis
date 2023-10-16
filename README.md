# AccessAnalysis

This Project provides a reduced java reimplementation of the Access Analysis presented in the Report [Model-Driven Specification and Analysis of Confidentiality in Component-Based Systems](doi.org/10.5445/IR/1000076957) of Kramer et al.
The analysis entry point is located in the [access.analysis.app](bundles/access.analysis.app) project. 

In addition, the EMF Metamodels of the [Palladio Component Model (PCM)](https://www.palladio-simulator.com/science/palladio_component_model/), [Confidentiality4CBSE](https://github.com/KASTEL-SCBS/Confidentiality4CBSE) and the EMF Profiles are replaced by a java based model. The PCM elements are located in the project [palladio.model.core](bundles/palladio.model.core) and the elements of the access analysis in the project [security.analysis.model](bundles/security.analysis.model/). The used elements and connections are as seen in the following figure.

## Analysis Usage 
In order to run the access analysis run the main-function of the ["AccessAnalysis"-class](bundles/access.analysis.app/src/access/analysis/app/AccessAnalysis.java) in the [access.analysis.app](bundles/access.analysis.app) project.

### Define Input and Output 
Add the source-file-path (path to the JSON-file containing the model) as first argument to define the input and the result-file-path as second argument to define the output location.

### Flags 
Several flags can be provided to additionally configure the output and the analysis algorithm. Please note that the input- and output- path as first and second argument!

The flag **--flat** flattens the results to be better readable by humans.
The flag **--grouped** enables whether results may be grouped.

The reimplemented analysis algorithm is designed to be configurable to represent different usage scenarios. 
The flag **--paperstyle** runs the analysis with the same configuration as in the Approach of [Model-Driven Specification and Analysis of Confidentiality in Component-Based Systems](doi.org/10.5445/IR/1000076957) 
The flag **--unknownsets** defines the handling of unknown data sets.   
The original access analysis implementation defines an access as valid if an adversary may know at least one of the annotated data sets. This option is enabled with **--mayknowone**. In the reimplementation, an adversary must know all annoted data sets, representing a permission-like understanding.  



