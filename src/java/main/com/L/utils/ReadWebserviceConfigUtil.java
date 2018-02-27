package com.szlabsun.wqimc.ins.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ReadWebserviceConfigUtil {
    private String configPath=null; //配置文件的路径
    
    private Properties props=null; //配置文件对象
    
    private  static InputStream in;
    /**
     * 默认构造函数，自动找到classpath下的webservice.properties。
     */
    public ReadWebserviceConfigUtil() throws IOException{
        in = ReadWebserviceConfigUtil.class.getClassLoader().getResourceAsStream("webservice.properties");
        props = new Properties();
        props.load(in);
        //关闭资源
        in.close();
    }
    /**
     * 可以传文件名的构造
     * */
    public ReadWebserviceConfigUtil(String fileName) throws IOException{
        in = ReadWebserviceConfigUtil.class.getClassLoader().getResourceAsStream(fileName);
        props = new Properties();
        props.load(in);
        //关闭资源
        in.close();
    }
    public void close() throws IOException {
    	in.close();
    }
    /**
     * 根据key值读取配置的值
     */
    public String readValue(String key) throws IOException {
        return  props.getProperty(key);
    }
    
    /**
     * 读取properties的全部信息
     */
    public Map<String,String> readAllProperties() throws FileNotFoundException,IOException  {
        //保存所有的键值
        Map<String,String> map=new HashMap<String,String>();
        Enumeration en = props.propertyNames();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            String Property = props.getProperty(key);
            map.put(key, Property);
        }
        return map;
    }

    /**
     * 设置某个key的值,并保存至文件。
     */
    public void setValue(String key,String value) throws IOException {
        Properties prop = new Properties();
        InputStream fis = new FileInputStream(this.configPath);
        // 从输入流中读取属性列表（键和元素对）
        prop.load(fis);
        // 调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
        // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
        OutputStream fos = new FileOutputStream(this.configPath);
        prop.setProperty(key, value);
        // 以适合使用 load 方法加载到 Properties 表中的格式，
        // 将此 Properties 表中的属性列表（键和元素对）写入输出流
        prop.store(fos,"last update");
        //关闭文件
        fis.close();
        fos.close();
    }
    
}
