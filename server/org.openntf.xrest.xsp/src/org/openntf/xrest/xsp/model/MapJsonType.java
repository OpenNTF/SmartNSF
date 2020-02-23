package org.openntf.xrest.xsp.model;

import java.util.List;
import java.util.Vector;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.convertor.MapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.DateOnlyMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.DateTimeArrayMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.DateTimeMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.DefaultMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.DoubleArrayMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.DoubleMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.IdentityMapArrayJsonProcessor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.IdentityMapJsonProcessor;
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
	DEFAULT(new DefaultMapJsonTypeProcessor(), "string", ""),
	STRING(new StringMapJsonTypeProcessor(), "string", ""),
	INTEGER(new IntegerMapJsonTypeProcessor(), "integer", "int32"),
	DOUBLE(new DoubleMapJsonTypeProcessor(), "number", "double"),
	MIME(new MimeMapJsonTypeProcessor(),"string",""),
	DATETIME(new DateTimeMapJsonTypeProcessor(), "string", "dateTime"),
	DATEONLY(new DateOnlyMapJsonTypeProcessor(), "string", "date"),
	TIMEONLY(new TimeOnlyMapJsonTypeProcessor(),"string", ""),
	ARRAY_OF_STRING(new StringArrayMapJsonTypeProcessor(), "string", ""),
	ARRAY_OF_INTEGER(new IntegerArrayMapJsonTypeProcessor(), "integer", "int32"),
	ARRAY_OF_DOUBLE(new DoubleArrayMapJsonTypeProcessor(), "number", "double"),
	ARRAY_OF_DATETIME(new DateTimeArrayMapJsonTypeProcessor(), "string", "dateTime"),
	IDENTITY(new IdentityMapJsonProcessor(),"string",""),
	ARRAY_OF_IDENTITY(new IdentityMapArrayJsonProcessor(), "string", "");
	
	final transient MapJsonTypeProcessor processor;
	final transient String yamlType;
	final transient String yamlFormat;

	private MapJsonType(final MapJsonTypeProcessor processor, String yamlType, String yamlFormat) {
		this.processor = processor;
		this.yamlType = yamlType;
		this.yamlFormat = yamlFormat;
	}

	
	public void processJsonValueToDocument(final JsonJavaObject jo, final Document doc, final MappingField mf, Context context) throws NotesException {
		processor.processJsonValueToDocument(jo, doc, mf, context);
	}

	public void processJsonValueToDocument(final Vector<?> values, final Document doc, final String fieldName, Context context) throws NotesException {
		processor.processJsonValueToDocument(values, doc, fieldName, context);
	}

	public void processItemToJsonObject(final Item item, final JsonObject jo, final String jsonProperty, Context context) throws NotesException {
		processor.processItemToJsonObject(item, jo, jsonProperty, context);
	}

	public void processColumnValueToJsonObject(final Object clmnValue, final JsonObject jo, final String jsonProperty, Context context)
			throws NotesException {
		processor.processColumnValueToJsonObject(clmnValue, jo, jsonProperty, context);
	}

	public void processValuesToJsonObject(final List<?> values, final JsonObject jo, final String jsonProperty, Context context) throws NotesException {
		processor.processValuesToJsonObject(values, jo, jsonProperty, context);
	}


	public String yamlType() {
		return yamlType;
	}
	public String yamlFormat() {
		return yamlFormat;
	}

}
