package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.convertor.MapJsonTypeProcessor;
import org.openntf.xrest.xsp.model.MappingField;
import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.Session;

public class DateTimeArrayMapJsonTypeProcessor extends AbstractDateTimeToISODate implements MapJsonTypeProcessor {

	@Override
	public void processItemToJsonObject(final Item item, final JsonObject jo, final String jsonPropertyName, Context context) throws NotesException {
		Vector<?> items = item.getValues();
		processValuesToJsonObject(items, jo, jsonPropertyName, context);
		NotesObjectRecycler.recycleList(items);
	}

	@Override
	public void processValuesToJsonObject(final List<?> values, final JsonObject jo, final String jsonPropertyName, Context context) throws NotesException {
		if (values != null && !values.isEmpty()) {
			List<String> val = buildISODateList(values);
			if (!val.isEmpty()) {
				jo.putJsonProperty(jsonPropertyName, val);
			}
		}
	}

	private List<String> buildISODateList(final List<?> values) throws NotesException {
		List<String> isoDates = new ArrayList<String>();
		for (Object obj : values) {
			if (obj instanceof DateTime) {
				isoDates.add(buildISO8601Date(((DateTime) obj).toJavaDate()));
			}
		}
		return isoDates;
	}

	@Override
	public void processJsonValueToDocument(final JsonJavaObject jso, final Document doc, final MappingField mfField, Context context) throws NotesException {
		if (!jso.containsKey(mfField.getJsonName())) {
			return;
		}
		Session session = doc.getParentDatabase().getParent();
		List<DateTime> lstValues = new ArrayList<DateTime>();
		try {
			JsonJavaArray array = jso.getAsArray(mfField.getJsonName());
			for (int nCounter = 0; nCounter < array.length(); nCounter++) {
				String dateValue = array.getAsString(nCounter);
				lstValues.add(buildDateTime(dateValue, session));
			}
			doc.replaceItemValue(mfField.getNotesFieldName(), lstValues);
		} catch (ParseException e) {
			throw new NotesException(9999, "Error during ISO Date parsing", e);
		} finally {
			if (!lstValues.isEmpty()) {
				NotesObjectRecycler.recycle(lstValues.toArray(new DateTime[lstValues.size()]));
			}
		}
	}

	@Override
	public void processJsonValueToDocument(final Vector<?> values, final Document doc, final String fieldName, Context context) throws NotesException {
		if (values != null && values.isEmpty()) {
			Session session = doc.getParentDatabase().getParent();
			List<DateTime> lstValues = new Vector<DateTime>();
			try {
				for (Object obj : values) {
					lstValues.add(buildDateTime(obj, session));
				}
				doc.replaceItemValue(fieldName, lstValues);
			} catch (ParseException e) {
				throw new NotesException(9999, "Error during ISO Date parsing", e);
			} finally {
				if (!lstValues.isEmpty()) {
					NotesObjectRecycler.recycle(lstValues.toArray(new DateTime[lstValues.size()]));
				}
			}
		}
	}

	@Override
	public void processColumnValueToJsonObject(final Object clmnValue, final JsonObject jo, final String jsonPropertyName, Context context)
			throws NotesException {
		if (clmnValue instanceof List) {
			processValuesToJsonObject((List<?>) clmnValue, jo, jsonPropertyName, context);
		} else {
			processValuesToJsonObject(Arrays.asList(clmnValue), jo, jsonPropertyName, context);
		}
	}

}
