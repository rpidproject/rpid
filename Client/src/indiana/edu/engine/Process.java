package indiana.edu.engine;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import indiana.edu.cordra.AirboxInstanceJSON;
import indiana.edu.cordra.Cordra;
import indiana.edu.cordra.DOIP;
import indiana.edu.erpid.HandleSystem;
import indiana.edu.mapping.MappingEngine;
import indiana.edu.property.Config;
import indiana.edu.repository.FedoraClient;
import net.handle.hdllib.HandleException;

public class Process {
	private Config configClass;
	private HandleSystem handle;
	public Process(Config config) throws IOException {
		this.configClass = config;
		this.handle = new HandleSystem(config);
	}
	
	public void resolveProcess(String pid) throws IOException {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String intro = "[" + timestamp + "] Handle Reoslving: " + pid;
		DOIP doip = new DOIP(this.configClass);
		try {
			JSONArray output = this.handle.httpResolve(pid);	
			this.configClass.writeToFile(output.toString(4), this.configClass.results, intro);
			this.configClass.writeToFile("", this.configClass.operationLogs, intro);
			this.configClass.writeToFile("", this.configClass.retrieve_pid, intro);
			
			JSONObject dtrResult = new JSONObject();
			for (Object items : output) {
				JSONObject item = (JSONObject) items;
				if (item.get("Type").toString().contains("cordraID")) {
					if (!item.getString("Value").equals("Cordra Object deleted")) {
						intro = "[" + timestamp + "] DTR Reoslving: " + item.getString("Value");
						try {
							JSONObject dtrContent = doip.retrieve(item.getString("Value"));
							dtrResult = (new MappingEngine(this.configClass)).mappingDTRObject(dtrContent);
							
							this.configClass.writeToFile(dtrResult.toString(4), this.configClass.results, intro);
							this.configClass.writeToFile("", this.configClass.operationLogs, intro);
						} catch (Exception e) {
							this.configClass.writeToFile("", this.configClass.results, intro+ " - Resolving failed");
							this.configClass.writeToFile("", this.configClass.operationLogs, intro+ " - Resolving failed");
							this.configClass.writeToFile("", this.configClass.errors, intro+ " - No such Cordra");
						}
					}
				}
			}		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.configClass.writeToFile("", this.configClass.results, intro+ " - Resolving failed");
			this.configClass.writeToFile("", this.configClass.operationLogs, intro+ " - Resolving failed");
			this.configClass.writeToFile("", this.configClass.errors, intro+ " - No such PID");
		}	
		
	}
	
	
	public void createProcess(File[] files, String repo) throws HandleException, Exception {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());	
		DOIP doip = new DOIP(this.configClass);
		for (File file : files) {
			
			//******* process the input data and generate Cordra object json *******
			ReadData read = new ReadData();
			Hashtable<String, String> content = read.readContent(file.getAbsolutePath());			
			JSONObject AirboxCordraInstance = (new AirboxInstanceJSON()).AirboxCordraInstance(
					content);
			//******* process the input data and generate Cordra object json *******
			
			
			try {	
				JSONObject doipResult = doip.create(AirboxCordraInstance);
				String dtrID = doipResult.getString("id");			
				String location = "No Location";
				
				//******* Repository *******
				if (repo.equalsIgnoreCase("Fedora")) {
					FedoraClient fedora = new FedoraClient(this.configClass);
					UUID uuid = UUID.randomUUID();
					fedora.createContainer(uuid.toString());
					location = fedora.uploadFile(uuid.toString(), file.getName(), file.getAbsolutePath());
				}	
				//******* Repository *******
				
				JSONObject pidKI = handle.pidKI(location, "E-RPID", dtrID);
			
				try {		
					String result = this.handle.createHandle(pidKI);
					if (result.equalsIgnoreCase("Failed")) {
						String intro = "[" + timestamp + "] Handle Creating: Failed";
						this.configClass.writeToFile("Creating failed", this.configClass.results, intro);
						this.configClass.writeToFile("Creating failed", this.configClass.operationLogs, intro);
						this.configClass.writeToFile("Failed to create the Handle", this.configClass.errors, intro);
					}else {
						String intro = "[" + timestamp + "] Handle Creating: " + pidKI.get("PID");
						this.configClass.writeToFile(pidKI.toString(4), this.configClass.results, intro);
						this.configClass.writeToFile("", this.configClass.operationLogs, intro);
						
						intro = "[" + timestamp + "] DTR Creating: " + dtrID;
						this.configClass.writeToFile(pidKI.toString(4), this.configClass.results, intro);
						this.configClass.writeToFile("", this.configClass.operationLogs, intro);
					}				
				} catch (Exception e) {
					e.printStackTrace();
					String intro = "[" + timestamp + "] Handle Creating: Failed";
					this.configClass.writeToFile("Creating failed", this.configClass.results, intro);
					this.configClass.writeToFile("Creating failed", this.configClass.operationLogs, intro);
					this.configClass.writeToFile("Failed to create the Handle", this.configClass.errors, intro);
				}		
			} catch (Exception e) {
				String intro = "[" + timestamp + "] DTR Creating: Failed";
				this.configClass.writeToFile("Creating failed", this.configClass.results, intro);
				this.configClass.writeToFile("Creating failed", this.configClass.operationLogs, intro);
				this.configClass.writeToFile("Failed to create the Handle", this.configClass.errors, intro);
			}
		}	
	}
	
	public void updateProcess(File file, String repo) throws HandleException, Exception {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		JSONObject updateInfo = this.configClass.readUpdateFile(file.getAbsolutePath());
		
		String pid = updateInfo.getString("PID");
		JSONArray PIDKI = updateInfo.getJSONArray("PID KI");
		JSONArray Cordra = updateInfo.getJSONArray("Cordra");
		String replaceFile = "";
		if (updateInfo.has("replace File")){
			replaceFile = updateInfo.getString("replace File");
		}
		
		ArrayList<String[]> newPIDKI = new ArrayList<String[]>();
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		String date = df.format(new Date());
		
		String cordraID = "";
		
		if (!pid.isEmpty()) {	
			if (PIDKI.length() != 0) {
				ArrayList<String[]> indexTypeValue = handle.handleResolve(pid);
		
				for (String[] items : indexTypeValue) {
					for (Object update : PIDKI) {
						JSONObject updateItem = (JSONObject) update;
						if (items[1].equalsIgnoreCase(updateItem.getString("type"))) {
							items[2] = updateItem.getString("value").toString();
							
							newPIDKI.add(items);
						}else if (items[1].equalsIgnoreCase("dateModified")) {
							items[2] = date;
							newPIDKI.add(items);
						} else if (items[1].equalsIgnoreCase("digitalObjectLocation")) {
							if (!replaceFile.isEmpty()) {
								String oldLocation = items[2];
								String[] elements = oldLocation.split("/");
								String container = elements[elements.length-2];
								
								//******* Repository *******
								if (repo.equalsIgnoreCase("Fedora")) {
									FedoraClient fedora = new FedoraClient(this.configClass);
									items[2] = fedora.uploadFile(container, new File(replaceFile).getName(), replaceFile);
								}
								//******* Repository *******
								
								newPIDKI.add(items);
							}
						}else {
							items[2] = items[2].replaceAll("\"", "");
							newPIDKI.add(items);
						}			
					}
					if (items[1].equalsIgnoreCase("cordraID")) {
						cordraID = items[2].replaceAll("\"", "");
					}			
				}
								
				try {
					handle.modifyHandle(newPIDKI, pid);
					JSONArray newPIDKIoutput = handle.httpResolve(pid);
					
					String intro = "[" + timestamp + "] Handle Updating: " + pid;
					this.configClass.writeToFile("", this.configClass.update_pid, intro);
					this.configClass.writeToFile(newPIDKIoutput.toString(4), this.configClass.results, intro);
					this.configClass.writeToFile("", this.configClass.operationLogs, intro);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					String intro = "[" + timestamp + "] Handle Updating: Failed";
					this.configClass.writeToFile("Updating failed", this.configClass.results, intro);
					this.configClass.writeToFile("Updating failed", this.configClass.operationLogs, intro);
					this.configClass.writeToFile("Failed to update the Handle", this.configClass.errors, intro);
				}
			}else {
				JSONArray pidKI = handle.httpResolve(pid);
				for (Object items : pidKI) {
					JSONObject item = (JSONObject) items;
					if (item.getString("Type").equalsIgnoreCase("cordraID")){
						cordraID = item.getString("Value");
					}
				}
			}
						
			if (Cordra.length() != 0) {
				DOIP doip = new DOIP(this.configClass);
				
				JSONObject cordra = doip.retrieve(cordraID);
				JSONObject newCordra = (new Cordra()).UpdateCordraInstance(cordra.getJSONObject("attributes").getJSONObject("content").getString("id"), 
						Cordra, cordra);
				
				try {
					doip.update(cordra.getJSONObject("attributes").getJSONObject("content").getString("id"), newCordra);
					
					String intro = "[" + timestamp + "] Cordra Updating: " + cordra.getJSONObject("attributes").getJSONObject("content").getString("id");
					this.configClass.writeToFile(newCordra.toString(4), this.configClass.results, intro);
					this.configClass.writeToFile("", this.configClass.operationLogs, intro);					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					String intro = "[" + timestamp + "] Cordra Updating: Failed";
					this.configClass.writeToFile("Updating failed", this.configClass.results, intro);
					this.configClass.writeToFile("Updating failed", this.configClass.operationLogs, intro);
					this.configClass.writeToFile("Failed to update the Cordra", this.configClass.errors, intro);
				}	
			}
			
			
		}	
	}
	
	
	
	public void deleteProcess(String pid) throws HandleException, Exception{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		ArrayList<String[]> indexTypeValue = handle.handleResolve(pid);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		String date = df.format(new Date());
		String cordraID = "";
		ArrayList<String[]> newPIDKI = new ArrayList<String[]>();
		
		for (String[] items : indexTypeValue) {
			
			if (items[1].equalsIgnoreCase("cordraID")) {
				cordraID = items[2].replaceAll("\"", "");
				items[2] = "Cordra Object deleted";
				
				newPIDKI.add(items);
			}else if (items[1].equalsIgnoreCase("dateModified")) {
				items[2] = date;
				newPIDKI.add(items);
			}else if (items[1].equalsIgnoreCase("digitalObjectLocation")) {
				items[2] = "You cannot access the file";
				
				newPIDKI.add(items);
			}else {
				items[2] = items[2].replaceAll("\"", "");
				newPIDKI.add(items);
			}
				
		}
					
		try {	
			handle.modifyHandle(newPIDKI, pid);
			JSONArray newPIDKIoutput = handle.httpResolve(pid);
			
			String intro = "[" + timestamp + "] Handle Deleting: " + pid;
			this.configClass.writeToFile("", this.configClass.delete_pid, intro);
			this.configClass.writeToFile(newPIDKIoutput.toString(4), this.configClass.results, intro);
			this.configClass.writeToFile("", this.configClass.operationLogs, intro);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String intro = "[" + timestamp + "] Handle Deleting: Failed";
			this.configClass.writeToFile("Deleting failed", this.configClass.results, intro);
			this.configClass.writeToFile("Deleting failed", this.configClass.operationLogs, intro);
			this.configClass.writeToFile("Failed to delete the Handle", this.configClass.errors, intro);
		}
			
		try {	
			DOIP doip = new DOIP(this.configClass);
			doip.delete(cordraID);
			
			String intro = "[" + timestamp + "] Cordra Deleting: " + cordraID;
			this.configClass.writeToFile("", this.configClass.results, intro);
			this.configClass.writeToFile("", this.configClass.operationLogs, intro);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String intro = "[" + timestamp + "] Cordra Updating: Failed";
			this.configClass.writeToFile("Deleting failed", this.configClass.results, intro);
			this.configClass.writeToFile("Deleting failed", this.configClass.operationLogs, intro);
			this.configClass.writeToFile("Failed to delete the Cordra", this.configClass.errors, intro);
		}	
		
	}
	

}
