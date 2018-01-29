package cn.gateway.dubbo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

public class Dubbo {
	protected static final int HEADER_LENGTH = 16;
	// magic header.
	protected static final short MAGIC = (short) 0xdabb;
	protected static final byte MAGIC_HIGH = Bytes.short2bytes(MAGIC)[0];
	protected static final byte MAGIC_LOW = Bytes.short2bytes(MAGIC)[1];
	// message flag.
	protected static final byte FLAG_REQUEST = (byte) 0x80;
	protected static final byte FLAG_TWOWAY = (byte) 0x40;
	protected static final byte FLAG_EVENT = (byte) 0x20;
	protected static final int SERIALIZATION_MASK = 0x1f;

	public static void main(String[] args) throws Exception {
		byte[] header = new byte[HEADER_LENGTH];
		Bytes.short2bytes(MAGIC, header);
		byte typeId = 2;
		header[2] = (byte) (FLAG_REQUEST | typeId);
		header[2] |= FLAG_TWOWAY;
		header[3] = 20;
		Bytes.long2bytes(System.nanoTime(), header, 4);
		byte[] head_magic = new byte[2];
		System.arraycopy(header, 0, head_magic, 0, 2);
		System.out.println("魔数" + Bytes.bytes2short(head_magic));
		byte fla = header[2];
		byte proto = (byte) (fla & SERIALIZATION_MASK);
		System.out.println("序列化协议:" + proto);
		System.out.println("标志" + header[2]);
		if ((fla & FLAG_REQUEST) == 0) {
			System.out.println("协议" + proto);
		}
		System.out.println("状态" + header[3]);
		byte[] requestId = new byte[8];
		System.arraycopy(header, 4, requestId, 0, 8);
		System.out.println(Bytes.bytes2long(requestId));
		byte[] bodySize = new byte[4];
		System.arraycopy(header, 12, bodySize, 0, 4);
		System.out.println(Bytes.bytes2int(bodySize));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		String version = "";
		String[] paraType = "com.alibaba.dubbo.demo.EnumTest,java.lang.String".split(",");
		String method = "test";
		Map<String, String> map = new HashMap<String, String>();
		String classPath = "com.alibaba.dubbo.demo.DemoService";
		Hessian2Output ho = new Hessian2Output(bos);
		ho.writeString("2.5.3-Rc1");
		ho.writeString(classPath);
		ho.writeString(version);
		map.put("path", classPath);
		map.put("interface", classPath + ", version=" + version);
		ho.writeString(method);
		ho.writeString(ReflectUtils.getDesc(paraType));
		int index = 0;
		List<Object> paras = JSON.parseArray("[\"SAT\",\"打破阿里系讹诈\"]", Object.class);
		for (String p : paraType) {
			Object para = null;
			try {
				para = paras.get(index++);
			} catch (Exception e) {
			}
			Object2HessianOutput.writeByte(p, para, ho);
		}
		ho.writeObject(map);
		ho.flush();
		byte[] b = bos.toByteArray();
		int len = b.length;
		Bytes.int2bytes(len, header, 12);
		SocketChannel channel = SocketChannel.open(new InetSocketAddress("10.100.0.212", 20880));
		Socket socket = channel.socket();
		OutputStream out = socket.getOutputStream();
		out.write(header);
		out.write(b);
		out.flush();
		InputStream is = socket.getInputStream();
		byte[] returnHeader = new byte[16];
		is.read(returnHeader);
		Hessian2Input hi = new Hessian2Input(is);
		int type = hi.readInt();
		System.err.println(type);
		System.err.println(JSON.toJSON(hi.readObject()));
		socket.close();
	}
}