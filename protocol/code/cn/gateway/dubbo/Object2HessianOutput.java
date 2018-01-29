package cn.gateway.dubbo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.caucho.hessian.io.Hessian2Output;

public class Object2HessianOutput {

	static Map<String, Boolean> classMap = new ConcurrentHashMap<>();

	public static void writeByte(String className, Object value, Hessian2Output output) throws Exception {
		if (value == null) {
			output.writeNull();
			return;
		}
		boolean type = false;
		String val = String.valueOf(value);
		Boolean simpleObject = null;
		try {
			simpleObject = classMap.get(className);
			if (simpleObject == null) {
				if (Class.forName(className).isEnum())
					classMap.put(className, Boolean.FALSE);
				else
					classMap.put(className, Boolean.TRUE);
			}
		} catch (Exception e) {
			classMap.put(className, Boolean.FALSE);
		}
		simpleObject = classMap.get(className);
		if (simpleObject != null && !simpleObject.booleanValue()) {
			if (!val.startsWith("{") && !val.startsWith("[")) {
				type = true;
			}
		}
		if (type) {
			int ref = output.writeObjectBegin(className);
			if (ref < -1) {
				output.writeString("name");
				output.writeString(val);
				output.writeMapEnd();
			} else {
				if (ref == -1) {
					output.writeClassFieldLength(1);
					output.writeString("name");
					output.writeObjectBegin(className);
				}
				output.writeString(val);
			}
		} else
			output.writeObject(value);
	}

}