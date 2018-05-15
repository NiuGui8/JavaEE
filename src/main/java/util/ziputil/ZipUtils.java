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

	 /**
   * 功能:压缩多个文件成一个zip文件
   * @param srcfile：源文件列表
   * @param zipfile：压缩后的文件
   */
  public static void zipFiles(File[] srcfile,File zipfile){
    byte[] buf=new byte[1024];
    try {
      //ZipOutputStream类：完成文件或文件夹的压缩
      ZipOutputStream out=new ZipOutputStream(new FileOutputStream(zipfile));
      for(int i=0;i<srcfile.length;i++){
        FileInputStream in=new FileInputStream(srcfile[i]);
        out.putNextEntry(new ZipEntry(srcfile[i].getName()));
        int len;
        while((len=in.read(buf))>0){
          out.write(buf,0,len);
        }
        out.closeEntry();
        in.close();
      }
      out.close();
      System.out.println("压缩完成.");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  /**
   * 功能:解压缩
   * @param zipfile：需要解压缩的文件
   * @param descDir：解压后的目标目录
   */
  public static void unZipFiles(File zipfile,String descDir){
    try {
      ZipFile zf=new ZipFile(zipfile);
      for(Enumeration entries=zf.entries();entries.hasMoreElements();){
        ZipEntry entry=(ZipEntry) entries.nextElement();
        String zipEntryName=entry.getName();
        InputStream in=zf.getInputStream(entry);
        OutputStream out=new FileOutputStream(descDir+zipEntryName);
        byte[] buf1=new byte[1024];
        int len;
        while((len=in.read(buf1))>0){
          out.write(buf1,0,len);
        }
        in.close();
        out.close();
        System.out.println("解压缩完成.");
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
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
