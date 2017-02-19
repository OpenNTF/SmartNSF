package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
	public void processItemToJsonObject(Item item, JsonObject jo, String jsonPropertyName) throws NotesException {
		List<?> values = item.getValues();
		processValuesToJsonObject(values, jo, jsonPropertyName);
	}

	@Override
	public void processValuesToJsonObject(List<?> values, JsonObject jo, String jsonPropertyName) throws NotesException {
		if (values != null && !values.isEmpty()) {
			List<String> stringValues = buildISODateList(values);
			jo.putJsonProperty(jsonPropertyName, stringValues);
		}
	}

	private List<String> buildISODateList(List<?> values) throws NotesException {
		List<String> isoDates = new ArrayList<String>();
		for (Object obj : values) {
			if (obj instanceof DateTime) {
				isoDates.add(buildISO8601Date(((DateTime) obj).toJavaDate()));
			}
		}
		return isoDates;
	}

	@Override
	public void processJsonValueToDocument(JsonJavaObject jso, Document doc, MappingField mfField) throws NotesException {
		if (!jso.containsKey(mfField.getJsonName())) {
			return;
		}
		Session session = doc.getParentDatabase().getParent();
		List<DateTime> lstValues = new Vector<DateTime>();
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
	public void processJsonValueToDocument(Vector<?> values, Document doc, String fieldName) throws NotesException {
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

}
