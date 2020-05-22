package FileSync;

import java.io.File;
import java.util.ArrayList;


public class FilePathList {
	private static ArrayList<String> filePaths;
	
	public static void initializeWithFiles(ArrayList<File> files){
		if(filePaths==null) {
			filePaths=new ArrayList<String>();
		}
		else{
			filePaths.clear();
		}
		
		for(File file:files) {		
			String filePath=file.getAbsolutePath();
			filePaths.add(filePath);
		}
	}
	public static void initializeWithPaths(ArrayList<String> paths){
		filePaths=paths;
	}
	public static ArrayList<String> getList(){
		return filePaths;
	}
	public static void Remove(String path) {
		filePaths.remove(path);
	}
}
