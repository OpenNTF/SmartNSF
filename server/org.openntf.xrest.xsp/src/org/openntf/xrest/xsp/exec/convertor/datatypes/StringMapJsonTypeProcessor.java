package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.util.List;

import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Item;
import lotus.domino.NotesException;

public class StringMapJsonTypeProcessor extends AbstractMapJsonTypeProcessor {

	@Override
	public void processItemToJsonObject(Item item, JsonObject jo, String jsonPropertyName) throws NotesException {
		String value = item.getValueString();
		jo.putJsonProperty(jsonPropertyName, value);

	}

	@Override
	public void processValuesToJsonObject(List<?> values, JsonObject jo, String jsonPropertyName) throws NotesException {
		if (values != null && !values.isEmpty()) {
			Object value = values.get(0);
			jo.putJsonProperty(jsonPropertyName, value + "");
		}
	}

}
