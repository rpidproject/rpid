package indiana.edu.property;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class Config {
	
	public JSONObject config_basicConfig = new JSONObject();
	public JSONObject config_repos = new JSONObject();
	public String results;
	public String operationLogs;
	public String retrieve_pid;
	public String errors;
	public String update_pid;
	public String delete_pid;
	public String updatePath;
	public String uploadPath;
	public String updatePathAlt;
	
	public String input_retrieve_pid;
	public String input_delete_pid;
	
	
	public Config(String folderPath) throws IOException {
	
		//read the configurations 
		String basicConfig = folderPath+"/config/basicConfig.json";
		String repos = folderPath+"/config/repos.json";
		this.config_basicConfig = readConfig(basicConfig);
		this.config_repos = readConfig(repos);
		
		//get the output path
		this.results = folderPath+"/output/results.txt";
		this.operationLogs = folderPath+"/logs/operation_logs.log";
		this.retrieve_pid = folderPath+"/logs/retrieve_pid_list.log";
		this.errors = folderPath+"/logs/mapper_errors.log";
		this.update_pid = folderPath+"/logs/update_pid_list.log";
		this.delete_pid = folderPath+"/logs/delete_pid_list.log";
		this.updatePath = folderPath+"/input/update/";
		this.uploadPath = folderPath+"/input/upload/";
		this.updatePathAlt = folderPath+"/input/updateAlt";	
		this.input_retrieve_pid = folderPath+"/config/retrieve_pid.txt";
		this.input_delete_pid = folderPath+"/config/delete_pid.txt";
	}
	
	
	public JSONObject readConfig (String file) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));
	    StringBuilder out = new StringBuilder();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        out.append(line);
	    }
	    JSONObject output = new JSONObject(out.toString());
		
		reader.close();
		return output;
	}
	
	
	public JSONObject readUpdateFile (String file) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));
	    StringBuilder out = new StringBuilder();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        out.append(line);
	    }
	    JSONObject output = new JSONObject(out.toString());
		
		reader.close();
		return output;
	}
	
	
	public JSONArray readFile (String file) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));
	    StringBuilder out = new StringBuilder();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        out.append(line);
	    }
	    JSONArray output = new JSONArray(out.toString());
		
		reader.close();
		return output;
	}
	
	public ArrayList<String> readInputFile (String file) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));
	    ArrayList<String> out = new ArrayList<String>();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        out.add(line);
	    }
		reader.close();
		return out;
	}
	
	
	public void writeToFile(String result, String file, String intro) {
		try (FileWriter write = new FileWriter(file, true)) {
			write.write(intro);
			if (result == "") {
				write.write("\n\n");
			}else {
				write.write("\n");
	            write.write(result);
	            write.write("\n\n");
			}
			
            write.flush();
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
}
