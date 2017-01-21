package org.openntf.xrest.xsp.model;

import java.util.Map;

public class MappingField {

	private final String notesFieldName;
	private final String jsonName;
	private final String type;
	private final boolean isFormula;
	private final String formula;

	public MappingField(String name) {
		notesFieldName = name;
		jsonName = name;
		type = "";
		isFormula = false;
		formula = "";
	}

	public MappingField(String name, Map<String, Object> options) {
		notesFieldName = name;
		jsonName = getFromMap("json", options, name);
		type = getFromMap("type", options, "");
		isFormula = getFromMapAsBoolean("isformula", options, false);
		formula = getFromMap("formula", options, "");
	}

	private String getFromMap(String key, Map<String, Object> options, String name) {
		if (options.containsKey(key)) {
			return (String) options.get(key);
		}
		return name;
	}

	private boolean getFromMapAsBoolean(String key, Map<String, Object> options, Boolean defaultValue) {
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

	public String getType() {
		return type;
	}

	public boolean isFormula() {
		return isFormula;
	}

	public String getFormula() {
		return formula;
	}
}
