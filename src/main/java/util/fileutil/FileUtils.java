
package util.fileutil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Properties;

import com.szlabsun.wqimc.web.login.action.LoginAction;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * @Description: 文件工具类
 * @version 1.0.0
 * @author John
 * @date 2015年4月25日
 */
public class FileUtils {
	
	public static File getWebInfoClasses(String fileName){
		File file = new File(FileUtils.class.getResource("/").getPath());
		file = new File(file.getPath() + "/" + fileName);
		return file;
	}
	
	/**
	 * 获取文件夹（文件）大小
	 * @param fileName
	 * @return
	 */
	public static long getFileSize(File file){
		long size = 0;
		try{
			if(file.exists()){
				if(file.isDirectory()){
					File[] files = file.listFiles();
					for(File f : files){
						if(f.isDirectory()){
							size += getFileSize(f);
						} else {
							size += f.length();
						}
					}
				} else {
					size += file.length();
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return size;
	}
	
	/**
	 * 格式化文件大小
	 * @param fileSize
	 * @return
	 */
	public static String FormetFileSize(long fileSize){
		DecimalFormat df = new DecimalFormat("#########0.00");
        String fileSizeString = "";
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "GB";
        }
        return fileSizeString;

	}
	
	/**
	 * 写入信息到文件中
	 * @param message 写入的内容
	 * @param file 文件
	 * @param append	是否追加写入
	 * @throws IOException
	 */
	public static void writeMessageInFile(byte[] message, File file , boolean append) throws IOException{
		if(!file.exists()){
			file.createNewFile();
		} else {
			if(file.isDirectory()){
				return ;
			}
		}
		FileOutputStream writer = new FileOutputStream(file, append);
		writer.write(message);
		writer.flush();
		writer.close();
	}
	
//	public static void main(String[] args) throws IOException{
//		File  file = new File(FileUtils.class.getClassLoader().
//				getResource("20150430bcd6d48d.properties").getPath());
//		
//		InputStream  in =FileUtils.class.getClassLoader().getResourceAsStream("20150430bcd6d48d.properties");
//		Properties properties = new Properties();
//		properties.load(in);
//		in.close();
//		file.setWritable(true);
//		OutputStream fos = new FileOutputStream(file);
//		properties.setProperty("age", "32");
//		properties.store(fos, "");
//		fos.close();
//		file.setReadOnly();
//		System.out.println(properties.getProperty("name"));
//		System.out.println(FileUtils.class.getClassLoader().
//				getResource("20150430bcd6d48d.properties").getPath());
////		writeMessageInFile("3=10", file, true);
////    	System.out.println(FormetFileSize(getFileSize(file)));
//	}
	
	/**
	* 解压带密码的zip文件
	* @param filePath 要解压的文件名称
	* @param password 解压密码
	* @param folderName 解压到的文件夹
	* @return 
	*/
	public static void ExtractByLoopAllFiles(String filePath,String password,String folderName) {
		
		try {
			// Initiate ZipFile object with the path/name of the zip file.
			ZipFile zipFile = new ZipFile(filePath);
			
			// Check to see if the zip file is password protected 
			if (zipFile.isEncrypted()) {
				// if yes, then set the password for the zip file
				zipFile.setPassword(password);
			}
			
			// Get the list of file headers from the zip file
			List fileHeaderList = zipFile.getFileHeaders();
			
			// Loop through the file headers
			for (int i = 0; i < fileHeaderList.size(); i++) {
				FileHeader fileHeader = (FileHeader)fileHeaderList.get(i);
				// Extract the file to the specified destination
				zipFile.extractFile(fileHeader, folderName);
			}
		} catch (ZipException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	* 将文件夹打包为zip文件
	* @param folderName 要打包的文件夹
	* @param fileName 打包后的文件路径
	* @return 
	*/
	public static void AddFolder(String folderName,String fileName) {
			
			try {
				// Initiate ZipFile object with the path/name of the zip file.
				ZipFile zipFile = new ZipFile(fileName);
				
				// Folder to add
				String folderToAdd = folderName;
				
				// Initiate Zip Parameters which define various properties such
				// as compression method, etc.
				ZipParameters parameters = new ZipParameters();
				
				// set compression method to store compression
				parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
				
				// Set the compression level
				parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
				
				// Add folder to the zip file
				zipFile.addFolder(folderToAdd, parameters);
				
			} catch (ZipException e) {
				e.printStackTrace();
			}
		}
	
	/**
	* 删除文件夹及目录下所有文件和文件夹
	* @param folderName
	* @return 
	*/
	public static void deleteFolderAndFiles(String folderName) {
		File file = new File(folderName);
		if(!file.exists()) {return;}
		if(file.isFile()) {
			file.delete();
		}else {
			File[] listFiles = file.listFiles();
			for (File f : listFiles) {
				if(f.isFile()) {
					f.delete();
				}else {
					deleteFolderAndFiles(f.getPath());
				}
			}
			file.delete();
		}
	}
	
	public static void main(String[] args) {
		
	}

}
