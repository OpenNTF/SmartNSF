package org.openntf.xrest.xsp.model;

import java.util.List;

import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Item;
import lotus.domino.NotesException;

public interface MapJsonTypeProcessor {

	public void processItemToJsonObject(Item item, JsonObject jo, String jsonProperty) throws NotesException;

	public void processValuesToJsonObject(List<?> values, JsonObject jo, String jsonProperty) throws NotesException;
}
