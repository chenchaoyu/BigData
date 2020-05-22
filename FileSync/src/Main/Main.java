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
	
	private static void initialize() throws IOException {
		folderPath="E:/test/file";
		FileSyncer fileSyncer=new FileSyncer(folderPath);
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
			ArrayList<String> filePaths=FilePathList.getList();
			fileSyncer.clearBucket();
			System.out.println("��ʼ�����ɹ����bucket");
			
			
			
		
			for(File file:files) {
				
				fileSyncer.upLoadFile(file);
				String filePath=file.getAbsolutePath();
				FilePathList.Remove(filePath);
					
			}
			System.out.println("��ʼ�����ɹ��ϴ������ļ�");
			File file=new File(folderPath+"\\log.txt");
			Utils.createFile(file);
			Utils.writetxtfile("1", folderPath+"\\log.txt");
		}
		else{
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(logFile),"GBK"));
			BufferedReader br2=new BufferedReader(new InputStreamReader(new FileInputStream(logFile),"GBK"));
			String s=null;
			ArrayList<String> filePaths=new ArrayList<String>();
			br2.readLine();
			while((s=br2.readLine())!=null) {
				String[] strArr=s.split(" "+" "+" ");
				if(strArr.length==1) {
					filePaths.add(strArr[0]);
				}
			}
			FilePathList.initializeWithPaths(filePaths);
			String tag=br.readLine();
			if(tag.equals("0")) {
				String temp=null;
				
				//��ȡδ�ϴ��ļ�·��
			    temp=br.readLine();
			    String[] strArr=temp.split(" "+" "+" ");
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
				else {
						File file=new File(strArr[0]);
						fileSyncer.upLoadFile(file);		
				}
			    System.out.println("��ʼ�����ɹ��ϴ������ļ�");
			    File file=new File(folderPath+"\\log.txt");
				Utils.createFile(file);
				Utils.writetxtfile("1", folderPath+"\\log.txt");
			}
					
			if(tag.equals("1")) {
					//do nothing
			}
		}
			
			
		}
		
		
		
	
	
	public static void getFiles(String path,ArrayList<File> list) throws Exception {
        //Ŀ�꼯��fileList
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fileIndex : files) {
                //�������ļ���Ŀ¼������еݹ�����
                if (fileIndex.isDirectory()) {
                    getFiles(fileIndex.getPath(),list);
                } else {
                    //����ļ�����ͨ�ļ������ļ�������뼯����
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
