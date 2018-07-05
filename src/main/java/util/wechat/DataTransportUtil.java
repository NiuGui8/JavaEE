package util.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import com.szlabsun.wqimc.api.bridge.InstrumentBridge.Client;
/**
 * @see 数据转换工具?
 * @author lcl
 * @date 2017-6-22
 * */
public class DataTransportUtil {
	
	/**
	 * 任意类型对象转换成byte[]
	 * */
	public static byte[] transportObjectToByteArray(Object t) throws IOException {
		ByteArrayOutputStream byt=new ByteArrayOutputStream();
		ObjectOutputStream obj=new ObjectOutputStream(byt);
		obj.writeObject(t);
		byte[] bytes=byt.toByteArray();
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
	 * 根据socket获取客户端
	 * @param socket
	 * @return client
	 * */
	public static Client getClient(TSocket socket) {
		try {
			System.out.println("正在连接  192.168.2.141   9999");
			if(socket == null) {
				socket = new TSocket("localhost",9999);
			}
			TFramedTransport transport = new TFramedTransport(socket);
			socket.open();
			TCompactProtocol protocol = new TCompactProtocol(transport);
			Client client = new Client(protocol);
			System.out.println("连接成功 ...");
			return client;
		} catch (TTransportException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 将前台传过来的UUID字符串转换成bytebuffer格式
	 * */
	public static ByteBuffer transportUUIDStringToBytebuffer(String uuid) {
		if("".equals(uuid) || uuid == null) {return null;}
		try {
			UUID id = UUID.fromString(uuid);
			ByteBuffer buf = ByteBuffer.allocate(16);
			buf.putLong(id.getMostSignificantBits());
			buf.putLong(id.getLeastSignificantBits());
			buf.flip();
			return buf;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	 /** 
     *获取openid和secret
     * */
    public String queryOpenidAndSecret() {
    	logger.info("【获取openid和secret】：[wxInstrumentStatusAction -- queryOpenidAndSecret] \n参数：code -- "+code);
        String requestUrl = "https://api.weixin.qq.com/sns/jscode2session";  //请求地址 https://api.weixin.qq.com/sns/jscode2session  
        Map<String,String> requestUrlParam = new HashMap<String,String>();  
        requestUrlParam.put("appid", "wx5aa27dff3b0d4008");  //开发者设置中的appId  
        requestUrlParam.put("secret", "adeaac18c683a52589cc23d1069e3054"); //开发者设置中的appSecret  
        requestUrlParam.put("js_code", code); //小程序调用wx.login返回的code  
        requestUrlParam.put("grant_type", "authorization_code");    //默认参数  
          
        //发送post请求读取调用微信 https://api.weixin.qq.com/sns/jscode2session 接口获取openid用户唯一标识  
        try {
			byte[] post = HttpsUtil.post(requestUrl, "appid=wx5aa27dff3b0d4008&secret=adeaac18c683a52589cc23d1069e3054&js_code="+code+"&grant_type=authorization_code", "utf-8");
			String result = new String(post);  
			actionResult = ActionResult.getActionResult(new OpAttributes().add("result", result));
			return returnType();
		} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
			actionResult = ActionResult.getActionResult(new OpAttributes().add("result", null));
			return returnType();
		}
    }

    /** 
     *获取openid和secret
     * */
    public String queryOpenidAndSecret() {
    	logger.info("【获取openid和secret】：[wxInstrumentStatusAction -- queryOpenidAndSecret] \n参数：code -- "+code);
        String requestUrl = "https://api.weixin.qq.com/sns/jscode2session";  //请求地址 https://api.weixin.qq.com/sns/jscode2session  
        Map<String,String> requestUrlParam = new HashMap<String,String>();  
        requestUrlParam.put("appid", "wx5aa27dff3b0d4008");  //开发者设置中的appId  
        requestUrlParam.put("secret", "adeaac18c683a52589cc23d1069e3054"); //开发者设置中的appSecret  
        requestUrlParam.put("js_code", code); //小程序调用wx.login返回的code  
        requestUrlParam.put("grant_type", "authorization_code");    //默认参数  
          
        //发送post请求读取调用微信 https://api.weixin.qq.com/sns/jscode2session 接口获取openid用户唯一标识  
        try {
			byte[] post = HttpsUtil.post(requestUrl, "appid=wx5aa27dff3b0d4008&secret=adeaac18c683a52589cc23d1069e3054&js_code="+code+"&grant_type=authorization_code", "utf-8");
			String result = new String(post);  
			actionResult = ActionResult.getActionResult(new OpAttributes().add("result", result));
			return returnType();
		} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
			actionResult = ActionResult.getActionResult(new OpAttributes().add("result", null));
			return returnType();
		}
    }
    
    
    /**
     * 获取access_token
     * */
    public String getAccessToken() {
    	try {
			byte[] post = HttpsUtil.post("https://api.weixin.qq.com/cgi-bin/token", 
					"grant_type=client_credential&appid=wx5aa27dff3b0d4008&secret=adeaac18c683a52589cc23d1069e3054", "UTF-8");
			
			String replaceAll = new String(post).replace("{","").replace("}","").replaceAll("\"", "");
			String accessToken = replaceAll.split(",")[0].split(":")[1];
			return accessToken;
		} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    /**
     * byte数组到图片
     * **/
    public void byte2image(byte[] data,String path){
      if(data.length<3||path.equals("")) return;
      try{
      FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
      imageOutput.write(data, 0, data.length);
      imageOutput.close();
      logger.info("Make Picture success,Please find image in " + path);
      } catch(Exception ex) {
        logger.error("Exception: " + ex);
        ex.printStackTrace();
      }
    }

    /**
     * 生产二维码
     * @param ins 
     * */
	private byte[] getQRImage(Instrument ins) {
		logger.info("二维码生成中...");
		//判断仪器是否已连接上
		InstrumentBridgeService instance = InstrumentBridgeService.getInstance();
		String connectStatus = "2";
		//获取accessToken
		String accessToken = getAccessToken();
		//获取小程序码
		String requestUrl = "https://api.weixin.qq.com/wxa/getwxacode?access_token=" + accessToken;  //请求地址   
		String json = "{"
						+ "\"path\":\"pages/qrcode/qrcode?insId="+ins.getId()
						+ "&cs=" + connectStatus
			        	+ "&p=" + ((ins.getProportion() != null && ins.getProportion() >0) ? ins.getProportion() : "1")
			        	+ "&u=" + (ins.getUnit() == null ? "mg/L" : ins.getUnit()) +"\""
					+ "}";
        try {
			byte[] post = HttpsUtil.post(requestUrl
					, json
					, "utf-8");
			String result = new String(post);
			return post;
		} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
			return null;
		}  
	}
	
	  
	  /**
	   * 生成随机码
	   * @param num 随机码长度
	   * @return string
	   * */
	  public String getRandomCode(int num) {
		  String chars = "2356789qwertyupkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM";
		  char[] charArray = chars.toCharArray();
		  StringBuffer sb = new StringBuffer("");
		  
		  for(int i=0; i<num; i++) {
			  sb.append(charArray[(int)(Math.random()*54)]);
		  }
		  return sb.toString();
	  }

	
}
