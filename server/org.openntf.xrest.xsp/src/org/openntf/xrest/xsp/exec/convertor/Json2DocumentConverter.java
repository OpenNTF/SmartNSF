package org.openntf.xrest.xsp.exec.convertor;

import java.util.Vector;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.model.MappingField;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Document;
import lotus.domino.NotesException;

public class Json2DocumentConverter {

	private final RouteProcessor routeProcessor;
	private final Document doc;
	private final JsonJavaObject jso;
	private final Context context;

	public Json2DocumentConverter(Document doc, RouteProcessor routeProcessor, JsonJavaObject jso, Context context) {
		this.jso = jso;
		this.routeProcessor = routeProcessor;
		this.doc = doc;
		this.context = context;
	}

	public boolean buildDocumentFromJson() throws NotesException {
		boolean update = false;
		for (MappingField mfField : routeProcessor.getMappingFields().values()) {
			if (jso.containsKey(mfField.getJsonName()) && !mfField.isReadOnly()) {
				mfField.getType().processJsonValueToDocument(jso, doc, mfField, this.context);
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
		field.getType().processJsonValueToDocument(result,doc,field.getNotesFieldName(), this.context);
		NotesObjectRecycler.recycleObjects(result);
	}

}
