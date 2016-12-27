package org.openntf.xrest.xsp.exec;

import org.openntf.xrest.xsp.model.RouteProcessor;

public class RouteProcessorExecutorFactory {

	public RouteProcessorExecutorFactory() {
	}

	public static RouteProcessorExecutor getExecutor(String method, String path, Context context, RouteProcessor rp) {
		if ("GET".equals(method)) {
			return new GETRouteProcessorExecutor(context, rp, path);
		}
		return null;
	}

}
