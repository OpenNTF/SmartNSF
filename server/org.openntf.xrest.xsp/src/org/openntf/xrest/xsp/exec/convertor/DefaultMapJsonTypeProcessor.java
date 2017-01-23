package org.openntf.xrest.xsp.exec.convertor;

import java.util.List;

import org.openntf.xrest.xsp.model.MapJsonTypeProcessor;

import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Item;
import lotus.domino.NotesException;

public class DefaultMapJsonTypeProcessor implements MapJsonTypeProcessor {

	@Override
	public void processItemToJsonObject(Item item, JsonObject jo, String jsonPropertyName) throws NotesException {
		processValuesToJsonObject(item.getValues(), jo, jsonPropertyName);
	}

	@Override
	public void processValuesToJsonObject(List<?> values, JsonObject jo, String jsonPropertyName) throws NotesException {
		if (values.size() == 1) {
			jo.putJsonProperty(jsonPropertyName, values.get(0));
		} else {
			jo.putJsonProperty(jsonPropertyName, values);
		}

	}

}
