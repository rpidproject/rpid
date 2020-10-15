package indiana.edu.cordra;

import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;

public class AirboxInstanceJSON {
	
	
	public AirboxInstanceJSON() {
		
	}
	
	//users have to create their own instance objects by following the DTR schema
	public JSONObject AirboxCordraInstance(Hashtable<String, String> inputContent) {
		JSONObject object = new JSONObject();
	
		JSONArray profile = new JSONArray();
		
		JSONObject item = new JSONObject().put("identifier", Type.SENSORTYPE).put("value", inputContent.get("SENSORTYPE"));
		profile.put(item);
		item = new JSONObject().put("identifier", Type.DEVICEMODEL).put("value", inputContent.get("DEVICEMODEL"));
		profile.put(item);
		item = new JSONObject().put("identifier", Type.STARTDATE).put("value", inputContent.get("STARTDATE"));
		profile.put(item);
		item = new JSONObject().put("identifier", Type.ENDDATE).put("value", inputContent.get("ENDDATE"));
		profile.put(item);
		item = new JSONObject().put("identifier", Type.GEOLOCATION).put("value", inputContent.get("GEOLOCATION"));
		profile.put(item);
		item = new JSONObject().put("identifier", Type.STATUS).put("value", inputContent.get("STATUS"));
		profile.put(item);	
		item = new JSONObject().put("identifier", Type.KEYWORDS).put("value", inputContent.get("KEYWORDS"));
		profile.put(item);
		item = new JSONObject().put("identifier", Type.SAMEAS).put("value", inputContent.get("SAMEAS"));
		profile.put(item);
		item = new JSONObject().put("identifier", Type.SPARIALCOVERAGE).put("value", inputContent.get("SPARIALCOVERAGE"));
		profile.put(item);
		item = new JSONObject().put("identifier", Type.TEMPORALCOVERAGE).put("value", inputContent.get("TEMPORALCOVERAGE"));
		profile.put(item);
		item = new JSONObject().put("identifier", Type.VARIABLEMEASURED).put("value", inputContent.get("VARIABLEMEASURED"));
		profile.put(item);
		item = new JSONObject().put("identifier", Type.INCLUDEDINDATACATALOG).put("value", inputContent.get("INCLUDEDINDATACATALOG"));
		profile.put(item);
		item = new JSONObject().put("identifier", Type.DISTRIBUTIONCONTENTURL).put("value", inputContent.get("DISTRIBUTIONCONTENTURL"));
		profile.put(item);
		item = new JSONObject().put("identifier", Type.DISTRIBUTIONFILEFORMAT).put("value", inputContent.get("DISTRIBUTIONFILEFORMAT"));
		profile.put(item);
		item = new JSONObject().put("identifier", Type.READINGFORMAT).put("value", inputContent.get("READINGFORMAT"));
		profile.put(item);
		item = new JSONObject().put("identifier", Type.READINGTYPE).put("value", inputContent.get("READINGTYPE"));
		profile.put(item);
		item = new JSONObject().put("identifier", Type.READINGVALUE).put("value", inputContent.get("READINGVALUE"));
		profile.put(item);
		item = new JSONObject().put("identifier", Type.SIZE).put("value", inputContent.get("SIZE"));
		profile.put(item);
		item = new JSONObject().put("identifier", Type.READINGSTRUCTURE).put("value", inputContent.get("READINGSTRUCTURE"));
		profile.put(item);
		
		JSONObject content = new JSONObject();	
		content.put("id", "");
		content.put("name", inputContent.get("name"));
		content.put("description", inputContent.get("DESCRIPTION"));
		content.put("profile", profile);
		
		JSONObject attributes = new JSONObject();
		attributes.put("content", content);
		object.put("attributes", attributes);
		object.put("type", "AirboxSensorObject");
		
		return object;
	}
	
	
}
