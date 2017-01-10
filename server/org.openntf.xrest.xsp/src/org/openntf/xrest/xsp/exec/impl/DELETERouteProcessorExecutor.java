package org.openntf.xrest.xsp.exec.impl;

import org.openntf.xrest.xsp.exec.DataModel;
import org.openntf.xrest.xsp.exec.ExecutorException;

import java.util.ArrayList;
import java.util.List;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.model.EventException;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import groovy.lang.Closure;
import lotus.domino.Document;
import lotus.domino.NotesException;

public class DELETERouteProcessorExecutor extends AbstractRouteProcessorExecutor {

	public DELETERouteProcessorExecutor(Context context, RouteProcessor routerProcessor, String path) {
		super(context, routerProcessor, path);
	}

	@Override
	protected void executeMethodeSpecific(Context context, DataModel<?> model) throws ExecutorException {
		preDelete(context, model);
		List<String> deletedDocuments;
		Closure<?> cl = getRouteProcessor().getEventClosure(EventType.ALT_DOCUMENT_DELETE);
		if (cl != null) {
			deletedDocuments = executeAlternativeDelete(cl, context, model);
		} else {
			deletedDocuments = executeDelteDocuments(model);
		}
		JsonObject jso = new JsonJavaObject();
		jso.putJsonProperty("deleted", deletedDocuments);
		setResultPayload(jso);
	}

	@SuppressWarnings("unchecked")
	private List<String> executeDelteDocuments(DataModel<?> model) throws ExecutorException {
		List<String> unids = new ArrayList<String>();
		try {
			if (model.isList()) {
				for (Document doc : (List<Document>) model.getData()) {
					unids.add(doc.getUniversalID());
					doc.remove(true);
				}
			} else {
				Document doc = (Document) model.getData();
				unids.add(doc.getUniversalID());
				doc.remove(true);
			}
		} catch (NotesException e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, getPath(), "executeDelete");
		}
		return unids;
	}

	private void preDelete(Context context, DataModel<?> model) throws ExecutorException {
		try {
			Closure<?> cl = getRouteProcessor().getEventClosure(EventType.PRE_DELETE);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context, model);
			}
		} catch (EventException e) {
			throw new ExecutorException(400, "Pre Delete Error: " + e.getMessage(), e, getPath(), "predelete");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, getPath(), "predelete");
		}

	}

	@SuppressWarnings("unchecked")
	private List<String> executeAlternativeDelete(Closure<?> cl, Context context, DataModel<?> model) throws ExecutorException {
		try {
			return (List<String>) DSLBuilder.callClosure(cl, context, model);
		} catch (EventException e) {
			throw new ExecutorException(400, "Alternate Delete Error: " + e.getMessage(), e, getPath(), "executealternatedelete");
		} catch (Exception e) {
			throw new ExecutorException(500, "Alternate Delete Error: " + e.getMessage(), e, getPath(), "executealternatedelete");
		}
	}
}
