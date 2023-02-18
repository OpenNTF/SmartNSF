package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import lotus.domino.DateTime;
import lotus.domino.NotesException;
import lotus.domino.Session;

public class AbstractDateTimeToISODate {

	public AbstractDateTimeToISODate() {
		super();
	}

	protected String buildISO8601Date(final Date javaDate) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

		TimeZone tz = TimeZone.getTimeZone("UTC");

		df.setTimeZone(tz);

		String output = df.format(javaDate);
		String result = output.replaceAll("UTC", "+00:00");
		return result;
	}

	protected Date parse(final String input) throws java.text.ParseException {

		String toParse = input;
		
		if (toParse.length() == 19) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			return df.parse(toParse);
			
		}
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
		// System.out.println("DEBUG: dateToParse=" + toParse);
		return df.parse(toParse);

	}

	protected DateTime buildDateTime(final Object value, final Session session) throws NotesException, ParseException {
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
		throw new NotesException(9999, value + " is not DateTime / Date / or String");
	}

	public String buildISO8601DateOnly(final Date javaDate) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(javaDate);
	}

}