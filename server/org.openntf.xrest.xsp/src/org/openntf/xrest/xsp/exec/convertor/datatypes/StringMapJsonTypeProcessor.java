package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.util.List;

import org.openntf.xrest.xsp.exec.Context;

import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Item;
import lotus.domino.NotesException;

public class StringMapJsonTypeProcessor extends AbstractMapJsonTypeProcessor {

	@Override
	public void processItemToJsonObject(final Item item, final JsonObject jo, final String jsonPropertyName, Context context) throws NotesException {
		String value = item.getValueString();
		jo.putJsonProperty(jsonPropertyName, value);

	}

	@Override
	public void processValuesToJsonObject(final List<?> values, final JsonObject jo, final String jsonPropertyName, Context context) throws NotesException {
		if (values != null && !values.isEmpty()) {
			Object value = values.get(0);
			jo.putJsonProperty(jsonPropertyName, value + "");
		}
	}

	@Override
	public void processColumnValueToJsonObject(final Object clmnValue, final JsonObject jo, final String jsonPropertyName, Context context)
			throws NotesException {
		if (clmnValue instanceof String && !((String) clmnValue).isEmpty()) {
			jo.putJsonProperty(jsonPropertyName, "" + clmnValue);
		}
	}

}
