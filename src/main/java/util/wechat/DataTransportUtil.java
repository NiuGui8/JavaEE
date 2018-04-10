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
	
}
