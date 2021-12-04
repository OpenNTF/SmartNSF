package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.text.ParseException;
import java.util.Date;

import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.Session;

public class DateTimeNSFHelper extends AbstractDateTimeToISODate {

	public String buildJsonDateStringFromDocument(Document doc, String fieldName) throws NotesException {
		String result = null;
		if (!doc.hasItem(fieldName)) {
			return result;
		}
		Item item = doc.getFirstItem(fieldName);
		DateTime dtCurrent = item.getDateTimeValue();
		if (dtCurrent != null) {
			Date javaDate = dtCurrent.toJavaDate();
			result = buildISO8601DateOnly(javaDate);
			dtCurrent.recycle();
		}
		item.recycle();
		return result;
		
	}

	public String buildJsonDateTimeStringFromDocument(Document doc, String fieldName)  throws NotesException {
		String result = null;
		if (!doc.hasItem(fieldName)) {
			return result;
		}
		Item item = doc.getFirstItem(fieldName);
		DateTime dtCurrent = item.getDateTimeValue();
		if (dtCurrent != null) {
			Date javaDate = dtCurrent.toJavaDate();
			result = buildISO8601Date(javaDate);
			dtCurrent.recycle();
		}
		item.recycle();
		return result;
	}

	public DateTime buildDateTimeFromJsonDateString(String jsonDateTimeString, Session session) throws ParseException, NotesException {
		Date date = parse(jsonDateTimeString);
		return session.createDateTime(date);
	}
}
