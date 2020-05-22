package FileObserve;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import com.amazonaws.services.datapipeline.model.Field;

import FileSync.FileSyncer;

import java.io.File;
import java.util.ArrayList;

public class CustomFileAlterationListener extends FileAlterationListenerAdaptor{
	private String folderPath;
	public CustomFileAlterationListener(String folderPath) {
		// TODO Auto-generated constructor stub
		this.folderPath=folderPath;
	}
	@Override
	public void onStart(FileAlterationObserver observer) {
		// TODO Auto-generated method stub
		super.onStart(observer);
		
	}
	@Override
	public void onFileChange(final File file) {
		if(file.getName()!="log.txt") {
		FileSyncer fileSyncer=new FileSyncer(folderPath);
		fileSyncer.upLoadFile(file);
		
		String filePathString=file.getAbsolutePath();
		System.out.println("成功修改文件"+filePathString);
		}
	}
	public void onFileCreate(final File file) {
		if(file.length()>0&&file.getName()!="log.txt") {
			FileSyncer fileSyncer=new FileSyncer(folderPath);
			fileSyncer.upLoadFile(file);
			String filePathString=file.getAbsolutePath();
			System.out.println("成功上传文件"+filePathString);
		}
	
	}
	
	public void onFileDelete(final File file) {
		if(file.getName()!="log.txt") {
		FileSyncer fileSyncer=new FileSyncer(folderPath);
		fileSyncer.deleteFile(file);
		}
	}
	
	

	
}