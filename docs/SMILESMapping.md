## Small Molecule Isolation Lattices Mapping to the PID Kernel Information Profile Recommended by the RDA PID Kernel Information Working Group

| PID KI Field | Form | Description | 
|--------------|------|-------------|
| PID          | Handle | Global identifier for the molecular model data | 
| RPDKIProfileType | Handle | PID for this profile 11723/60acd5b32326dd7e2612 |
| digitalObjectType | Handle | Points to type definition for this type of object |
| digitalObjectLocation | URL | Location of the file holding the molecular model to be used |
| etag | HexString | Checksum of the molecular model data contents |
| lastModified | ISO 8601 Date | Date the molecular data was last modified. Applicable only if the object has been modified from it's original | 
| creationDate | ISO 8601 Date | Date the molecular model data was created |
| version | String | If applicable, not necessary in the SMILES use case |
| wasDerivedFrom | Handle | Applicable if a transformation of a molecular model into another, an update of a model resulting in a new one, or the construction of a new model based on a pre-existing entity |
| specializationOf | Handle | Model is a specialization of another that shares all aspects of the latter, and additional presents more specific aspects of the same thing as the latter |
| wasRevisionOf | Handle | A derivation for which the resulting model is a revised version of some original |
| hadPrimarySource | Handle | A primary source for a model refers to something produced by some agent with direct experience and knowledge about the model, at the time of the model's study, without benefit from hindsight |
| wasQuotedFrom | Handle | Used for the repeat of (some or all of) a model by someone who may or may not be its original creator, most likely not applicable to the SMILES use case | 
| alternateOf | Handle | Model present aspects of the same thing. These aspects may be the same or different, and the alternate models may or may not overlap in time |
