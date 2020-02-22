package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.convertor.MapJsonTypeProcessor;
import org.openntf.xrest.xsp.model.MappingField;
import org.openntf.xrest.xsp.names.IdentityMapProvider;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;

public class IdentityMapArrayJsonProcessor implements MapJsonTypeProcessor {

	@Override
	public void processItemToJsonObject(Item item, JsonObject jo, String jsonPropertyName, Context context) throws NotesException {
		processValuesToJsonObject(item.getValues(), jo, jsonPropertyName, context);

	}

	@Override
	public void processValuesToJsonObject(List<?> values, JsonObject jo, String jsonPropertyName, Context context) throws NotesException {
		IdentityMapProvider imp = checkAndGetIdentityProvider(context);
		if (values != null && !values.isEmpty()) {
			jo.putJsonProperty(jsonPropertyName, buildIdentityList(values, imp, context));
		}

	}

	private List<String> buildIdentityList(List<?> values, IdentityMapProvider imp, Context context) {
		List<String> rc = new ArrayList<String>();
		for (Object value : values) {
			String identity = imp.getIdentityForNotesName("" + value, context);
			rc.add(identity);
		}
		return rc;
	}

	@Override
	public void processColumnValueToJsonObject(Object clmnValue, JsonObject jo, String jsonPropertyName, Context context) throws NotesException {
		if (clmnValue instanceof List) {
			processValuesToJsonObject((List<?>) clmnValue, jo, jsonPropertyName, context);
		} else {
			processValuesToJsonObject(Arrays.asList("" + clmnValue), jo, jsonPropertyName, context);
		}
	}

	@Override
	public void processJsonValueToDocument(JsonJavaObject jo, Document doc, MappingField mf, Context context) throws NotesException {
		IdentityMapProvider imp = checkAndGetIdentityProvider(context);
		if (!jo.containsKey(mf.getJsonName())) {
			return;
		}
		List<String> lstValues = new ArrayList<String>();
		JsonJavaArray array = jo.getAsArray(mf.getJsonName());
		for (int nCounter = 0; nCounter < array.length(); nCounter++) {
			lstValues.add(array.getAsString(nCounter));
		}
		doc.replaceItemValue(mf.getNotesFieldName(), new Vector<String>(buildIdentityList(lstValues, imp, context)));
	}

	@Override
	public void processJsonValueToDocument(Vector<?> values, Document doc, String fieldName, Context context) throws NotesException {
		IdentityMapProvider imp = checkAndGetIdentityProvider(context);
		if (values != null && !values.isEmpty()) {
			doc.replaceItemValue(fieldName, buildIdentityList(values, imp, context));
		}
	}

	private IdentityMapProvider checkAndGetIdentityProvider(Context context) {
		if (context.getIdentityMapProvider() == null) {
			throw new RuntimeException("No Identity Provider is registred!");
		}
		return context.getIdentityMapProvider();
	}

}
