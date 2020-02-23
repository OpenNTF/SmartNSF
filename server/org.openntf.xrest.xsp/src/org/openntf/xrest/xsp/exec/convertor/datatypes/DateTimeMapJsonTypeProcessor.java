package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.openntf.xrest.xsp.exec.Context;
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
	public void processItemToJsonObject(final Item item, final JsonObject jo, final String jsonPropertyName, Context context) throws NotesException {
		DateTime dtCurrent = item.getDateTimeValue();
		if (dtCurrent != null) {
			Date javaDate = dtCurrent.toJavaDate();
			jo.putJsonProperty(jsonPropertyName, buildISO8601Date(javaDate));
			dtCurrent.recycle();
		}
	}

	@Override
	public void processValuesToJsonObject(final List<?> values, final JsonObject jo, final String jsonPropertyName, Context context) throws NotesException {
		if (values != null && !values.isEmpty()) {
			Object value = values.get(0);
			if (value instanceof DateTime) {
				Date dtCurrent = ((DateTime) value).toJavaDate();
				jo.putJsonProperty(jsonPropertyName, buildISO8601Date(dtCurrent));
			}
		}
	}

	@Override
	public void processJsonValueToDocument(final JsonJavaObject jo, final Document doc, final MappingField mf, Context context) throws NotesException {
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
	public void processJsonValueToDocument(final Vector<?> values, final Document doc, final String fieldName, Context context) throws NotesException {
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

	@Override
	public void processColumnValueToJsonObject(final Object clmnValue, final JsonObject jo, final String jsonPropertyName, Context context)
			throws NotesException {
		if (clmnValue instanceof DateTime) {
			DateTime dtCurrent = (DateTime) clmnValue;
			Date javaDate = dtCurrent.toJavaDate();
			jo.putJsonProperty(jsonPropertyName, buildISO8601Date(javaDate));
		}
	}
}
