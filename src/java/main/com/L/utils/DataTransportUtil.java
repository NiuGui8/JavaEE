package com.szlabsun.wqimc.ins.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.szlabsun.wqimc.ins.dao.InstrumentMapper;
/**
 * @see 数据转换工具类
 * @author lcl
 * @date 2017-6-22
 * */
public class DataTransportUtil {
	
	private static final String conf = "spring-mybatis.xml";  
    private static ApplicationContext ac = new ClassPathXmlApplicationContext(conf);
	
	/**
	 * 任意类型对象转换成byte[]
	 * */
	public static byte[] transportObjectToByteArray(Object t) throws IOException {
		ByteArrayOutputStream byt=new ByteArrayOutputStream();
		ObjectOutputStream obj=new ObjectOutputStream(byt);
		obj.writeObject(t);
		byte[] bytes=byt.toByteArray();
		byt.close();
		obj.close();
		return bytes;
	}
	
	/**
	 * ByteBuffer类型转换成UUID类型
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * */
	public static UUID transportByteBufferToUUID(ByteBuffer uuid) throws IOException, ClassNotFoundException {
		byte[] uuids = new byte[uuid.remaining()];
		uuid.get(uuids);
		ByteArrayInputStream byteInt=new ByteArrayInputStream(uuids);
		ObjectInputStream objInt=new ObjectInputStream(byteInt);
		UUID insuuid=(UUID)objInt.readObject();
		byteInt.close();
		objInt.close();
		return insuuid;
	}
	
	/**
	 * 将byte[]转换成任意对象
	 * @return 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * */
	public static  Object transportByteArrayToObject(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream byteInt=new ByteArrayInputStream(bytes);
		ObjectInputStream objInt=new ObjectInputStream(byteInt);
		return objInt.readObject();
	}
	
	/**
	 * @see 根据仪器uuid(String 类型)获取仪器id
	 * */
	public static int getInsIdByInsUUID(String uuid) {
		InstrumentMapper im = ac.getBean(InstrumentMapper.class);
		int insResult = im.selectInstrumentIdByUuid(uuid);
		return insResult; 
	}
	
	/**
	 * @see 根据仪器uuid(ByteBuffer类型)获取仪器id
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * */
	public static int getInsIdByInsUUID(ByteBuffer uuid) throws ClassNotFoundException, IOException {
		String insUuid = null;
		insUuid= transportByteBufferToUUID(uuid).toString();
		InstrumentMapper im = ac.getBean(InstrumentMapper.class);
		Integer insResult = im.selectInstrumentIdByUuid(insUuid);
		if(insResult == null) {
			insResult = 0;
		}
		return insResult; 
	}
	
	/**
	 * 获取网络时间
	 * @param webUrl  要获取网络时间的网站
	 * */
	public static String getNetworkTime(String webUrl) {
        try {
            URL url = new URL(webUrl);
            URLConnection conn = url.openConnection();
            conn.connect();
            long dateL = conn.getDate();
            Date date = new Date(dateL);
            SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            return dateFormat.format(date);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
	
	//获取  中国科学院国家授时中心 的网络时间
	public static long getNetworkTime() {
        try {
            URL url = new URL("http://www.taobao.com");
            URLConnection conn = url.openConnection();
            conn.connect();
            long dateL = conn.getDate();
            return dateL;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }
	
	/**
	 * 验证仪器身份
	 * */
	public static String decode(String uuid){
		String securityString=null;
		try {
			MessageDigest digest=MessageDigest.getInstance("MD5");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh");
			String format = sdf.format(new Date());
			digest.update(format.getBytes("UTF-8"));
			digest.update(uuid.getBytes("UTF-8"));
			byte[] securityByte = digest.digest();
			for(int i = 1;i < securityByte.length;i += 2)
			{
				if(i % 2 != 0)
				{
					securityByte[i] = securityByte[i >>> 1];
				}
			}
			securityString = bytes2Hex(securityByte);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return securityString;
	}
	public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }
	
	public static void main(String[] args) {
		for(int i = 0;i < 100;i ++) {
			System.out.println(new Date());
			System.out.println(decode("123"));
			try {
				Thread.sleep(10000l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
