package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.util.List;

import org.openntf.xrest.xsp.exec.Context;

import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Item;
import lotus.domino.NotesException;

public class IdentityMapJsonProcessor extends AbstractMapJsonTypeProcessor {

	@Override
	public void processItemToJsonObject(Item item, JsonObject jo, String jsonPropertyName, Context context) throws NotesException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processValuesToJsonObject(List<?> values, JsonObject jo, String jsonPropertyName, Context context) throws NotesException {
		// TODO Auto-generated method stub
		
	}


}
