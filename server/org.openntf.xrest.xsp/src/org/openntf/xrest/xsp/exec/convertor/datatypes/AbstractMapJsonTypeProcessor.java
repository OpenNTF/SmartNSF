package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.util.Vector;

import org.openntf.xrest.xsp.exec.convertor.MapJsonTypeProcessor;
import org.openntf.xrest.xsp.model.MappingField;

import com.ibm.commons.util.io.json.JsonJavaObject;

import lotus.domino.Document;
import lotus.domino.NotesException;

public abstract class AbstractMapJsonTypeProcessor implements MapJsonTypeProcessor {

	@Override
	public void processJsonValueToDocument(JsonJavaObject jso, Document doc, MappingField mfField) throws NotesException {
		doc.replaceItemValue(mfField.getNotesFieldName(), jso.get(mfField.getJsonName()));

	}

	@Override
	public void processJsonValueToDocument(Vector<?> values, Document doc, String fieldName) throws NotesException {
		doc.replaceItemValue(fieldName, values);

	}

}
