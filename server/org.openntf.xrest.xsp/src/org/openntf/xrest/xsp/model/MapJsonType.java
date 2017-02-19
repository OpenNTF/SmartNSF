package org.openntf.xrest.xsp.model;

import java.util.List;
import java.util.Vector;

import org.openntf.xrest.xsp.exec.convertor.MapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.DateOnlyMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.DateTimeArrayMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.DateTimeMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.DefaultMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.DoubleArrayMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.DoubleMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.IntegerArrayMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.IntegerMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.MimeMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.StringArrayMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.StringMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.TimeOnlyMapJsonTypeProcessor;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;

public enum MapJsonType {
	DEFAULT(new DefaultMapJsonTypeProcessor()), STRING(new StringMapJsonTypeProcessor()), INTEGER(new IntegerMapJsonTypeProcessor()), DOUBLE(new DoubleMapJsonTypeProcessor()), MIME(
			new MimeMapJsonTypeProcessor()), DATETIME(new DateTimeMapJsonTypeProcessor()), DATEONLY(new DateOnlyMapJsonTypeProcessor()), TIMEONLY(new TimeOnlyMapJsonTypeProcessor()), ARRAY_OF_STRING(
					new StringArrayMapJsonTypeProcessor()), ARRAY_OF_INTEGER(new IntegerArrayMapJsonTypeProcessor()), ARRAY_OF_DOUBLE(new DoubleArrayMapJsonTypeProcessor()), ARRY_OF_DATETIME(
							new DateTimeArrayMapJsonTypeProcessor());

	final MapJsonTypeProcessor processor;

	public void processJsonValueToDocument(JsonJavaObject jo, Document doc, MappingField mf) throws NotesException {
		processor.processJsonValueToDocument(jo, doc, mf);
	}

	public void processJsonValueToDocument(Vector<?> values, Document doc, String fieldName) throws NotesException {
		processor.processJsonValueToDocument(values, doc, fieldName);
	}

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
