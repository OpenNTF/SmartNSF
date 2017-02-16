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

public class IntegerArrayMapJsonTypeProcessor extends AbstractMapJsonTypeProcessor {

	@Override
	public void processItemToJsonObject(Item item, JsonObject jo, String jsonPropertyName) throws NotesException {
		Vector<?> values = item.getValues();
		processValuesToJsonObject(values, jo, jsonPropertyName);
	}

	@Override
	public void processValuesToJsonObject(List<?> values, JsonObject jo, String jsonPropertyName) throws NotesException {
		if (values != null && !values.isEmpty()) {
			List<Integer> stringValues = makeIntegerList(values);
			jo.putJsonProperty(jsonPropertyName, stringValues);
		}
	}

	private List<Integer> makeIntegerList(List<?> values) {
		List<Integer> convertedValues = new ArrayList<Integer>();
		for (Object value : values) {
			convertedValues.add(TypeEnforcement.getAsInteger(value));
		}
		return convertedValues;
	}

	@Override
	public void processJsonValueToDocument(JsonJavaObject jso, Document doc, MappingField mfField) throws NotesException {
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
	public void processJsonValueToDocument(Vector<?> values, Document doc, String fieldName) throws NotesException {
		super.processJsonValueToDocument(new Vector<Integer>(makeIntegerList(values)), doc, fieldName);
	}

}
