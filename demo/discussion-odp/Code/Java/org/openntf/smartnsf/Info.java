package org.openntf.smartnsf;

import lotus.domino.Database;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.CustomRestHandler;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.util.JsonWriter;
import com.ibm.domino.services.HttpServiceConstants;

public class Info implements CustomRestHandler {

	public void processCall(Context context, String path) throws Exception {	
		JsonJavaObject result = new JsonJavaObject(); 
		Database db = context.getDatabase();
		result.put("db_title", db.getTitle());
		result.put("db_filepath", db.getFilePath());
		result.put("db_server", db.getServer());
		result.put("db_fulltextindex", db.isFTIndexed());
		result.put("db_documentcount", db.getAllDocuments().getCount());
		result.put("groups", context.getGroups());
		result.put("roles", context.getRoles());
		context.getResponse().setContentType(HttpServiceConstants.CONTENTTYPE_APPLICATION_JSON_UTF8);
		JsonWriter jsw = new JsonWriter(context.getResponse().getWriter(),true);
		jsw.outObject(result);
		jsw.close();
	}
}
