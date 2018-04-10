package util.ziputil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
	
	// 已经压缩的文件
	private static int hasZipCountFile = 0;
	/**
     * 创建ZIP文件
     * @param sourcePath 文件或文件夹路径
     * @param zipPath 生成的zip文件存在路径（包括文件名）
     * @param downfiles 需要压缩的文件
     */
    public static void createZip(String sourcePath, String zipPath, List<String> downfiles) {
    	hasZipCountFile = 0;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipPath);
            zos = new ZipOutputStream(fos);
            writeZip(new File(sourcePath), "", zos, downfiles);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {            	
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
            	e.printStackTrace();
            }

        }
    }

    private static void writeZip(File file, String parentPath, ZipOutputStream zos, List<String> downfiles) {
        if(file.exists()){
            if(file.isDirectory()){//处理文件夹
                parentPath+=file.getName()+File.separator;
                File [] files=file.listFiles();
                for(File f:files){
                	// 如果是文件夹或者就是需要下载的文件----压缩
                	if(f.isFile() && !downfiles.contains(f.getName())){    
                		continue;
                	}
                	writeZip(f, parentPath, zos, downfiles);
                	hasZipCountFile++;
                	if(hasZipCountFile == downfiles.size()){
                		break;
                	}
                }
            }else{
                FileInputStream fis=null;
                try {
                    fis=new FileInputStream(file);
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    byte [] content=new byte[1024];
                    int len;
                    while((len=fis.read(content))!=-1){
                        zos.write(content,0,len);
                        zos.flush();
                    }
                } catch (FileNotFoundException e) {
                	e.printStackTrace();
                } catch (IOException e) {
                	e.printStackTrace();
                }finally{
                    try {
                        if(fis!=null){
                            fis.close();
                        }
                    }catch(IOException e){
                    	e.printStackTrace();
                    }
                }
            }
        }
    } 
    
    public static void main(String[] args){
    	String sourcePath= "E:/XXX/e";
    	File file = new File(sourcePath);
    	System.out.println((double)file.length()/1024);
//    	String zipPath= "E:/Program File/apache-tomcat-8.0.9/webapps/WQIMC/softversion" + "/loadsql.zip";
//    	createZip(sourcePath, zipPath);
    }


}