package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.model.MappingField;
import org.openntf.xrest.xsp.utils.TypeEnforcement;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;

public class IntegerArrayMapJsonTypeProcessor extends AbstractMapJsonTypeProcessor {

	@Override
	public void processItemToJsonObject(final Item item, final JsonObject jo, final String jsonPropertyName, Context context) throws NotesException {
		processValuesToJsonObject(item.getValues(), jo, jsonPropertyName, context);
	}

	@Override
	public void processValuesToJsonObject(final List<?> values, final JsonObject jo, final String jsonPropertyName, Context context) throws NotesException {
		if (values != null && !values.isEmpty()) {
			List<Integer> val = makeIntegerList(values);
			if (!val.isEmpty()) {
				jo.putJsonProperty(jsonPropertyName, val);
			}
		}
	}

	private List<Integer> makeIntegerList(final List<?> values) {
		List<Integer> convertedValues = new ArrayList<Integer>();
		for (Object value : values) {
			Integer val = TypeEnforcement.getAsInteger(value);
			if (null != val) {
				convertedValues.add(val);
			}
		}
		return convertedValues;
	}

	@Override
	public void processJsonValueToDocument(final JsonJavaObject jso, final Document doc, final MappingField mfField, Context context) throws NotesException {
		if (!jso.containsKey(mfField.getJsonName())) {
			return;
		}
		List<Integer> lstValues = new ArrayList<Integer>();
		JsonJavaArray array = jso.getAsArray(mfField.getJsonName());
		for (int nCounter = 0; nCounter < array.length(); nCounter++) {
			lstValues.add(array.getAsInt(nCounter));
		}
		doc.replaceItemValue(mfField.getNotesFieldName(), new Vector<Integer>(lstValues));
	}

	@Override
	public void processJsonValueToDocument(final Vector<?> values, final Document doc, final String fieldName, Context context) throws NotesException {
		super.processJsonValueToDocument(new Vector<Integer>(makeIntegerList(values)), doc, fieldName, context);
	}

}
