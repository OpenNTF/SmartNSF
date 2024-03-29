package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.model.MappingField;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;

public class StringArrayMapJsonTypeProcessor extends AbstractMapJsonTypeProcessor {

	@Override
	public void processItemToJsonObject(final Item item, final JsonObject jo, final String jsonPropertyName, Context context) throws NotesException {
		processValuesToJsonObject(item.getValues(), jo, jsonPropertyName, context);
	}

	@Override
	public void processValuesToJsonObject(final List<?> values, final JsonObject jo, final String jsonPropertyName, Context context) throws NotesException {
		List<String> jsonValues = buildValues(values);
		jo.putJsonProperty(jsonPropertyName, jsonValues);
	}

	private List<String> buildValues(List<?> values) {
		if (values == null || values.isEmpty()) {
			return Collections.emptyList();
		}
		return makeStringList(values);
	}

	@SuppressWarnings("unchecked")
	private List<String> makeStringList(final List<?> values) {
		return (List<String>)values.stream().filter(val-> val instanceof String).filter(val -> val != null).collect(Collectors.toList());
	}

	@Override
	public void processJsonValueToDocument(final JsonJavaObject jso, final Document doc, final MappingField mfField, Context context) throws NotesException {
		if (!jso.containsKey(mfField.getJsonName())) {
			return;
		}
		List<String> lstValues = new ArrayList<String>();
		JsonJavaArray array = jso.getAsArray(mfField.getJsonName());
		for (int nCounter = 0; nCounter < array.length(); nCounter++) {
			lstValues.add(array.getAsString(nCounter));
		}
		doc.replaceItemValue(mfField.getNotesFieldName(), new Vector<String>(lstValues));
	}

	@Override
	public void processJsonValueToDocument(final Vector<?> values, final Document doc, final String fieldName, Context context) throws NotesException {
		super.processJsonValueToDocument(new Vector<String>(makeStringList(values)), doc, fieldName,context);
	}

}
