package indiana.edu.repository;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.activation.MimetypesFileTypeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import indiana.edu.property.Config;

import java.io.FileReader;


public class FedoraClient {
	public Config configClass;
	public String HOME_ADDRESS;
	public String output_address;
	
	public FedoraClient(Config config) throws IOException {
		this.configClass = config;
		JSONObject repoConfig = this.configClass.config_repos;
		JSONArray list = repoConfig.getJSONArray("repo list");
		for (Object items : list) {
			JSONObject item = (JSONObject) items;
			if (item.getString("repo name").contentEquals("fedora")) {
				this.HOME_ADDRESS = item.getString("repo host");
			}
		}
		
	}
	
	
	public String getOutputAddress() {
		return this.output_address;
	}
	
	
	public void disconnection(HttpURLConnection httpConn){
		httpConn.disconnect();	
	}
	
	public boolean validHttp(HttpURLConnection httpConn){
		int responseCode;
		boolean re = false;
		try {
			responseCode = httpConn.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK){
				re = true;
			}else{
				re = false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			re = false;
			e.printStackTrace();
		}	
		return re;		
	}
	
	//create Container with specified path
	public void createContainer(String containerName){
		try{		
			URL url = new URL(HOME_ADDRESS+containerName);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			
			httpConn.setRequestMethod("PUT");
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(httpConn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}
	
	//upload files
	public String uploadFile(String containerName, String fileName, String file_path){
		try{
			URL url = new URL(HOME_ADDRESS+containerName+"/"+fileName);
			this.output_address = HOME_ADDRESS+containerName+"/"+fileName;
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			File file = new File(file_path);
			httpConn.setDoOutput(true);		
			httpConn.setRequestMethod("PUT");
			httpConn.setRequestProperty("Content-Type", new MimetypesFileTypeMap().getContentType(file));
			httpConn.setRequestProperty("Content-disposition", "attachment; filename="+ file_path.substring(file_path.lastIndexOf("/")+1));
					
			OutputStream out = httpConn.getOutputStream();
			
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			byte[] buffer = new byte[4096];
		    int length;
		    while ((length = in.read(buffer)) > 0) {
		        out.write(buffer, 0, length);
		    } 
			
			in.close();			
			out.close();
					
			BufferedReader res = new BufferedReader(
			        new InputStreamReader(httpConn.getInputStream()));
			
			res.close();
			
			return this.output_address;
			
		}catch(Exception e){
			e.printStackTrace();
			return "error";
		}
		
	}
	
	
	public void uploadMetadataForContainer(String containerName, String file_path){
		try{
			HttpClient client = HttpClientBuilder.create().build();
			HttpPatch patch = new HttpPatch(HOME_ADDRESS+containerName);
			patch.addHeader("Content-Type", "application/sparql-update");
			String input = "";
			BufferedReader br = new BufferedReader(new FileReader(new File(file_path)));
		    try {
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) {
		            sb.append(line);
		            sb.append("\n");
		            line = br.readLine();
		        }
		        input = sb.toString();
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
			HttpEntity a = new StringEntity(input);			
			patch.setEntity(a);
			HttpResponse dcResp = client.execute(patch);
			patch.releaseConnection();
			if (dcResp.getStatusLine().getStatusCode() == 204){
				System.out.println("Successfully update metadata");
			}else{
				System.out.println("Failed to update metadata");
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	
	public void uploadMetadataForFile(String containerName, String file_path){
		try{
			HttpClient client = HttpClientBuilder.create().build();
			HttpPatch patch = new HttpPatch(HOME_ADDRESS+containerName+"/fcr:metadata");
			patch.addHeader("Content-Type", "application/sparql-update");
			String input = "";
			BufferedReader br = new BufferedReader(new FileReader(new File(file_path)));
		    try {
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) {
		            sb.append(line);
		            sb.append("\n");
		            line = br.readLine();
		        }
		        input = sb.toString();
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
			HttpEntity a = new StringEntity(input);			
			patch.setEntity(a);
			HttpResponse dcResp = client.execute(patch);
			patch.releaseConnection();
			if (dcResp.getStatusLine().getStatusCode() == 204){
				System.out.println("Successfully update metadata");
			}else{
				System.out.println("Failed to update metadata");
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	//delete a file or a Container
	public int delete(HttpURLConnection httpConn){		
		try{

			httpConn.setRequestMethod("DELETE");
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(httpConn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			System.out.println("Delete path ["+httpConn.getURL()+"]");
			return httpConn.getResponseCode();
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	} 

	
	
	//delete the file with its tombstone
	public void deleteContainerORBinaryFile(String name) throws MalformedURLException, IOException{
		String access_address = HOME_ADDRESS + name;
		HttpURLConnection httpConn = (HttpURLConnection) (new URL(access_address)).openConnection();		
		if (httpConn != null){
			int response = delete(httpConn);
			if (response == 204){
				System.out.println("Delete the specified file/container");
				disconnection(httpConn);
				access_address += "/fcr:tombstone";
				HttpURLConnection httpConn_tombstone = (HttpURLConnection) (new URL(access_address)).openConnection();
				
				if (httpConn_tombstone != null){
					response = delete(httpConn_tombstone);
					disconnection(httpConn_tombstone);
					if (response == 204){
						System.out.println("Delete tombstone");
					}else{
						System.out.println("Cannot delete: no such path");
					}
				}else{
					System.out.println("Cannot delete tombstone");
				}				
			}else{
				System.out.println("Cannot delete: no such path");
			}
		}else{
			System.out.println("Cannot delete file/container and its' tombstone");
		}
	
	}
}



