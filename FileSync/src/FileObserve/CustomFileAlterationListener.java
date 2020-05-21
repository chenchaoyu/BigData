package FileObserve;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import com.amazonaws.services.datapipeline.model.Field;

import FileSync.FileSyncer;

import java.io.File;

public class CustomFileAlterationListener extends FileAlterationListenerAdaptor{
	private String folderPath;
	public CustomFileAlterationListener(String folderPath) {
		// TODO Auto-generated constructor stub
		this.folderPath=folderPath;
	}
	
	@Override
	public void onDirectoryChange(final File file) {
		System.out.println("文件改变");
	}
	public void onFileCreate(final File file) {
		FileSyncer fileSyncer=new FileSyncer(folderPath);
		fileSyncer.UpLoadFile(file);
	}
	
	public void onFileDelete(final File file) {
		FileSyncer fileSyncer=new FileSyncer(folderPath);
		fileSyncer.deleteFile(file);
	}
	
}