package indiana.edu.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

public class ReadData {

	
	public ReadData() {
		
	}
	//please create your own program to parse the data	
	public Hashtable<String, String> readContent(String path) throws IOException {
		Hashtable<String, String> all = new Hashtable<String, String>();
			
		BufferedReader content = new BufferedReader(new FileReader(path));
		
		String line = content.readLine();
		
		
		if (line.contains("s_t4,s_h4,s_b2,s_d2,s_d0,s_d1,d_t5,d_h5")) {
			all.put("SENSORTYPE",  "s_t4,s_h4,s_b2,s_d2,s_d0,s_d1,d_t5,d_h5");
		}
		else if (line.contains("s_0,s_1,s_2,s_3,s_d0,s_d1,s_d2,s_t0,s_h0")) {
			all.put("SENSORTYPE", "s_0,s_1,s_2,s_3,s_d0,s_d1,s_d2,s_t0,s_h0");
		}
		else if (line.contains("s_t4,s_h4,s_b2,s_d2,s_d0,s_d1")) {
			all.put("SENSORTYPE", "s_t4,s_h4,s_b2,s_d2,s_d0,s_d1");
		}
		else if (line.contains("s_d0,s_d1,s_t4,s_h4")) {
			all.put("SENSORTYPE", "s_d0,s_d1,s_t4,s_h4");
		}else {
			all.put("SENSORTYPE", "Error of Sensor information");
		}
		
		String[] items = line.split(",");
		
		int lat = Arrays.asList(items).indexOf("gps_lat");
		int lon = lat + 1;
		int deviceType = Arrays.asList(items).indexOf("app");
		
		ArrayList<String> geolocation = new ArrayList<String>();
		
		String device = "";
		int count = 0;
		while ((line = content.readLine()) != null ) {
			String[] contents = line.split(",");
			String temp_location = "("+contents[lat]+","+contents[lon]+")";
			if (!geolocation.contains(temp_location)) {
				geolocation.add(temp_location);
			}

			if (count == 0) {
				device = contents[deviceType];
				all.put("READINGVALUE", line);
				count = 1;
			}			
		}
		
		if (!all.containsKey("READINGVALUE")) {
			all.put("READINGVALUE", "Empty");
		}

		String[] temp_name = path.split("/");
		String fileName = temp_name[temp_name.length-1];
		String fileType = fileName.substring(fileName.indexOf("."));
		String fileDate = fileName.substring(0, fileName.indexOf("."));
		fileName = device + " " +fileName.substring(0, fileName.indexOf("."));
		File temp_size = new File(path);	
		content.close();
			
		all.put("name", fileName);	
		all.put("DEVICEMODEL", device);
		all.put("STARTDATE", fileDate);
		all.put("ENDDATE", fileDate);
		all.put("GEOLOCATION", geolocation.toString());
		all.put("STATUS", "raw");
		all.put("DESCRIPTION", "Airbox Cordra Instance");
		all.put("KEYWORDS", "Sesnor reading/per day");
		all.put("SAMEAS", "no sameAs value");
		all.put("SPARIALCOVERAGE", geolocation.toString());
		all.put("TEMPORALCOVERAGE", fileDate);
		all.put("VARIABLEMEASURED", all.get("SENSORTYPE"));
		all.put("INCLUDEDINDATACATALOG", "unknown repository");
		all.put("DISTRIBUTIONCONTENTURL", "donwloadLocation");
		all.put("DISTRIBUTIONFILEFORMAT", fileType);
		all.put("READINGFORMAT", fileType);
		all.put("READINGTYPE", "Float and Char");
		all.put("SIZE", temp_size.length()+" bytes");
		all.put("READINGSTRUCTURE", "array ");
		
		return all;
	}
	

}
