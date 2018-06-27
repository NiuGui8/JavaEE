package com.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
*各种加密算法
*/
public class RandomCode
{
	/**
	 * 返回UUID
	 * @author jay.zhoujingjie
	 * @return UUID
	 */
	public static String getUUID()
	{
		return UUID.randomUUID().toString();
	}
	
	/**
	 * 随机生成5位有效数字，生成一个随机盐值
	 * @return
	 */
	public static int getSalt(int digit){
		String result="";
		Random random=new Random();
		//保证第一位大于等于1
		while(true){
			int temp=random.nextInt(10);
			if(temp>=1){
				result+=temp;
				break;
			}
		}
		for(int i=1;i<digit;i++){
			result+=random.nextInt(10);
		}
		return Integer.parseInt(result);
		
	}
	

	public static String encryptEncode(String pwd,String encryptType){
		String securityString=null;
		try {
			MessageDigest digest=MessageDigest.getInstance(encryptType);
			digest.update(pwd.getBytes("UTF-8"));
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

	
	private static String appendSecurity(byte[] securityByte)
	{
		StringBuffer buf = new StringBuffer();
		for(int j = 0,i;j < securityByte.length;j++)
		{
			i = securityByte[j];
			if(i < 0)
				i += 256;
			if(i < 16)
				buf.append("0");
			buf.append(Integer.toHexString(i));
		}
		return buf.toString();
	}	
	
	/**
	* 生成随机码
	* @param
	* @return 
	*/
	@SuppressWarnings("static-access")
	public static String getDynamicCode(String param) {
		try {
			Long date = ((new Date().getTime())/1800/1000);
			String dateString = date.toString();
			String password = "Luip@Admin" + param + dateString;
			String md5String = getMd5String(password);
			String str = md5String + "0000";
			Integer l11 = Integer.parseInt(str.substring(0, 6),16);
			Integer l12 = Integer.parseInt(str.substring(6,12),16);
			Integer l21 = Integer.parseInt(str.substring(12,18),16);
			Integer l22 = Integer.parseInt(str.substring(18,24),16);
			Integer l31 = Integer.parseInt(str.substring(24,30),16);
			Integer l32 = Integer.parseInt(str.substring(30),16);
			Integer l1 = l11 ^ l21 ^ l31;
			Integer l2 = l12 ^ l22 ^ l32;
			String hex1 = l1.toHexString(l1);
			String hex2 = l2.toHexString(l2);
			System.out.println(hex1);
			System.out.println(hex2);
			String hexString = (addZero(hex1) + addZero(hex2));
			String result = "";
			for(int i=0;i<12;i=i+2) {
				result = result + (Integer.valueOf(hexString.substring(i, i+2),16)%10);
			}
			return result;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static String addZero(String hex2) {
		if(hex2.length() == 6) {
			return hex2;
		}else {
			String zero = "";
			for(int i=0;i< 6 - hex2.length();i++) {
				zero = zero + "0";
			}
			return zero + hex2;
		}
	}

	/**
	* 获取标准MD5值
	* @param
	* @return 
	*/
	private static String getMd5String(String password) {
		 try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			 byte[] result = digest.digest(password.getBytes());
			 StringBuffer buffer = new StringBuffer();
			 // 把每一个byte 做一个与运算 0xff;
			 for (byte b : result) {
			     // 与运算
			     int number = b & 0xff;// 加盐
			     String str = Integer.toHexString(number);
			     if (str.length() == 1) {
			         buffer.append("0");
			     }
			     buffer.append(str);
			 }
			 return buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
//		System.out.println(encryptEncode("LabsunPT62-TN2018:06:26:18:2","md5"));
		System.out.println(getDynamicCode("D99999"));
		String dynamicCode = getDynamicCode("D99999");
		char[] result = dynamicCode.toCharArray();
		Double percent = 0d;
		long now = new Date().getTime();//当前时间毫秒值
		long nowHalf = now/1800/1000;//当前时间半小时值
		long next = (nowHalf + 1)*1800*1000;//下个半小时毫秒值
		System.out.println(now);
		System.out.println(next);
		System.out.println(next - now);
		System.out.println(1800*1000);
		percent = (((next - now)+0.00000000d)/((1800*1000) + 0.00000000d));
		System.out.println(result);
		System.out.println(percent*100);
	}
}
