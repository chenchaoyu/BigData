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
        //注册观察者
        fileAlterationMonitor.addObserver(observer);
        //启动监听
        fileAlterationMonitor.start();
        //让主线程别这么快结束。
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
