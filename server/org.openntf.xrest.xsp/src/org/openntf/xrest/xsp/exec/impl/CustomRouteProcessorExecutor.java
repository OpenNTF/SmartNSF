package org.openntf.xrest.xsp.exec.impl;

import java.io.IOException;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.CustomRestHandler;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.RouteProcessorExecutor;
import org.openntf.xrest.xsp.exec.output.ExecutorExceptionProcessor;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.model.strategy.Custom;

import com.ibm.commons.util.io.json.JsonException;

public class CustomRouteProcessorExecutor implements RouteProcessorExecutor {

	private final Context context;
	private final RouteProcessor rp;
	private final String path;

	public CustomRouteProcessorExecutor(Context context, RouteProcessor rp, String path) {
		this.context = context;
		this.rp = rp;
		this.path = path;
	}

	@Override
	public void execute() {
		try {
			Custom model = (Custom) rp.getStrategyModel();
			CustomRestHandler handler = model.getCustomRestHandler();
			handler.processCall(context, path);
		} catch (ExecutorException ex) {
			try {
				ExecutorExceptionProcessor.INSTANCE.processExecutorException(ex, context.getResponse(), context.traceEnabled());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (JsonException ex) {
			try {
				ExecutorExceptionProcessor.INSTANCE.processGeneralException(500, ex, context.getResponse());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException ex) {
			try {
				ExecutorExceptionProcessor.INSTANCE.processGeneralException(500, ex, context.getResponse());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			try {
				ExecutorExceptionProcessor.INSTANCE.processGeneralException(500, ex, context.getResponse());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
