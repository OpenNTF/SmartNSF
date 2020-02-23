package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.util.List;

import org.openntf.xrest.xsp.exec.Context;

import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Item;
import lotus.domino.NotesException;

public class DefaultMapJsonTypeProcessor extends AbstractMapJsonTypeProcessor {

	@Override
	public void processItemToJsonObject(final Item item, final JsonObject jo, final String jsonPropertyName, Context context) throws NotesException {
		processValuesToJsonObject(item.getValues(), jo, jsonPropertyName, context);
	}

	@Override
	public void processValuesToJsonObject(final List<?> values, final JsonObject jo, final String jsonPropertyName, Context context) throws NotesException {
		if (values == null || values.isEmpty()) {
			return;
		}
		if (values.size() == 1) {
			jo.putJsonProperty(jsonPropertyName, values.get(0));
		} else {
			jo.putJsonProperty(jsonPropertyName, values);
		}

	}

	@Override
	public void processColumnValueToJsonObject(final Object clmnValue, final JsonObject jo, final String jsonPropertyName, Context context)
			throws NotesException {
		// TODO Auto-generated method stub

	}

}
