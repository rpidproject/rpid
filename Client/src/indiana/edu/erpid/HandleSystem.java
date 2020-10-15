package indiana.edu.erpid;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivateKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import indiana.edu.mapping.MappingEngine;
import indiana.edu.property.Config;
import net.handle.hdllib.AbstractMessage;
import net.handle.hdllib.AbstractResponse;
import net.handle.hdllib.AuthenticationInfo;
import net.handle.hdllib.CreateHandleRequest;
import net.handle.hdllib.ErrorResponse;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;
import net.handle.hdllib.ModifyValueRequest;
import net.handle.hdllib.Util;

public class HandleSystem {
	public Config configClass;
	public String handle;
	public String handlePrefix;
	public String admin_privKey_file;
	public String handle_admin_identifier;
	public String password;
	
	public HandleSystem(Config config) throws IOException{
		this.configClass = config;
		JSONObject basicConfig = this.configClass.config_basicConfig;
		JSONObject handle = (JSONObject) basicConfig.get("Handle System");
		
		this.handle = handle.get("API URL").toString();
		this.handlePrefix = handle.get("prefix").toString();
		this.admin_privKey_file = handle.get("private key file").toString();
		this.handle_admin_identifier = handle.get("handle admin identifier").toString();
		this.password = handle.get("private key file password").toString();
	}
	
	public JSONArray httpResolve(String pidID) throws Exception {
		
		String handleURL = this.handle + pidID;
		URL object = new URL(handleURL);
		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setRequestMethod("GET");
		
		StringBuilder content;

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {

            String line;
            content = new StringBuilder();

            while ((line = in.readLine()) != null) {
                content.append(line);
                content.append(System.lineSeparator());
            }
        }
        String jsonString = content.toString();
        
        JSONObject output = new JSONObject(jsonString);
        
        HashMap<JSONObject, String> resolved = (new MappingEngine(this.configClass)).mappingHandleObject(output);
        
        JSONArray newPID = new JSONArray();
		for (Map.Entry item : resolved.entrySet()) {
			JSONObject temp = new JSONObject();
			temp.put("Type", (JSONObject) item.getKey());
			temp.put("Value", item.getValue().toString());
			newPID.put(temp);
		}
		
		return newPID;
		
	}
	
	
	public ArrayList<String[]> handleResolve(String pidID) throws HandleException, Exception {
		HandleValue values[] = new HandleResolver().resolveHandle(pidID, null, null);
		ArrayList<String[]> result = new ArrayList<String[]>();
		for (int i = 0; i < values.length; i++) {
			String[] content = values[i].toString().split(" ");
			String[] extract = new String[3];
			extract[0] = content[1].split("=")[1];
			extract[1] = content[2].split("=")[1];
			extract[2] = content[4];
			result.add(extract);
			
		}
		
		return result;
	}
	
	
	public String createHandle(JSONObject object) throws Exception {

		HandleResolver resolver = new HandleResolver();
		
		File privKeyFile = new File(this.admin_privKey_file);
		PrivateKey hdl_adm_priv = net.handle.hdllib.Util.getPrivateKeyFromFileWithPassphrase(privKeyFile, this.password);
		byte adm_handle[] = Util.encodeString(this.handle_admin_identifier);
		AuthenticationInfo auth = new net.handle.hdllib.PublicKeyAuthenticationInfo(adm_handle, 300, hdl_adm_priv);
			
		String handle_identifier = object.getString("PID");	
		
		HandleValue[] new_values = new HandleValue[object.keySet().size()];
		
		int count = 0;
		for (Object attribute : object.keySet()){
			HandleValue new_value = new HandleValue(count+1, Util.encodeString(attribute.toString()), Util.encodeString(object.get(attribute.toString()).toString()));
			new_values[count] = new_value;
			count++;
		}
		
		CreateHandleRequest assign_request = new CreateHandleRequest(Util.encodeString(handle_identifier), new_values,
				auth);
		
		AbstractResponse response_assign = resolver.processRequestGlobally(assign_request);
		
		if (response_assign.responseCode == AbstractMessage.RC_SUCCESS) {
			return handle_identifier;

		} else {
			byte values[] = ((ErrorResponse) response_assign).message;
			for (int i = 0; i < values.length; i++) {
				System.out.print(String.valueOf(values[i]));
			}
			return "Failed";
		}
	}
	
	public String modifyHandle(ArrayList<String[]> object, String handle_identifier) throws Exception {

		HandleResolver resolver = new HandleResolver();
		
		File privKeyFile = new File(this.admin_privKey_file);
		PrivateKey hdl_adm_priv = net.handle.hdllib.Util.getPrivateKeyFromFileWithPassphrase(privKeyFile, this.password);
		byte adm_handle[] = Util.encodeString(this.handle_admin_identifier);
		AuthenticationInfo auth = new net.handle.hdllib.PublicKeyAuthenticationInfo(adm_handle, 300, hdl_adm_priv);
	
		HandleValue[] new_values = new HandleValue[object.size()];
		
		int count = 0;
		for (String[] item : object){
			HandleValue new_value = new HandleValue(Integer.parseInt(item[0]), Util.encodeString(item[1].toString()), Util.encodeString(item[2].toString()));
			new_values[count] = new_value;
			count++;
		}
		
		ModifyValueRequest assign_request = new ModifyValueRequest(Util.encodeString(handle_identifier), new_values,
				auth);
		
		AbstractResponse response_assign = resolver.processRequestGlobally(assign_request);
		
		if (response_assign.responseCode == AbstractMessage.RC_SUCCESS) {
			return handle_identifier;

		} else {
			byte values[] = ((ErrorResponse) response_assign).message;
			for (int i = 0; i < values.length; i++) {
				System.out.print(String.valueOf(values[i]));
			}
			return "Failed";
		}
	}
	
	
	public JSONObject pidKI(String location, String etag, String cordraID) {
		JSONObject pidKI = new JSONObject();
		String uuid = UUID.randomUUID().toString();
		String handle_identifier = this.handlePrefix + "/" + uuid;
		pidKI.put("PID", handle_identifier);
		pidKI.put("KernelInformationProfile", "test");
		pidKI.put("digitalObjectType", "test");
		pidKI.put("digitalObjectLocation", location);
		pidKI.put("digitalObjectPolicy", "test");
		pidKI.put("etag", etag);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		String date = df.format(new Date());
		pidKI.put("dateModified", date);
		pidKI.put("dateCreated", date);
		pidKI.put("version", "0.1");
		pidKI.put("wasDerivedFrom", "null");
		pidKI.put("specializationOf", "null");
		pidKI.put("wasRevisionOf", "null");
		pidKI.put("hadPrimarySource", "null");
		pidKI.put("wasQuotedFrom", "null");
		pidKI.put("alternateOf", "null");
		pidKI.put("cordraID", cordraID);
		return pidKI;
	}

	
}
