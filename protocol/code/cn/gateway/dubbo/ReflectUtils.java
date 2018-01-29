package cn.gateway.dubbo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReflectUtils {
	/**
	 * void(V).
	 */
	public static final char JVM_VOID = 'V';

	/**
	 * boolean(Z).
	 */
	public static final char JVM_BOOLEAN = 'Z';

	/**
	 * byte(B).
	 */
	public static final char JVM_BYTE = 'B';

	/**
	 * char(C).
	 */
	public static final char JVM_CHAR = 'C';

	/**
	 * double(D).
	 */
	public static final char JVM_DOUBLE = 'D';

	/**
	 * float(F).
	 */
	public static final char JVM_FLOAT = 'F';

	/**
	 * int(I).
	 */
	public static final char JVM_INT = 'I';

	/**
	 * long(J).
	 */
	public static final char JVM_LONG = 'J';

	/**
	 * short(S).
	 */
	public static final char JVM_SHORT = 'S';
	private static Map<String, Class<?>> parasTypeMap = new HashMap<String, Class<?>>();
	static {
		parasTypeMap.put(int.class.getName(), int.class);
		parasTypeMap.put(java.util.Map.class.getName(), java.util.Map.class);
		parasTypeMap.put(Long.class.getName(), Long.class);
		parasTypeMap.put(String.class.getName(), String.class);
		parasTypeMap.put(Double.class.getName(), Double.class);
		parasTypeMap.put(Float.class.getName(), Float.class);
		parasTypeMap.put(float.class.getName(), float.class);
		parasTypeMap.put(Integer.class.getName(), Integer.class);
		parasTypeMap.put(long.class.getName(), long.class);
		parasTypeMap.put(Character.class.getName(), Character.class);
		parasTypeMap.put(List.class.getName(), List.class);
		parasTypeMap.put(char.class.getName(), char.class);
		parasTypeMap.put(byte.class.getName(), byte.class);
		parasTypeMap.put(Date.class.getName(), Date.class);
		parasTypeMap.put(Set.class.getName(), Set.class);
		parasTypeMap.put(Object.class.getName(), Object.class);
		parasTypeMap.put(HashMap.class.getName(), HashMap.class);
		parasTypeMap.put(boolean.class.getName(), boolean.class);
		parasTypeMap.put(Boolean.class.getName(), Boolean.class);
		parasTypeMap.put(short.class.getName(), short.class);
		parasTypeMap.put(Short.class.getName(), Short.class);
		parasTypeMap.put(Byte.class.getName(), Byte.class);
	}

	public static String getDesc(String c) {
		try {
			return getDesc(Class.forName(c));
		} catch (ClassNotFoundException e) {
			StringBuilder ret = new StringBuilder();
			ret.append('L');
			ret.append(c.replace('.', '/'));
			ret.append(';');
			return ret.toString();
		}
	}

	public static String getDesc(String[] cs) {
		StringBuilder sb = new StringBuilder();
		for (String c : cs) {
			sb.append(getDesc(c));
		}
		return sb.toString();
	}

	public static String getDesc(Class<?> c) {
		StringBuilder ret = new StringBuilder();

		while (c.isArray()) {
			ret.append('[');
			c = c.getComponentType();
		}

		if (c.isPrimitive()) {
			String t = c.getName();
			if ("void".equals(t))
				ret.append(JVM_VOID);
			else if ("boolean".equals(t))
				ret.append(JVM_BOOLEAN);
			else if ("byte".equals(t))
				ret.append(JVM_BYTE);
			else if ("char".equals(t))
				ret.append(JVM_CHAR);
			else if ("double".equals(t))
				ret.append(JVM_DOUBLE);
			else if ("float".equals(t))
				ret.append(JVM_FLOAT);
			else if ("int".equals(t))
				ret.append(JVM_INT);
			else if ("long".equals(t))
				ret.append(JVM_LONG);
			else if ("short".equals(t))
				ret.append(JVM_SHORT);
		} else {
			ret.append('L');
			ret.append(c.getName().replace('.', '/'));
			ret.append(';');
		}
		return ret.toString();
	}
}
