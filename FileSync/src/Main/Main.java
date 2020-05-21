package Main;

import java.io.File;
import java.io.FileFilter;


import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;



import FileObserve.CustomFileAlterationListener;
		
public class Main {
	public static void main(String[] args) throws Exception {
		FileFilter filter=FileFilterUtils.and(new MyFileFilter());
		String folderPath="E:/test/file";
		FileAlterationObserver observer = new FileAlterationObserver(folderPath,filter);
		FileAlterationListener listener=new CustomFileAlterationListener(folderPath);
		observer.addListener(listener);
		FileAlterationMonitor fileAlterationMonitor = new FileAlterationMonitor();
        //ע��۲���
        fileAlterationMonitor.addObserver(observer);
        //��������
        fileAlterationMonitor.start();
        //�����̱߳���ô�������
        Thread.sleep(1000000);

	}
	
}
class MyFileFilter implements IOFileFilter{

	@Override
	public boolean accept(File file) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean accept(File dir, String name) {
		// TODO Auto-generated method stub
		return true;
	}

    
}
