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
	//创建文件
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
    //写入txt 内容不被覆盖 追加写入
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
     
     
    //写入txt内容 覆盖原内容
    public static boolean writetxtfile(String Content,String filepath) {
        boolean flag=false;
        try {
            //写入的txt文档的路径
            PrintWriter pw=new PrintWriter(filepath);
            //写入的内容
            pw.write(Content);
            pw.flush();
            pw.close();
            flag=true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
     
    //读取txt内容
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
