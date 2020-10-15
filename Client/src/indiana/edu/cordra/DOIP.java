package indiana.edu.cordra;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import indiana.edu.property.Config;
import net.cnri.doip.DoipRequestHeaders;
import net.cnri.doip.client.DoipClient;
import net.cnri.doip.client.DoipClientResponse;
import net.cnri.doip.client.DoipConnection;

public class DOIP {
	private Config configClass;
	private String username;
	private String password;
	private String serviceID;
	private InetAddress addr;
	
	
	public DOIP(Config config) throws IOException {
		this.configClass = config;
		JSONObject basicConfig = this.configClass.config_basicConfig;
		JSONObject doip = (JSONObject) basicConfig.get("DTR DOIP");
		this.username = doip.getString("uesrname");
		this.password = doip.getString("password");
		this.serviceID = doip.getString("serviceID");
		this.addr = InetAddress.getByName(doip.getString("InetAddress"));
		
	
	}
	
	public JSONObject hello() throws IOException {
		DoipClient client = new DoipClient();
		DoipConnection conn = client.connect(addr, 9000);
		DoipRequestHeaders header = new DoipRequestHeaders();
		header.operationId = "0.DOIP/Op.Hello";
		header.targetId = serviceID;
		DoipClientResponse response = conn.sendCompactRequest(header);
		ArrayList<JsonElement> element = new ArrayList<JsonElement>();
		response.getOutput().forEach((x)->{
			try {
				element.add(x.getJson());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});;
		conn.close();
		client.close();
		JSONObject result = new JSONObject(element.get(0).toString());
		return result;
	}
	
	public JSONObject create(JSONObject dtrInput) throws IOException {
		DoipClient client = new DoipClient();
		DoipConnection conn = client.connect(addr, 9000);
		DoipRequestHeaders header = new DoipRequestHeaders();
		header.operationId = "0.DOIP/Op.Create";
		header.targetId = serviceID;
		JSONObject auth = new JSONObject();
		auth.put("username", this.username);
		auth.put("password", this.password);
		Gson gson = new Gson();
		JsonElement authentication = gson.fromJson(auth.toString(), JsonElement.class);
		header.authentication = authentication;
		
		gson = new Gson();
		JsonElement inputContent = gson.fromJson(dtrInput.toString(), JsonElement.class);
		
		header.input = inputContent;
		
		DoipClientResponse response = conn.sendCompactRequest(header);
		ArrayList<JsonElement> contains = new ArrayList<JsonElement>();
		response.getOutput().forEach((x)->{
			try {
				contains.add(x.getJson());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});;
		conn.close();
		client.close();
		JSONObject result = new JSONObject(contains.get(0).toString());
		
		return result;
	}
	
	public JSONObject retrieve(String objectID) throws IOException {
		DoipClient client = new DoipClient();
		DoipConnection conn = client.connect(addr, 9000);
		DoipRequestHeaders header = new DoipRequestHeaders();
		header.operationId = "0.DOIP/Op.Retrieve";
		header.targetId = objectID;

		DoipClientResponse response = conn.sendCompactRequest(header);
		ArrayList<JsonElement> element = new ArrayList<JsonElement>();
		response.getOutput().forEach((x)->{
			try {
				
				element.add(x.getJson());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});;
		conn.close();
		client.close();
		JSONObject result = new JSONObject(element.get(0).toString());
		return result;
	}
	
	
	public void update(String id, JSONObject dtrInput) throws IOException {
		DoipClient client = new DoipClient();
		DoipConnection conn = client.connect(addr, 9000);
		DoipRequestHeaders header = new DoipRequestHeaders();
		header.operationId = "0.DOIP/Op.Update";
		header.targetId = id;
		
		JSONObject auth = new JSONObject();
		auth.put("username", this.username);
		auth.put("password", this.password);
		Gson gson = new Gson();
		JsonElement authentication = gson.fromJson(auth.toString(), JsonElement.class);
		header.authentication = authentication;
		
		gson = new Gson();
	
		JsonElement input = gson.fromJson(dtrInput.toString(), JsonElement.class);
		
		header.input = input;
		
		DoipClientResponse response = conn.sendCompactRequest(header);
		conn.close();
		client.close();
		
	}
	
	
	public void delete(String id) throws IOException {
		DoipClient client = new DoipClient();
		DoipConnection conn = client.connect(addr, 9000);
		DoipRequestHeaders header = new DoipRequestHeaders();
		header.operationId = "0.DOIP/Op.Delete";
		header.targetId = id;
		
		JSONObject auth = new JSONObject();
		auth.put("username", this.username);
		auth.put("password", this.password);
		Gson gson = new Gson();
		JsonElement authentication = gson.fromJson(auth.toString(), JsonElement.class);
		header.authentication = authentication;
		
		gson = new Gson();
		
		DoipClientResponse response = conn.sendCompactRequest(header);
		conn.close();
		client.close();
		
	}
	
	public JSONObject extractType(JSONObject input) {
	
		JSONObject attributes = input.getJSONObject("attributes");
		JSONObject content = attributes.getJSONObject("content");
		
		content.remove("representationsAndSemantics");
		return content;
	}
	
}
