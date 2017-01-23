package org.openntf.xrest.xsp.model;

import java.util.List;

import org.openntf.xrest.xsp.exec.convertor.DateTimeMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.DefaultMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.MimeMapJsonTypeProcessor;

import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Item;
import lotus.domino.NotesException;

public enum MapJsonType {
	DEFAULT(new DefaultMapJsonTypeProcessor()), STRING(new DefaultMapJsonTypeProcessor()), INTEGER(new DefaultMapJsonTypeProcessor()), DOUBLE(new DefaultMapJsonTypeProcessor()), MIME(
			new MimeMapJsonTypeProcessor()), DATETIME(new DateTimeMapJsonTypeProcessor()), DATEONLY(new DefaultMapJsonTypeProcessor()), TIMEONLY(new DefaultMapJsonTypeProcessor()), ARRAY_OF_STRING(
					new DefaultMapJsonTypeProcessor()), ARRAY_OF_INTEGER(new DefaultMapJsonTypeProcessor()), ARRAY_OF_DOUBLE(new DefaultMapJsonTypeProcessor()), ARRY_OF_DATETIME(
							new DefaultMapJsonTypeProcessor());

	final MapJsonTypeProcessor processor;

	public void processItemToJsonObject(Item item, JsonObject jo, String jsonProperty) throws NotesException {
		processor.processItemToJsonObject(item, jo, jsonProperty);
	}

	public void processValuesToJsonObject(List<?> values, JsonObject jo, String jsonProperty) throws NotesException {
		processor.processValuesToJsonObject(values, jo, jsonProperty);
	}

	private MapJsonType(MapJsonTypeProcessor processor) {
		this.processor = processor;
	}
	
	
}
