# E-RPID Mapper Client

The major goals of the E-RPID project fall into three distinct bins:
* Developing a software-based mechanism for the mapping of existing data repositories to be compatible with E-RPID services and the underlying Digital Object Interface Protocol (DOIP). This will allow adoption efforts to proceed without refactoring of the underlying data repository schema.

* Testing the DOIP with the four use cases defined in the project description. The DOIP will allow a set of well defined operations to be carried directly out on digital objects.

* Producing introductory educational and training materials that can inform data repository administrators, client developers, and project managers the advantages of operating within a Digital Object Architecture (DOA) environment and how this DOA environment encourages and aligns the repository with the FAIR (Findable, Accessible, Interoperable, and Reusable) principles.

## Installation Guide

### Software Dependencies

1. Apache Maven V3.0 or higher
2. JDK V1.6 or higher
3. Cordra V2.0
4. Handle service

### Required Software

1. Cordra (DTR Object Service):
   1. download the Cordra service from https://cordra.org/index.html
   2. follow the instructions to install and run the Cordra service
      1. create the proper schema for Cordra Objects in Cordra **Admin > Types** (please follow the `DTR Type format.json` under the `samples`)
      2. create the proper schema for Cordra Types
      3. create the proper types for your objects

2. Handle System (Persistent Identifier Service)
  1. download the Handle service from http://handle.net
  2. follow the instructions to install and run the Handle service

### Building the Source

Check out source codes:

```
git clone https://github.com/luoyu357/E-RPID.git
```

go to the source code folder:

```
cd E-RPID
```

#### Updating the code based on your project

---
##### Update the `basicConfig.json` under `project > config`

* upload the Handle service's URL in `API URL`
* upload the Handle prefix in `prefix`
* upload the path of Handle service's `admpriv.bin` in `private key file`
* upload the password of Handle service in `private key file password`\
* upload the handle admin identifier in `handle admin identifier`


```
{
	"Handle System" :
	{
		"API URL" : "http://149.165.168.252:8000/api/handles/",
		"prefix" : 11723,
		"private key file" : "/path/admpriv.bin",
		"private key file password" : "password",
		"handle admin identifier" : "0.NA/11723"
	}
```
* upload the Cordra's address in `InetAddress`
* upload the port in `port`
* upload the username of Cordra's account in `username`
* upload the password of Cordra's account in `password`

```
	"DTR DOIP" :
	{
		"InetAddress" : "149.165.169.46",
		"serviceID" : "20.500.123/service",
		"port" : 9000,
		"uesrname" : "username",
		"password" : "password"
	}
}
```
---
##### Upload your repository's information into the `repos.json` under `project > config`

```
{
	"repo list" :
	[
		{
			"repo name" : "dspace",
			"repo description" : "ERPID test DSpace",
			"repo host" : "http://34.214.32.90:8080/",
			"repo type" : "rest",
			"repo username" : "luoyu@dspace",
			"repo password" : "dspace"
		},
		{
			"repo name" : "fedora",
			"repo description" : "fedora repository",
			"repo host" : "http://54.213.12.174:8080/fcrepo-webapp-4.4.0/rest/",
			"repo type" : "fedora",
			"repo username" : "username",
			"repo password" : "password"
		},
		{
			"repo name" : "SEADTrain Github",
			"repo description" : "SEADTrain Github",
			"repo host" : "api.github.com",
			"repo type" : "github",
			"repo username" : "username",
			"repo password" : "password"
		},
		{
			"repo name" : "some_repo",
			"repo description" : "Some repository",
			"repo host" : "some.repo.com",
			"repo type" : "",
			"repo username" : "username",
			"repo password" : "password"
		}
	]
}
```
---
##### Add your pre-defined DTR types into the `src > indiana > edu > cordra > Type.java`

```
public static final String SENSORTYPE = "11723/9dbfb029092e96171074";
public static final String DEVICEMODEL = "11723/22dde27488f42c5872e5";
public static final String STARTDATE = "11723/87269431ee45c3e09809";
```


##### Please add your codes in `src > indiana > edu > engine > ReadData.java` to process your input data

* we provide the sample code to process the sample data `2017-12-04.csv` in `project > input > upload`


##### Add your codes to map the extracted data information with the DTR types

* we provide the sample code `AirboxInstanceJSON.java` in `src > indiana > edu > cordra`

* for creating the JSON object, please follow the `STR Schema.json` in `samples`

##### Create the Cordra object by reading, processing and mapping the data

* Modify the `src > indiana > edu > engine > Process.java` to generate the Cordra object (**Once**)

```
//******* process the input data and generate Cordra object json *******
ReadData read = new ReadData();
Hashtable<String, String> content = read.readContent(file.getAbsolutePath());			
JSONObject AirboxCordraInstance = (new AirboxInstanceJSON()).AirboxCordraInstance(
		content);
//******* process the input data and generate Cordra object json *******
```
---
##### Add your codes for accessing your repository in `src > indiana > edu > repository`

* we provide the sample code in `FedoraClient.java` to access the Fedora repository

* Modify the `src > indiana > edu > engine > Process.java` to run your repository client (**Twice**)

```
//******* Repository *******
if (repo.equalsIgnoreCase("Fedora")) {
	FedoraClient fedora = new FedoraClient(this.configClass);
	UUID uuid = UUID.randomUUID();
	fedora.createContainer(uuid.toString());
	location = fedora.uploadFile(uuid.toString(), file.getName(), file.getAbsolutePath());
}
//******* Repository *******
```
---
Finally, generate the runnable jar file:

```
mvn package
```

The `ERPIDv2-0.0.1-SNAPSHOT.jar` will be located at `target` folder

---

## Operations

### Create

Users add the input raw file into the `project > input > upload`, and run:

```
java -jar ERPIDv2-0.0.1-SNAPSHOT.jar /path/project create repository_name
```

The Mapper Client will register an PID and Cordra object for the data file. When the operation is done, the result will be written into `project > logs > operation_logs.log` and `project > output > results.txt`. Also, users could find the registered PIDs in these files.

The error message will be written into `project > logs > operation_logs.log`, `project > logs > mapper_errors.log` and `project > output > results.txt`

### Retrieve

Users add the retrieving PIDs into the `project > config > retrieve_pid.txt` line by line, and run:

```
java -jar ERPIDv2-0.0.1-SNAPSHOT.jar /path/project retrieve
```

The Mapper Client will retrieve the PIDs and related Cordra object . When the operation is done, the mapping result will be written into `project > logs > operation_logs.log`, `project > logs > retrieve_pid_list.log` and `project > output > results.txt`.

The error message will be written into `project > logs > operation_logs.log`, `project > logs > mapper_errors.log` and `project > output > results.txt`

### Update

There are two choices for updating: **Update the content of PID and Cordra** and **Update the content of PID and Cordra with new file**

If users are only modifying the PID and (or) Cordra content, they could use `project > input > update > updateSample.json`

If users are modifying the PID and Cordra by replacing the incorrect data file, they could use the `project > input > update > advancedUpdateSample.json`

The command is:

```
java -jar ERPIDv2-0.0.1-SNAPSHOT.jar /path/project update
```

When the operation is done, the result will be written into `project > logs > operation_logs.log`, `project > logs > update_pid_list.log` and `project > output > results.txt`.

The error message will be written into `project > logs > operation_logs.log`, `project > logs > mapper_errors.log` and `project > output > results.txt`


### Delete

Users add the deleting PIDs into the `project > config > delete_pid.txt` line by line, and run:

```
java -jar ERPIDv2-0.0.1-SNAPSHOT.jar /path/project delete
```

When the operation is done, the result will be written into `project > logs > operation_logs.log`, `project > logs > delete_pid_list.log` and `project > output > results.txt`.

The error message will be written into `project > logs > operation_logs.log`, `project > logs > mapper_errors.log` and `project > output > results.txt`


## Appendix

* `project > logs > delete_pid_list.log` contains the deleted PIDs

* `project > logs > mapper_errors.log` contains the error messages for all Operations

* `project > logs > operation_logs.log` contains the list of operations

* `project > logs > retrieve_pid_list.log` contains the retrieved PIDs

* `project > logs > update_pid_list.log` contains the updated PIDs

* `project > output > results.txt` contains the mapping result for all Operations
