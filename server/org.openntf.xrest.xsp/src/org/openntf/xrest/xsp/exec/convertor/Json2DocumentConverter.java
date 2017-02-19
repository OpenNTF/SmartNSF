package org.openntf.xrest.xsp.exec.convertor;

import java.util.Vector;

import org.openntf.xrest.xsp.model.MappingField;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Document;
import lotus.domino.NotesException;

public class Json2DocumentConverter {

	private final RouteProcessor routeProcessor;
	private final Document doc;
	private final JsonJavaObject jso;

	public Json2DocumentConverter(Document doc, RouteProcessor routeProcessor, JsonJavaObject jso) {
		this.jso = jso;
		this.routeProcessor = routeProcessor;
		this.doc = doc;
	}

	public boolean buildDocumentFromJson() throws NotesException {
		boolean update = false;
		for (MappingField mfField : routeProcessor.getMappingFields().values()) {
			if (jso.containsKey(mfField.getJsonName()) && !mfField.isReadOnly()) {
				mfField.getType().processJsonValueToDocument(jso, doc, mfField);
				update = true;
			}
		}
		for (MappingField field : routeProcessor.getFormulaFields()) {
			if (!field.isReadOnly()) {
				processFormulaToDocument(jso, field, doc);
				update = true;
			}
		}
		return update;
	}

	private void processFormulaToDocument(JsonObject jo, MappingField field, Document doc) throws NotesException {
		Vector<?> result = doc.getParentDatabase().getParent().evaluate(field.getFormula(), doc);
		field.getType().processJsonValueToDocument(result,doc,field.getNotesFieldName());
	}

}
