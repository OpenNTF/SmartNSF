package org.openntf.xrest.xsp.exec.impl;

import java.util.ArrayList;
import java.util.List;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.EventException;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import groovy.lang.Closure;
import lotus.domino.Document;
import lotus.domino.NotesException;

public class DELETERouteProcessorExecutor extends AbstractJsonRouteProcessorExecutor {

	public DELETERouteProcessorExecutor( final String path) {
		super( path);
	}

	@Override
	protected void executeMethodeSpecific(final Context context, final DataContainer<?> container, RouteProcessor routeProcessor) throws ExecutorException {
		preDelete(context, container,routeProcessor);
		List<String> deletedDocuments;
		Closure<?> cl = routeProcessor.getEventClosure(EventType.ALT_DOCUMENT_DELETE);
		if (cl != null) {
			deletedDocuments = executeAlternativeDelete(cl, context, container);
		} else {
			deletedDocuments = executeDeleteDocuments(container);
		}
		JsonObject jso = new JsonJavaObject();
		jso.putJsonProperty("deleted", deletedDocuments);
		setResultPayload(jso, context, routeProcessor);
	}

	@SuppressWarnings("unchecked")
	private List<String> executeDeleteDocuments(final DataContainer<?> container) throws ExecutorException {
		List<String> unids = new ArrayList<String>();
		try {
			if (container.isList()) {
				for (Document doc : (List<Document>) container.getData()) {
					unids.add(doc.getUniversalID());
					doc.remove(true);
				}
			} else {
				Document doc = (Document) container.getData();
				unids.add(doc.getUniversalID());
				doc.remove(true);
			}
		} catch (NotesException e) {
			throw new ExecutorException(500, "Runtime Error: " + e.getMessage(), e, getPath(), "executeDelete");
		}
		return unids;
	}

	private void preDelete(final Context context, final DataContainer<?> container, RouteProcessor routeProcessor) throws ExecutorException {
		try {
			Closure<?> cl = routeProcessor.getEventClosure(EventType.PRE_DELETE);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context, container.getData(), container);
			}
		} catch (EventException e) {
			throw new ExecutorException(e, getPath(), "predelete");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runtime Error: " + e.getMessage(), e, getPath(), "predelete");
		}

	}

	@SuppressWarnings("unchecked")
	private List<String> executeAlternativeDelete(final Closure<?> cl, final Context context, final DataContainer<?> container)
			throws ExecutorException {
		try {
			return (List<String>) DSLBuilder.callClosure(cl, context, container.getData(), container);
		} catch (EventException e) {
			throw new ExecutorException(e, getPath(), "executealternatedelete");
		} catch (Exception e) {
			throw new ExecutorException(500, "Alternate Delete Error: " + e.getMessage(), e, getPath(), "executealternatedelete");
		}
	}
}
