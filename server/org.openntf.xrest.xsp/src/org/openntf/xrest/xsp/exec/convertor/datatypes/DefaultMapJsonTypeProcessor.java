package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.util.List;

import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Item;
import lotus.domino.NotesException;

public class DefaultMapJsonTypeProcessor extends AbstractMapJsonTypeProcessor {

	@Override
	public void processItemToJsonObject(Item item, JsonObject jo, String jsonPropertyName) throws NotesException {
		processValuesToJsonObject(item.getValues(), jo, jsonPropertyName);
	}

	@Override
	public void processValuesToJsonObject(List<?> values, JsonObject jo, String jsonPropertyName) throws NotesException {
		if (values == null || values.isEmpty()) {
			return;
		}
		if (values.size() == 1) {
			jo.putJsonProperty(jsonPropertyName, values.get(0));
		} else {
			jo.putJsonProperty(jsonPropertyName, values);
		}

	}

}
