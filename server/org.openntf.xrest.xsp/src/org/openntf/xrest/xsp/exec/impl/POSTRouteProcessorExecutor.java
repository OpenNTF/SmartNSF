package org.openntf.xrest.xsp.exec.impl;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.Json2DocumentConverter;
import org.openntf.xrest.xsp.exec.datacontainer.DocumentDataContainer;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.EventException;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaObject;

import groovy.lang.Closure;
import lotus.domino.Document;
import lotus.domino.NotesException;

public class POSTRouteProcessorExecutor extends AbstractRouteProcessorExecutor {

	public POSTRouteProcessorExecutor(Context context, RouteProcessor routeProcessor, String path) {
		super(context, routeProcessor, path);
	}

	@Override
	protected void executeMethodeSpecific(Context context, DataContainer<?> container) throws ExecutorException {
		if (container.isList()) {
			buildResultMapping(context, container);
		} else {
			DocumentDataContainer docContainer = (DocumentDataContainer)container;
			Closure<?> cl = getRouteProcessor().getEventClosure(EventType.ALT_DOCUMENT_UPDATE);
			if (cl != null) {
				executeAlternateDocumentUpdate(cl, context, docContainer);
			} else {
				executeDocumentUpdate(context, docContainer);
			}
			executePreSave(context, docContainer);
			executeDocumentSave(docContainer);
			executePostSave(context, docContainer);
			buildResultMapping(context, docContainer);
		}

	}

	private void executeAlternateDocumentUpdate(Closure<?> cl, Context context, DataContainer<?> model) throws ExecutorException {
		try {
			DSLBuilder.callClosure(cl, context, model.getData());
		} catch (EventException e) {
			throw new ExecutorException(400, "Alternate Document Update Error: " + e.getMessage(), e, getPath(), "executealternatedelete");
		} catch (Exception e) {
			throw new ExecutorException(500, "Alternate Document Update Error: " + e.getMessage(), e, getPath(), "executealternatedelete");
		}
	}

	private void executePreSave(Context context, DocumentDataContainer container) throws ExecutorException {
		try {
			Closure<?> cl = getRouteProcessor().getEventClosure(EventType.PRE_SAVE_DOCUMENT);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context, container.getData());
			}
		} catch (EventException e) {
			throw new ExecutorException(400, "Pre Load Error: " + e.getMessage(), e, getPath(), "presavedocument");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, getPath(), "presavedocument");
		}

	}

	private void executeDocumentUpdate(Context context, DocumentDataContainer container) throws ExecutorException {
		try {
			Document doc = container.getData();
			JsonJavaObject jso = (JsonJavaObject) context.getJsonPayload();
			Json2DocumentConverter converter = new Json2DocumentConverter(doc, getRouteProcessor(), jso);
			converter.buildDocumentFromJson();
		} catch (NotesException e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, getPath(), "applyPayLoad");
		}
	}

	private void executeDocumentSave(DocumentDataContainer container) throws ExecutorException {
		try {
			Document doc = container.getData();
			doc.save(true, false, true);
		} catch (NotesException e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, getPath(), "savedocument");
		}
	}

	private void executePostSave(Context context, DocumentDataContainer container) throws ExecutorException {
		try {
			Closure<?> cl = getRouteProcessor().getEventClosure(EventType.POST_SAVE_DOCUMENT);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context, container.getData(), container);
			}
		} catch (EventException e) {
			throw new ExecutorException(400, "Pre Load Error: " + e.getMessage(), e, getPath(), "postsavedocument");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, getPath(), "postsavedocument");
		}

	}

	private void buildResultMapping(Context context, DataContainer<?> container) {
		try {
			setResultPayload(getRouteProcessor().getStrategyModel().buildResponse(context, getRouteProcessor(), container));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
