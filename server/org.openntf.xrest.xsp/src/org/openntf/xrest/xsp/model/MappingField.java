package org.openntf.xrest.xsp.model;

import java.util.Map;

public class MappingField {

	private final String notesFieldName;
	private final String jsonName;
	private final MapJsonType type;
	private final boolean isFormula;
	private final boolean isReadonly;
	private final boolean isWriteonly;
	private final String formula;

	public MappingField(final String name) {
		notesFieldName = name;
		jsonName = name;
		type = MapJsonType.DEFAULT;
		isFormula = false;
		formula = "";
		isReadonly = false;
		isWriteonly = false;
	}

	public MappingField(final String name, final Map<String, Object> options) {
		notesFieldName = name;
		jsonName = getFromMap("json", options, name);
		type = getFromMapAsMTP("type", options, MapJsonType.DEFAULT);
		isFormula = getFromMapAsBoolean("isformula", options, false);
		isReadonly = getFromMapAsBoolean("readonly", options, false);
		isWriteonly = getFromMapAsBoolean("writeonly", options, false);
		formula = getFromMap("formula", options, "");
	}

	private MapJsonType getFromMapAsMTP(final String key, final Map<String, Object> options, final MapJsonType defaultValue) {
		if (options.containsKey(key)) {
			String type = (String) options.get(key);
			return MapJsonType.valueOf(type);
		}
		return defaultValue;
	}

	private String getFromMap(final String key, final Map<String, Object> options, final String name) {
		if (options.containsKey(key)) {
			return (String) options.get(key);
		}
		return name;
	}

	private boolean getFromMapAsBoolean(final String key, final Map<String, Object> options, final Boolean defaultValue) {
		if (options.containsKey(key)) {
			return (Boolean) options.get(key);
		}
		return defaultValue;
	}

	public String getNotesFieldName() {
		return notesFieldName;
	}

	public String getJsonName() {
		return jsonName;
	}

	public MapJsonType getType() {
		return type;
	}

	public boolean isFormula() {
		return isFormula;
	}

	public String getFormula() {
		return formula;
	}

	public boolean isReadOnly() {
		return isReadonly;
	}

	public boolean isWriteOnly() {
		return isWriteonly;
	}

	@Override
	public String toString() {
		return "MappingField [" + (notesFieldName != null ? "notesFieldName=" + notesFieldName + ", " : "") + (jsonName != null
				? "jsonName=" + jsonName + ", " : "") + (type != null ? "type=" + type + ", " : "") + "isFormula=" + isFormula
				+ ", isReadonly=" + isReadonly + ", isWriteonly=" + isWriteonly + ", " + (formula != null ? "formula=" + formula : "")
				+ "]";
	}

}
