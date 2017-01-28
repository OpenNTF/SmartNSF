package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
	public void processItemToJsonObject(Item item, JsonObject jo, String jsonPropertyName) throws NotesException {
		Vector<?> values = item.getValues();
		processValuesToJsonObject(values, jo, jsonPropertyName);
	}

	@Override
	public void processValuesToJsonObject(List<?> values, JsonObject jo, String jsonPropertyName) throws NotesException {
		if (values != null && !values.isEmpty()) {
			List<Double> stringValues = makeDoubleList(values);
			jo.putJsonProperty(jsonPropertyName, stringValues);
		}
	}

	private List<Double> makeDoubleList(List<?> values) {
		List<Double> doubleValues = new ArrayList<Double>();
		for (Object value : values) {
			doubleValues.add(TypeEnforcement.getAsDouble(value));
		}
		return doubleValues;
	}

	@Override
	public void processJsonValueToDocument(JsonJavaObject jso, Document doc, MappingField mfField) throws NotesException {
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
	public void processJsonValueToDocument(Vector<?> values, Document doc, String fieldName) throws NotesException {
		super.processJsonValueToDocument(new Vector<Double>(makeDoubleList(values)), doc, fieldName);
	}

}
