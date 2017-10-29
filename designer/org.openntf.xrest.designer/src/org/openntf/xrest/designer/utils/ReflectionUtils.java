package org.openntf.xrest.designer.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.openntf.xrest.xsp.log.SmartNSFLoggerFactory;

public class ReflectionUtils {
	private static Map<String, Field> fieldMap = new HashMap<String, Field>();

	private ReflectionUtils(){}
	
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
			SmartNSFLoggerFactory.DDE.errorp(ReflectionUtils.class, "getPrivateField",e, "Faild to get field (0) for Class (1)", fieldName, clazz.getName());
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
			SmartNSFLoggerFactory.DDE.errorp(ReflectionUtils.class, "setPrivateField",e, "Faild to set value (2) for field (0) for Class (1)", fieldName, clazz.getName(), newValue);
		}
	}
}
