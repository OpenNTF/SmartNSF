package org.openntf.xrest.xsp.exec.impl;

import java.util.List;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DataModel;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.Json2DocumentConverter;
import org.openntf.xrest.xsp.model.EventException;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonArray;
import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import groovy.lang.Closure;
import lotus.domino.Document;
import lotus.domino.NotesException;

public class POSTRouteProcessorExecutor extends AbstractRouteProcessorExecutor {

	public POSTRouteProcessorExecutor(Context context, RouteProcessor routeProcessor, String path) {
		super(context, routeProcessor, path);
	}

	@Override
	protected void executeMethodeSpecific(Context context, DataModel<?> model) throws ExecutorException {
		if (model.isList()) {
			buildResultMapping(context, model);
		} else {
			executePreSave(context, model);
			Closure<?> cl = getRouteProcessor().getEventClosure(EventType.ALT_DOCUMENT_UPDATE);
			if (cl != null) {
				executeAlternateDocumentUpdate(cl, context, model);
			} else {
				executeDocumentUpdateAndSave(context, model);
			}
			executePostSave(context, model);
			buildResultMapping(context, model);
		}

	}

	private void executeAlternateDocumentUpdate(Closure<?> cl, Context context, DataModel<?> model) throws ExecutorException {
		try {
			DSLBuilder.callClosure(cl, context, model.getData());
		} catch (EventException e) {
			throw new ExecutorException(400, "Alternate Document Update Error: " + e.getMessage(), e, getPath(), "executealternatedelete");
		} catch (Exception e) {
			throw new ExecutorException(500, "Alternate Document Update Error: " + e.getMessage(), e, getPath(), "executealternatedelete");
		}
	}

	private void executePreSave(Context context, DataModel<?> model) throws ExecutorException {
		try {
			Closure<?> cl = getRouteProcessor().getEventClosure(EventType.PRE_SAVE_DOCUMENT);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context, model.getData());
			}
		} catch (EventException e) {
			throw new ExecutorException(400, "Pre Load Error: " + e.getMessage(), e, getPath(), "presavedocument");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, getPath(), "presavedocument");
		}

	}

	private void executeDocumentUpdateAndSave(Context context, DataModel<?> model) throws ExecutorException {
		try {
			Document doc = (Document) model.getData();
			JsonJavaObject jso = (JsonJavaObject) context.getJsonPayload();
			Json2DocumentConverter converter = new Json2DocumentConverter(doc, getRouteProcessor(), jso);
			converter.buildDocumentFromJson();
		} catch (NotesException e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, getPath(), "applyPayLoad");
		}
	}

	private void executePostSave(Context context, DataModel<?> model) throws ExecutorException {
		try {
			Closure<?> cl = getRouteProcessor().getEventClosure(EventType.POST_SAVE_DOCUMENT);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context, model.getData());
			}
		} catch (EventException e) {
			throw new ExecutorException(400, "Pre Load Error: " + e.getMessage(), e, getPath(), "postsavedocument");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, getPath(), "postsavedocument");
		}

	}

	private void buildResultMapping(Context context, DataModel<?> model) {
		try {
			if (model.isList()) {
				JsonArray jsa = new JsonJavaArray();
				@SuppressWarnings("unchecked")
				List<Document> documents = (List<Document>) model.getData();
				for (Document doc : documents) {
					JsonObject jo = buildJsonFromDocument(doc);
					jsa.add(jo);
				}
				setResultPayload(jsa);
			} else {
				Document doc = (Document) model.getData();
				JsonObject jo = buildJsonFromDocument(doc);
				setResultPayload(jo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
