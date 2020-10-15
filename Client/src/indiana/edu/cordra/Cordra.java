package indiana.edu.cordra;

import org.json.JSONArray;
import org.json.JSONObject;

public class Cordra {
	
	public Cordra() {
		
	}
	
	
	public JSONObject UpdateCordraInstance(String id, JSONArray profileInput, JSONObject oldCordra) {

		JSONObject oldAttributes = oldCordra.getJSONObject("attributes");
		JSONObject oldContent = oldAttributes.getJSONObject("content");
		JSONArray oldProfile = oldContent.getJSONArray("profile");
		String name = oldContent.getString("name");
		String description = oldContent.getString("description");
		
		JSONArray newProfile = new JSONArray();
		
		for (Object oldItems : oldProfile) {
			JSONObject oldItem = (JSONObject) oldItems;
			int check = 0;
			for (int i = 0; i < profileInput.length(); i++) {
				JSONObject inputItem = (JSONObject) profileInput.get(i);
				
				if (inputItem.getString("identifier").equalsIgnoreCase(oldItem.getString("identifier"))) {
					newProfile.put(inputItem);
					profileInput.remove(i);
					check = 1;
				}
				
				if (inputItem.getString("identifier").equalsIgnoreCase("name")) {
					name = inputItem.getString("value");
					profileInput.remove(i);
				}
				
				if (inputItem.getString("identifier").equalsIgnoreCase("description")) {
					description = inputItem.getString("value");
					profileInput.remove(i);
				}
				
			}
			
			if (check == 0) {
				newProfile.put(oldItem);
			}
		}
		
		if (profileInput.length() != 0) {
			for (int i = 0; i < profileInput.length(); i++) {
				newProfile.put((JSONObject) profileInput.get(i));
			}
		}
		
		JSONObject newContent = new JSONObject();
		newContent.put("profile", newProfile);
		newContent.put("name", name);
		newContent.put("description", description);
		newContent.put("id", id);
		
		JSONObject newAttributes = new JSONObject();
		newAttributes.put("content", newContent);
		
		JSONObject object = new JSONObject();
		object.put("attributes", newAttributes);
		object.put("id", id);
		object.put("type", oldCordra.getString("type"));
		
		
		return object;
	}

}
