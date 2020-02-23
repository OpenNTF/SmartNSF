package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.util.List;
import java.util.Vector;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.convertor.MapJsonTypeProcessor;
import org.openntf.xrest.xsp.model.MappingField;
import org.openntf.xrest.xsp.names.IdentityMapProvider;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;

public class IdentityMapJsonProcessor implements MapJsonTypeProcessor {

	@Override
	public void processItemToJsonObject(Item item, JsonObject jo, String jsonPropertyName, Context context) throws NotesException {
		IdentityMapProvider imp = checkAndGetIdentityProvider(context);
		String value = item.getValueString();
		String identity = imp.getIdentityForNotesName(value, context);
		jo.putJsonProperty(jsonPropertyName, identity);

	}

	@Override
	public void processValuesToJsonObject(List<?> values, JsonObject jo, String jsonPropertyName, Context context) throws NotesException {
		IdentityMapProvider imp = checkAndGetIdentityProvider(context);
		if (values != null && !values.isEmpty()) {
			Object value = values.get(0);
			String identity = imp.getIdentityForNotesName("" + value, context);
			jo.putJsonProperty(jsonPropertyName, identity);
		}

	}

	@Override
	public void processColumnValueToJsonObject(Object clmnValue, JsonObject jo, String jsonPropertyName, Context context) throws NotesException {
		IdentityMapProvider imp = checkAndGetIdentityProvider(context);
		if (clmnValue instanceof String && !((String) clmnValue).isEmpty()) {
			String identity = imp.getIdentityForNotesName("" + clmnValue, context);
			jo.putJsonProperty(jsonPropertyName, identity);
		}

	}

	@Override
	public void processJsonValueToDocument(JsonJavaObject jo, Document doc, MappingField mf, Context context) throws NotesException {
		IdentityMapProvider imp = checkAndGetIdentityProvider(context);
		String value = "" + jo.get(mf.getJsonName());
		String notesName = imp.getNotesNameForIdentity(value, context);
		doc.replaceItemValue(mf.getNotesFieldName(), notesName);

	}

	@Override
	public void processJsonValueToDocument(Vector<?> values, Document doc, String fieldName, Context context) throws NotesException {
		IdentityMapProvider imp = checkAndGetIdentityProvider(context);
		if (values != null && !values.isEmpty()) {
			String value = "" + values.get(0);
			String notesName = imp.getNotesNameForIdentity(value, context);
			doc.replaceItemValue(fieldName, notesName);
		}
	}

	private IdentityMapProvider checkAndGetIdentityProvider(Context context) {
		if (context.getIdentityMapProvider() == null) {
			throw new RuntimeException("No Identity Provider is registred!");
		}
		return context.getIdentityMapProvider();
	}

}
