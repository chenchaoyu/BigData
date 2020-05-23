package Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;

import com.amazonaws.services.s3.model.PartETag;

import FileObserve.CustomFileAlterationListener;
import FileSync.FilePathList;
import FileSync.FileSyncer;
import Utils.Utils;
		
public class Main {
	private static String folderPath;
	public static void main(String[] args) throws Exception {
		
		initialize();
		
		FileFilter filter=FileFilterUtils.and(new MyFileFilter());
		FileAlterationObserver observer = new FileAlterationObserver(folderPath,filter);
		FileAlterationListener listener=new CustomFileAlterationListener();
		observer.addListener(listener);
		FileAlterationMonitor fileAlterationMonitor = new FileAlterationMonitor();
        //注册观察者
        fileAlterationMonitor.addObserver(observer);
        //启动监听
        fileAlterationMonitor.start();
        //让主线程别这么快结束。
        Thread.sleep(1000000);

	}
	
	private static void initialize() throws IOException {
		Scanner sc = new Scanner(System.in);
		System.out.println("请输入同步的本地路径(按回车使用默认路径E:/test):");
		folderPath=sc.nextLine();
		if(folderPath.equals("")) {
			folderPath="E:/test";
		}
		
		System.out.println("请输入accessKey:");
		String accessKey=sc.nextLine();
		System.out.println("请输入secretKey:");
		String secretKey=sc.nextLine();
		System.out.println("请输入bucketName:");
		String bucketName=sc.nextLine();
		FileSyncer.initialize(folderPath, accessKey, secretKey, bucketName);
		FileSyncer fileSyncer=FileSyncer.getInstance();
		File logFile=new File(folderPath+"\\log.txt");
		
		if(!logFile.exists()) {
			ArrayList<File> files=new ArrayList<File>();
			
			try {
				getFiles(folderPath, files);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			FilePathList.initializeWithFiles(files);
			fileSyncer.clearBucket();
			System.out.println("初始化：成功清空bucket");
			
			
			
			for(File file:files) {
				String filePath=file.getAbsolutePath();
				FilePathList.Remove(filePath);
				fileSyncer.upLoadFile(file);
				
				
					
			}
			System.out.println("初始化：成功上传所有文件");
			File file=new File(folderPath+"\\log.txt");
			Utils.createFile(file);
			Utils.writetxtfile("1", folderPath+"\\log.txt");
		}
		else{
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(logFile),"GBK"));
			BufferedReader br2=new BufferedReader(new InputStreamReader(new FileInputStream(logFile),"GBK"));
			String s=null;
			ArrayList<File> files=new ArrayList<File>();
			br2.readLine();
			while((s=br2.readLine())!=null) {
				String[] strArr=s.split("\\$\\$\\$\\$");
				if(strArr.length==1) {
					File file=new File(strArr[0]);
					files.add(file);
				}
			}
			FilePathList.initializeWithFiles(files);
			String tag=br.readLine();
			if(tag.equals("0")) {
				String temp=null;
				
				//获取未上传文件路径
			    temp=br.readLine();
			    if(temp!=null) {
			    	 String[] strArr=temp.split("\\$\\$\\$\\$");
					    if(strArr.length>=3) {
					    	File file=new File(strArr[0]);
							String upLoadId=strArr[1];
							int n=Integer.parseInt(strArr[2]);
							int len=strArr.length;
							int i=3;
							ArrayList<PartETag> partETags=new ArrayList<PartETag>();
							while(i+1<len) {
								int partNumber=Integer.parseInt(strArr[i]);
								String eTag=strArr[i+1];
								i+=2;
								PartETag partETag=new PartETag(partNumber, eTag);
								partETags.add(partETag);
							}
							fileSyncer.continueUpLoadFile(file, upLoadId,n,partETags);
						
					    }
					    for(File file:files) {
							
							fileSyncer.upLoadFile(file);
							String filePath=file.getAbsolutePath();
							FilePathList.Remove(filePath);
								
						}
					    System.out.println("初始化：成功上传所有文件");
						Utils.writetxtfile("1", folderPath+"\\log.txt");
					}else {
			    	System.out.println("初始化成功，无文件需上传");
			    	Utils.writetxtfile("1", folderPath+"\\log.txt");
			    }
			}
			   
					
			if(tag.equals("1")) {
					//do nothing
				System.out.println("初始化成功，无文件需上传");
		    	Utils.writetxtfile("1", folderPath+"\\log.txt");
			}
		}
			
			
		}
		
		
		
	
	
	public static void getFiles(String path,ArrayList<File> list) throws Exception {
        //目标集合fileList
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fileIndex : files) {
                //如果这个文件是目录，则进行递归搜索
                if (fileIndex.isDirectory()) {
                    getFiles(fileIndex.getPath(),list);
                } else {
                    //如果文件是普通文件，则将文件句柄放入集合中
                    list.add(fileIndex);
                    
                }
            }
        }
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
