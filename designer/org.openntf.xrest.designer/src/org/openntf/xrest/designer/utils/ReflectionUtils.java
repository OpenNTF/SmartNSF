package org.openntf.xrest.designer.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtils {
	private static Map<String, Field> fieldMap = new HashMap<String, Field>();

	public static <T> Object getPrivateField(Class<T> clazz, String fieldName, Object target) {
		String key = clazz.getCanonicalName() + fieldName;
		Field field = fieldMap.get(key);
		try {
			if (field == null) {
				field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				fieldMap.put(key, field);
			}
			return field.get(target);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> void setPrivateField(Class<T> clazz, String fieldName, Object target, Object newValue) {
		String key = clazz.getCanonicalName() + fieldName;
		Field field = fieldMap.get(key);
		try {
			if (field == null) {
				field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				fieldMap.put(key, field);
			}
			field.set(target, newValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
