package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.amazonaws.services.s3.model.PartETag;

public class Utils {
	//�����ļ�
    public static void createFile(File filename) {     
        try {
            if(!filename.exists()) {
                filename.createNewFile();              
            }
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    //д��txt ���ݲ������� ׷��д��
    public static boolean filechaseWrite(String Content,String filepath) {
        boolean flag=false;
        try {
                FileWriter fw=new FileWriter(filepath,true);
                fw.write(Content);
                fw.flush();
                fw.close();
                flag=true;
            }catch (Exception e) {
                //
             e.printStackTrace();
            }
        return flag;
    }
     
     
    //д��txt���� ����ԭ����
    public static boolean writetxtfile(String Content,String filepath) {
        boolean flag=false;
        try {
            //д���txt�ĵ���·��
            PrintWriter pw=new PrintWriter(filepath);
            //д�������
            pw.write(Content);
            pw.flush();
            pw.close();
            flag=true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
     
    //��ȡtxt����
    public static String readtxtFile(File file) {
        String sResult="";
        try {
            InputStreamReader reader=new InputStreamReader(new FileInputStream(file),"gbk");
            BufferedReader br=new BufferedReader(reader);
            String s=null;
            while((s=br.readLine())!=null) {
                sResult+=s;
                System.out.println(s);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return sResult;
    }
    
  
}
