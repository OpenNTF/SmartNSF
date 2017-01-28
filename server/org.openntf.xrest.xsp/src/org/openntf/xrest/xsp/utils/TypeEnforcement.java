package org.openntf.xrest.xsp.utils;

public class TypeEnforcement {

	private TypeEnforcement() {
	}

	public static Integer getAsInteger(Object obj) {
		if (obj instanceof Integer) {
			return (Integer) obj;
		}
		if (obj instanceof String) {
			return Integer.parseInt((String) obj);
		}
		if (obj instanceof Double) {
			return ((Double) obj).intValue();
		}
		return (Integer) obj;
	}

	public static Double getAsDouble(Object value) {
		// TODO Auto-generated method stub
		return null;
	}
}
