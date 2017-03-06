package org.openntf.xrest.xsp.exec;

import org.openntf.xrest.xsp.exec.impl.DELETERouteProcessorExecutor;
import org.openntf.xrest.xsp.exec.impl.GETAttachmentRouteProcessorExecutor;
import org.openntf.xrest.xsp.exec.impl.GETRouteProcessorExecutor;
import org.openntf.xrest.xsp.exec.impl.POSTRouteProcessorExecutor;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.model.Strategy;

public class RouteProcessorExecutorFactory {

	public RouteProcessorExecutorFactory() {
	}

	public static RouteProcessorExecutor getExecutor(String method, String path, Context context, RouteProcessor rp) {
		if ("GET".equals(method)) {
			if (rp.getStrategyValue() == Strategy.SELECT_ATTACHMENT) {
				return new GETAttachmentRouteProcessorExecutor(context, rp, path);
			}
			return new GETRouteProcessorExecutor(context, rp, path);
		}
		if ("POST".equalsIgnoreCase(method)) {
			return new POSTRouteProcessorExecutor(context, rp, path);
		}
		if ("PUT".equalsIgnoreCase(method)) {
			return new POSTRouteProcessorExecutor(context, rp, path);
		}
		if ("DELETE".equalsIgnoreCase(method)) {
			return new DELETERouteProcessorExecutor(context, rp, path);
		}
		return null;
	}

}
