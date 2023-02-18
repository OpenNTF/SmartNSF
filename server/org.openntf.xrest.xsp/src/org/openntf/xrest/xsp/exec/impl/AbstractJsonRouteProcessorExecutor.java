package org.openntf.xrest.xsp.exec.impl;

import java.io.IOException;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.Document2JsonConverter;
import org.openntf.xrest.xsp.exec.output.JsonPayloadProcessor;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.EventException;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonObject;

import groovy.lang.Closure;
import lotus.domino.Document;
import lotus.domino.NotesException;

public abstract class AbstractJsonRouteProcessorExecutor extends AbstractRouteProcessorExecutor {

	public AbstractJsonRouteProcessorExecutor(final String path) {
		super( path);
	}

	@Override
	protected void preSubmitValues(Context context, RouteProcessor routeProcessor, DataContainer<?> dataContainer) throws ExecutorException {
		try {
			Closure<?> cl = routeProcessor.getEventClosure(EventType.PRE_SUBMIT);
			if (cl != null) {
				if (dataContainer.isList()) {
					DSLBuilder.callClosure(cl, context, null);					
				} else {
					DSLBuilder.callClosure(cl, context, dataContainer.getData());
				}
			}
		} catch (EventException e) {
			throw new ExecutorException(e, path, "presubmit");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runtime Error: " + e.getMessage(), e, path, "presubmit");
		}

	}

	@Override
	protected void submitValues(Context context, RouteProcessor routeProcessor,DataContainer<?> dataContainer) throws IOException, JsonException {
		JsonPayloadProcessor.INSTANCE.processJsonPayload(context.getResultPayload(), context.getResponse());
	}

	public void setResultPayload(final Object rp, Context context, RouteProcessor routeProcessor) {
		context.setResultPayload(rp);
	}

	protected JsonObject buildJsonFromDocument(final Document doc, Context context, RouteProcessor routeProcessor) throws NotesException {
		Document2JsonConverter d2jc = new Document2JsonConverter(routeProcessor, context);
		return d2jc.buildJsonFromDocument(doc);
	}
}