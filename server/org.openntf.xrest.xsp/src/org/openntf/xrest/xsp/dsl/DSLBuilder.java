package org.openntf.xrest.xsp.dsl;

import java.util.HashMap;
import java.util.Map;

import org.openntf.xrest.xsp.model.Router;

public class DSLBuilder {

	public static Router buildRouterFromDSL(String dsl) {
		Router router = new Router();
		//BasicGlobalSettings settings = new BasicGlobalSettings();
		Map<String, Object> bindings = new HashMap<String, Object>();
		//bindings.put(BINDING_VAR_SETTINGS, settings);
		bindings.put("router", router);
		return router;
	}
}
