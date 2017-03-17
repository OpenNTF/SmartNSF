package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.util.List;

import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Item;
import lotus.domino.NotesException;

public class DoubleMapJsonTypeProcessor extends AbstractMapJsonTypeProcessor {

	@Override
	public void processItemToJsonObject(final Item item, final JsonObject jo, final String jsonPropertyName) throws NotesException {
		double value = item.getValueDouble();
		jo.putJsonProperty(jsonPropertyName, value);

	}

	@Override
	public void processValuesToJsonObject(final List<?> values, final JsonObject jo, final String jsonPropertyName) throws NotesException {
		if (values != null && !values.isEmpty()) {
			Object value = values.get(0);
			if (value instanceof Double) {
				Double doubleValue = (Double) value;
				jo.putJsonProperty(jsonPropertyName, doubleValue);
			}
		}
	}

	@Override
	public void processColumnValueToJsonObject(final Object clmnValue, final JsonObject jo, final String jsonPropertyName)
			throws NotesException {
		double value = (Double) clmnValue;
		jo.putJsonProperty(jsonPropertyName, value);

	}

}
