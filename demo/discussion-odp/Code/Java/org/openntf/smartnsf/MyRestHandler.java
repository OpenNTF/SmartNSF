package org.openntf.smartnsf;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.CustomRestHandler;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.util.JsonWriter;

public class MyRestHandler implements CustomRestHandler {

	public void processCall(Context context, String path) throws Exception {
		
		System.out.println("Was Called");
		JsonJavaObject result = new JsonJavaObject();
		result.put("test", "hello");
		JsonWriter jsw = new JsonWriter(context.getResponse().getWriter(),true);
		jsw.outObject(result);
		jsw.close();
	}

}
