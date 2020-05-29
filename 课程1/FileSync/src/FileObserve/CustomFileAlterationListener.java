package FileObserve;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import com.amazonaws.services.datapipeline.model.Field;

import FileSync.FileSyncer;

import java.io.File;
import java.util.ArrayList;

public class CustomFileAlterationListener extends FileAlterationListenerAdaptor{
	
	public CustomFileAlterationListener() {
		// TODO Auto-generated constructor stub
		
	}
	@Override
	public void onStart(FileAlterationObserver observer) {
		// TODO Auto-generated method stub
		super.onStart(observer);
		
	}
	@Override
	public void onFileChange(final File file) {
		String name=file.getName();
		if(!name.equals("log.txt")) {
		FileSyncer fileSyncer=FileSyncer.getInstance();
		fileSyncer.upLoadFile(file);
		
		String filePathString=file.getAbsolutePath();
		System.out.println("�ɹ��޸��ļ�"+filePathString);
		}
	}
	public void onFileCreate(final File file) {
		String name=file.getName();
		if(file.length()>0&&!name.equals("log.txt")) {
			FileSyncer fileSyncer=FileSyncer.getInstance();
			fileSyncer.upLoadFile(file);
			String filePathString=file.getAbsolutePath();
			System.out.println("�ɹ��ϴ��ļ�"+filePathString);
		}
	
	}
	
	public void onFileDelete(final File file) {
		String name=file.getName();
		if(!name.equals("log.txt")) {
		FileSyncer fileSyncer=FileSyncer.getInstance();
		fileSyncer.deleteFile(file);
		}
	}
	
	

	
}