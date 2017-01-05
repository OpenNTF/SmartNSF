package org.openntf.xrest.xsp.model;

import java.util.Map;

public class MappingField {

	private final String notesFieldName;
	private final String jsonName;
	private final String type;

	public MappingField(String name) {
		notesFieldName = name;
		jsonName = name;
		type = "";
	}

	public MappingField(String name, Map<String, Object> options) {
		notesFieldName = name;
		jsonName = getFromMap("json", options, name);
		type = getFromMap("type", options, "");
	}

	private String getFromMap(String key, Map<String, Object> options, String name) {
		if (options.containsKey(key)) {
			return (String) options.get(key);
		}
		return name;
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
}
