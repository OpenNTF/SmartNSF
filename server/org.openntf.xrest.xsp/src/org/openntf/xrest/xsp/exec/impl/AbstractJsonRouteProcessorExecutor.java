package org.openntf.xrest.xsp.exec.impl;

import java.io.IOException;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.Document2JsonConverter;
import org.openntf.xrest.xsp.exec.output.JsonPayloadProcessor;
import org.openntf.xrest.xsp.model.EventException;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonObject;

import groovy.lang.Closure;
import lotus.domino.Document;
import lotus.domino.NotesException;

public abstract class AbstractJsonRouteProcessorExecutor extends AbstractRouteProcessorExecutor  {

	private Object resultPayload;

	public AbstractJsonRouteProcessorExecutor(Context context, RouteProcessor routerProcessor, String path) {
		super(context,routerProcessor,path);
	}

	@Override
	protected void preSubmitValues() throws ExecutorException {
		try {
			Closure<?> cl = routeProcessor.getEventClosure(EventType.PRE_SUBMIT);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context, dataContainer.getData());
			}
		} catch (EventException e) {
			throw new ExecutorException(400, "Post Load Error: " + e.getMessage(), e, path, "presubmit");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, path, "presubmit");
		}

	}

	@Override
	protected void submitValues() throws IOException, JsonException {
		JsonPayloadProcessor.INSTANCE.processJsonPayload(resultPayload, context.getResponse());
		dataContainer.cleanUp();
	}

	public void setResultPayload(Object rp) {
		resultPayload = rp;
	}

	protected JsonObject buildJsonFromDocument(Document doc) throws NotesException {
		Document2JsonConverter d2jc = new Document2JsonConverter(doc, routeProcessor, context);
		return d2jc.buildJsonFromDocument();
	}
}