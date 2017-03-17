package org.openntf.xrest.xsp.exec.convertor;

import java.util.List;
import java.util.Vector;

import org.openntf.xrest.xsp.model.MappingField;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;

public interface MapJsonTypeProcessor {

	public void processItemToJsonObject(Item item, JsonObject jo, String jsonPropertyName) throws NotesException;

	public void processColumnValueToJsonObject(Object clmnValue, JsonObject jo, String jsonPropertyName) throws NotesException;

	public void processValuesToJsonObject(List<?> values, JsonObject jo, String jsonPropertyName) throws NotesException;

	public void processJsonValueToDocument(JsonJavaObject jo, Document doc, MappingField mf) throws NotesException;

	public void processJsonValueToDocument(Vector<?> values, Document doc, String fieldName) throws NotesException;

}
