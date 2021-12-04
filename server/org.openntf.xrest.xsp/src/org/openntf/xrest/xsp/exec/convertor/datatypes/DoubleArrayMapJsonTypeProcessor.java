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

public class DoubleArrayMapJsonTypeProcessor extends AbstractMapJsonTypeProcessor {

	@Override
	public void processItemToJsonObject(final Item item, final JsonObject jo, final String jsonPropertyName, Context context) throws NotesException {
		processValuesToJsonObject(item.getValues(), jo, jsonPropertyName, context);
	}

	@Override
	public void processValuesToJsonObject(final List<?> values, final JsonObject jo, final String jsonPropertyName, Context context) throws NotesException {
		if (values != null && !values.isEmpty()) {
			List<Double> val = makeDoubleList(values);
			if (!val.isEmpty()) {
				jo.putJsonProperty(jsonPropertyName, val);
			}
		}
	}

	private List<Double> makeDoubleList(final List<?> values) {
		List<Double> doubleValues = new ArrayList<Double>();
		for (Object value : values) {
			Double val = TypeEnforcement.getAsDouble(value);
			if (null != val) {
				doubleValues.add(val);
			}
		}
		return doubleValues;
	}

	@Override
	public void processJsonValueToDocument(final JsonJavaObject jso, final Document doc, final MappingField mfField, Context context) throws NotesException {
		if (!jso.containsKey(mfField.getJsonName())) {
			return;
		}
		List<Double> lstValues = new ArrayList<Double>();
		JsonJavaArray array = jso.getAsArray(mfField.getJsonName());
		for (int nCounter = 0; nCounter < array.length(); nCounter++) {
			lstValues.add(array.getAsDouble(nCounter));
		}
		doc.replaceItemValue(mfField.getNotesFieldName(), new Vector<Double>(lstValues));
	}

	@Override
	public void processJsonValueToDocument(final Vector<?> values, final Document doc, final String fieldName, Context context) throws NotesException {
		super.processJsonValueToDocument(new Vector<Double>(makeDoubleList(values)), doc, fieldName, context);
	}

}
