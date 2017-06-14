package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.util.Arrays;
import java.util.List;

import org.openntf.xrest.xsp.exec.convertor.MapJsonTypeProcessor;
import org.openntf.xrest.xsp.model.MappingField;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Document;
import lotus.domino.NotesException;

public abstract class AbstractMapJsonTypeProcessor implements MapJsonTypeProcessor {

	@Override
	public void processJsonValueToDocument(final JsonJavaObject jso, final Document doc, final MappingField mfField) throws NotesException {
		doc.replaceItemValue(mfField.getNotesFieldName(), jso.get(mfField.getJsonName()));

	}

	@Override
	public void processJsonValueToDocument(final List<?> values, final Document doc, final String fieldName) throws NotesException {
		doc.replaceItemValue(fieldName, values);

	}

	@Override
	public void processColumnValueToJsonObject(final Object clmnValue, final JsonObject jo, final String jsonPropertyName)
			throws NotesException {
		if (clmnValue instanceof List) {
			processValuesToJsonObject((List<?>) clmnValue, jo, jsonPropertyName);
		} else {
			processValuesToJsonObject(Arrays.asList(clmnValue), jo, jsonPropertyName);
		}
	}

}
