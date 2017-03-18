package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openntf.xrest.xsp.model.MappingField;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;

public class StringArrayMapJsonTypeProcessor extends AbstractMapJsonTypeProcessor {

	@Override
	public void processItemToJsonObject(final Item item, final JsonObject jo, final String jsonPropertyName) throws NotesException {
		Vector<?> values = item.getValues();
		processValuesToJsonObject(values, jo, jsonPropertyName);
	}

	@Override
	public void processValuesToJsonObject(final List<?> values, final JsonObject jo, final String jsonPropertyName) throws NotesException {
		if (values != null && !values.isEmpty()) {
			List<String> stringValues = makeStringList(values);
			jo.putJsonProperty(jsonPropertyName, stringValues);
		}
	}

	private List<String> makeStringList(final List<?> values) {
		List<String> stringValues = new ArrayList<String>();
		for (Object value : values) {
			stringValues.add("" + value);
		}
		return stringValues;
	}

	@Override
	public void processJsonValueToDocument(final JsonJavaObject jso, final Document doc, final MappingField mfField) throws NotesException {
		if (!jso.containsKey(mfField.getJsonName())) {
			return;
		}
		List<String> lstValues = new ArrayList<String>();
		JsonJavaArray array = jso.getAsArray(mfField.getJsonName());
		System.out.println("The Array: " + array);
		for (int nCounter = 0; nCounter < array.length(); nCounter++) {
			System.out.println("Parsing: " + array.getAsString(nCounter));
			lstValues.add(array.getAsString(nCounter));
		}
		doc.replaceItemValue(mfField.getNotesFieldName(), new Vector<String>(lstValues));
	}

	@Override
	public void processJsonValueToDocument(final Vector<?> values, final Document doc, final String fieldName) throws NotesException {
		super.processJsonValueToDocument(new Vector<String>(makeStringList(values)), doc, fieldName);
	}

	@Override
	public void processColumnValueToJsonObject(final Object clmnValue, final JsonObject jo, final String jsonPropertyName)
			throws NotesException {
		@SuppressWarnings("unchecked")
		Vector<?> values = (Vector<String>) clmnValue;
		processValuesToJsonObject(values, jo, jsonPropertyName);

	}

}
