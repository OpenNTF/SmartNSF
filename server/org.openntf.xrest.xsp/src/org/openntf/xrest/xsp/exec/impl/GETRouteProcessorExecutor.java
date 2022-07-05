package org.openntf.xrest.xsp.exec.impl;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

public class GETRouteProcessorExecutor extends AbstractJsonRouteProcessorExecutor {

	public GETRouteProcessorExecutor(String path) {
		super(path);
	}

	@Override
	protected void executeMethodeSpecific(Context context, DataContainer<?> container, RouteProcessor routeProcessor) throws ExecutorException {
		try {
			setResultPayload(routeProcessor.getStrategyModel().buildResponse(context, routeProcessor, container), context, routeProcessor);
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, getPath(), "buildResult");
		}
	}

}
