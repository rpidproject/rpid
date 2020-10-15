package indiana.edu.mapping;

import java.io.IOException;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import indiana.edu.cordra.DOIP;
import indiana.edu.property.Config;
import net.handle.hdllib.HandleValue;

public class MappingEngine {
	public Config configClass;
	public String handlePrefix;
	public String dtrPrefix;
	public String profileDTRSuffix;
	
	public MappingEngine(Config config) throws IOException {
		this.configClass = config;
		JSONObject basicConfig = this.configClass.config_basicConfig;
		JSONObject handle = (JSONObject) basicConfig.get("Handle System");
		this.handlePrefix = handle.get("prefix").toString();
	}
	
	
	public HashMap<JSONObject, String> mappingHandleObject(HandleValue values[]) throws JSONException, Exception {
		DOIP doip = new DOIP(this.configClass);
		HashMap<JSONObject, String> newPID = new HashMap<JSONObject, String>();
		
		for (int i = 0; i < values.length; i++) {
			String[] list = values[i].toString().split(" ");
			String type = list[2].toString().split("=")[1];		
			String[] valueField = values[i].toString().split("\"");
			String value = "";
			
			
			if (valueField.length > 1) {
				value = valueField[1];
			}
			if (type.contains(this.handlePrefix)) {
				JSONObject resolvedType = doip.extractType(doip.retrieve(type));
				newPID.put(resolvedType, value);
			}else {
				JSONObject resolvedType = new JSONObject();
				resolvedType.put("name", type);
				newPID.put(resolvedType, value);
			}
							
		}
		
		return newPID;
		
	}
	
	
	public HashMap<JSONObject, String> mappingHandleObject(JSONObject values) throws JSONException, Exception {
		DOIP doip = new DOIP(this.configClass);
		HashMap<JSONObject, String> newPID = new HashMap<JSONObject, String>();
		
		JSONArray value = values.getJSONArray("values");
		
		for (Object items : value) {
			JSONObject item = (JSONObject) items;
			
			String type = item.getString("type");
			String dataValue = ((JSONObject) item.get("data")).get("value").toString();

			if (type.contains(this.handlePrefix)) {
				JSONObject resolvedType = doip.extractType(doip.retrieve(type));
				newPID.put(resolvedType, dataValue);
			}else {
				JSONObject resolvedType = new JSONObject();
				resolvedType.put("name", type);
				newPID.put(resolvedType, dataValue);
			}							
		}
		
		return newPID;
		
	}
	
	
	public JSONObject mappingDTRObject(JSONObject values) throws IOException{
		
		JSONObject attributes = values.getJSONObject("attributes");
		JSONObject metadata = attributes.getJSONObject("metadata");
		JSONObject content = attributes.getJSONObject("content");
		
		JSONArray profile = (JSONArray) content.remove("profile");
			
		JSONArray newProfile = new JSONArray();
		for (Object items : profile) {
			DOIP doip = new DOIP(this.configClass);
			JSONObject item = (JSONObject) items;
			String type = item.getString("identifier");
			JSONObject dtrContent = doip.retrieve(type);
			JSONObject typeContent = doip.extractType(dtrContent);
			JSONObject newItem = new JSONObject();
			newItem.put("identifier", typeContent).put("value", item.getString("value"));
			newProfile.put(newItem);
			
		}
		
		JSONObject newAttributes = new JSONObject();
		content.remove("profile");
		content.append("profile", newProfile);
		newAttributes.append("metadata", metadata).append("content", content);
		values.remove("attributes");
		values.append("attributes", newAttributes);
		return values;
	}
	
	
}
