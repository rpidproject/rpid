package indiana.edu.engine;

import java.io.File;
import java.util.ArrayList;
import indiana.edu.property.Config;
import net.handle.hdllib.HandleException;

public class StartMapperClient {

	
	public static void main(String[] args) throws HandleException, Exception {
		String folderPath = args[0];
		String operation = args[1];
		
		Config config = new Config(folderPath);

		Process process = new Process(config);
		
		if (operation.equalsIgnoreCase(OperationInstance.CREATE)) {
			String uploadPath = config.uploadPath;
			File temp = new File(uploadPath);
			File[] allFiles = temp.listFiles();
			String repo = args[2];
			process.createProcess(allFiles, repo);	
		}
		if (operation.equalsIgnoreCase(OperationInstance.RESOLVE)) {
			ArrayList<String> pids = config.readInputFile(config.input_retrieve_pid);
			for (String pid : pids) {
				process.resolveProcess(pid);
			}
		}
		if (operation.equalsIgnoreCase(OperationInstance.UPDATE)) {
			String updatePath = config.updatePath;
			File temp = new File(updatePath);
			File[] allFiles = temp.listFiles();
			String repo = args[2];
			for (File file : allFiles) {
				process.updateProcess(file, repo);
			}	
		}
		if (operation.equalsIgnoreCase(OperationInstance.DELETE)) {
			ArrayList<String> pids = config.readInputFile(config.input_delete_pid);
			for (String pid : pids) {
				process.deleteProcess(pid);
			}
		}			
	}
}
