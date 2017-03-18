package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.util.List;

import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Item;
import lotus.domino.NotesException;

public class IntegerMapJsonTypeProcessor extends AbstractMapJsonTypeProcessor {

	@Override
	public void processItemToJsonObject(final Item item, final JsonObject jo, final String jsonPropertyName) throws NotesException {
		int value = item.getValueInteger();
		jo.putJsonProperty(jsonPropertyName, value);

	}

	@Override
	public void processValuesToJsonObject(final List<?> values, final JsonObject jo, final String jsonPropertyName) throws NotesException {
		if (values != null && !values.isEmpty()) {
			Object value = values.get(0);
			if (value instanceof Integer) {
				Integer intValue = (Integer) value;
				jo.putJsonProperty(jsonPropertyName, intValue);
			}
		}
	}

	@Override
	public void processColumnValueToJsonObject(final Object clmnValue, final JsonObject jo, final String jsonPropertyName)
			throws NotesException {
		int value = (Integer) clmnValue;
		jo.putJsonProperty(jsonPropertyName, value);
	}

}
