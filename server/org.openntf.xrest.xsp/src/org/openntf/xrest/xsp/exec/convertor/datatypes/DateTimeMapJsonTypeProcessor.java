package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.openntf.xrest.xsp.exec.convertor.MapJsonTypeProcessor;
import org.openntf.xrest.xsp.model.MappingField;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;

public class DateTimeMapJsonTypeProcessor extends AbstractDateTimeToISODate implements MapJsonTypeProcessor {

	@Override
	public void processItemToJsonObject(Item item, JsonObject jo, String jsonPropertyName) throws NotesException {
		DateTime dtCurrent = item.getDateTimeValue();
		Date javaDate = dtCurrent.toJavaDate();
		jo.putJsonProperty(jsonPropertyName, buildISO8601Date(javaDate));
	}

	@Override
	public void processValuesToJsonObject(List<?> values, JsonObject jo, String jsonPropertyName) throws NotesException {
		if (values != null && !values.isEmpty()) {
			Object value = values.get(0);
			if (value instanceof DateTime) {
				Date dtCurrent = ((DateTime) value).toJavaDate();
				jo.putJsonProperty(jsonPropertyName, buildISO8601Date(dtCurrent));
			}
		}
	}

	@Override
	public void processJsonValueToDocument(JsonJavaObject jo, Document doc, MappingField mf) throws NotesException {
		if (jo.containsKey(mf.getJsonName())) {
			DateTime dtValue = null;
			try {
				Date date = parse(jo.getAsString(mf.getJsonName()));
				dtValue = doc.getParentDatabase().getParent().createDateTime(date);
				doc.replaceItemValue(mf.getNotesFieldName(), dtValue);
			} catch (Exception ex) {
				throw new NotesException(9999, "Error during ISO Date parsing", ex);
			} finally {
				if (dtValue != null) {
					dtValue.recycle();
				}
			}
		}

	}

	@Override
	public void processJsonValueToDocument(Vector<?> values, Document doc, String fieldName) throws NotesException {
		if (values != null && values.isEmpty()) {
			Object obj = values.get(0);
			DateTime dateTimeValue;
			try {
				dateTimeValue = buildDateTime(obj, doc.getParentDatabase().getParent());
				if (dateTimeValue != null) {
					doc.replaceItemValue(fieldName, dateTimeValue);
					dateTimeValue.recycle();
				}
			} catch (ParseException e) {
				throw new NotesException(9999, "Error during ISO Date parsing", e);
			}
		}
	}
}
