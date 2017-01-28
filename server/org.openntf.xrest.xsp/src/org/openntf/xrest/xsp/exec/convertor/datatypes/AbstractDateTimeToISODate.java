package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.openntf.xrest.xsp.exec.convertor.MapJsonTypeProcessor;

import lotus.domino.DateTime;
import lotus.domino.NotesException;
import lotus.domino.Session;

public class AbstractDateTimeToISODate {

	public AbstractDateTimeToISODate() {
		super();
	}

	protected String buildISO8601Date(Date javaDate) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
	
		TimeZone tz = TimeZone.getTimeZone("UTC");
	
		df.setTimeZone(tz);
	
		String output = df.format(javaDate);
	
		int inset0 = 9;
		int inset1 = 6;
	
		String s0 = output.substring(0, output.length() - inset0);
		String s1 = output.substring(output.length() - inset1, output.length());
		String result = s0 + s1;
		result = result.replaceAll("UTC", "+00:00");
		return result;
	}

	protected Date parse(String input) throws java.text.ParseException {
	
		String toParse = input;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
	
		// this is zero time so we need to add that TZ indicator for
		if (toParse.endsWith("Z")) {
			toParse = toParse.substring(0, input.length() - 1) + "GMT-00:00";
		} else {
			int inset = 6;
	
			String s0 = toParse.substring(0, toParse.length() - inset);
			String s1 = toParse.substring(toParse.length() - inset, toParse.length());
	
			toParse = s0 + "GMT" + s1;
		}
	
		return df.parse(toParse);
	
	}

	protected DateTime buildDateTime(Object value, Session session) throws NotesException, ParseException {
		if (value instanceof DateTime) {
			return (DateTime) value;
		}
		if (value instanceof Date) {
			return session.createDateTime((Date) value);
		}
		if (value instanceof String) {
			Date date = parse((String) value);
			return session.createDateTime(date);
		}
		throw new NotesException(9999, value +" is not DateTime / Date / or String");
	}

}