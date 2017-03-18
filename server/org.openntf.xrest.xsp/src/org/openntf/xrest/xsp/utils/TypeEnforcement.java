package org.openntf.xrest.xsp.utils;

public class TypeEnforcement {

	private TypeEnforcement() {
	}

	// Integer, Double, Boolean,

	public static Integer getAsInteger(final Object obj) {
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

	public static Double getAsDouble(final Object value) {
		if (value instanceof Double) {
			return ((Double) value).doubleValue();
		}
		return null;
	}
}
