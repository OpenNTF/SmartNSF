package org.openntf.xrest.xsp.exec;

import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonArray;
import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

public class GETRouteProcessorExecutor extends AbstractRouteProcessorExecutor {

	public GETRouteProcessorExecutor(Context context, RouteProcessor routerProcessor, String path) {
		super(context, routerProcessor, path);
	}

	@Override
	protected void executeMethodeSpecific() {
		try {
			JsonArray array = new JsonJavaArray();
			JsonObject json1 = new JsonJavaObject();
			JsonObject json2 = new JsonJavaObject();
			json1.putJsonProperty("title", "great project");
			json1.putJsonProperty("type", "XSP");
			json2.putJsonProperty("title", "2. great project");
			json2.putJsonProperty("type", "OSGI");
			array.add(json1);
			array.add(json2);
			setResultPayload(array);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
