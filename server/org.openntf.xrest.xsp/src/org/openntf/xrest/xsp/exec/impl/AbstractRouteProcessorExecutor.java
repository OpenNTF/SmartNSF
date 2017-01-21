package org.openntf.xrest.xsp.exec.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DataModel;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.RouteProcessorExecutor;
import org.openntf.xrest.xsp.exec.convertor.Document2JsonConverter;
import org.openntf.xrest.xsp.exec.output.ExecutorExceptionProcessor;
import org.openntf.xrest.xsp.exec.output.JsonPayloadProcessor;
import org.openntf.xrest.xsp.model.EventException;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonObject;

import groovy.lang.Closure;
import lotus.domino.Document;
import lotus.domino.NotesException;

public abstract class AbstractRouteProcessorExecutor implements RouteProcessorExecutor {

	private final Context context;
	private final RouteProcessor routeProcessor;
	private final String path;
	private DataModel<?> model;
	private Object resultPayload;

	public AbstractRouteProcessorExecutor(Context context, RouteProcessor routerProcessor, String path) {
		super();
		this.context = context;
		this.routeProcessor = routerProcessor;
		this.path = path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.xrest.xsp.exec.RouteProcessorExecutor#execute(java.lang.
	 * String)
	 */
	@Override
	public void execute() {
		try {
			checkAccess();
			validateRequest();
			preLoadDocument();
			loadDocument();
			postNewDocument();
			postLoadDocument();
			executeMethodeSpecific(this.context, this.model);
			preSubmitValues();
			submitValues();
		} catch (ExecutorException ex) {
			try {
				ExecutorExceptionProcessor.INSTANCE.processExecutorException(ex, context.getResponse());
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
		}
	}

	private void checkAccess() throws ExecutorException {
		List<String> allowedUsersAndGroups = routeProcessor.getAccessGroups();
		if (allowedUsersAndGroups == null || allowedUsersAndGroups.isEmpty()) {
			return;
		}
		List<String> myGroups = new ArrayList<String>();
		myGroups.add(context.getUserName());
		myGroups.addAll(context.getGroups());
		myGroups.addAll(context.getRoles());
		for (String me : myGroups) {
			if (allowedUsersAndGroups.contains(me)) {
				return;
			}
		}
		throw new ExecutorException(403, "Access denied for user " + context.getUserName(), path, "checkAccess");
	}

	private void validateRequest() throws ExecutorException {
		try {
			Closure<?> cl = routeProcessor.getEventClosure(EventType.VALIDATE);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context);
			}
		} catch (EventException e) {
			throw new ExecutorException(400, "Validation Error: " + e.getMessage(), e, path, "validation");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, path, "validation");
		}
	}

	private void preLoadDocument() throws ExecutorException {
		try {
			Closure<?> cl = routeProcessor.getEventClosure(EventType.PRE_LOAD_DOCUMENT);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context);
			}
		} catch (EventException e) {
			throw new ExecutorException(400, "Pre Load Error: " + e.getMessage(), e, path, "preloadmodel");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, path, "preloadmodel");
		}
	}

	private void loadDocument() throws ExecutorException {
		model = routeProcessor.getDataModel(context);
	}

	private void postNewDocument() throws ExecutorException {
		if (model.isList()) {
			return;
		}
		try {
			Document doc = (Document) model.getData();
			Closure<?> cl = routeProcessor.getEventClosure(EventType.POST_NEW);
			if (cl != null && doc.isNewNote()) {
				DSLBuilder.callClosure(cl, context, model);
			}
		} catch (EventException e) {
			throw new ExecutorException(400, "Post Load Error: " + e.getMessage(), e, path, "postloadmodel");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, path, "postloadmodel");
		}
	}

	private void postLoadDocument() throws ExecutorException {
		try {
			Closure<?> cl = routeProcessor.getEventClosure(EventType.POST_LOAD_DOCUMENT);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context, model);
			}
		} catch (EventException e) {
			throw new ExecutorException(400, "Post Load Error: " + e.getMessage(), e, path, "postloadmodel");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, path, "postloadmodel");
		}
	}

	private void preSubmitValues() throws ExecutorException {
		try {
			Closure<?> cl = routeProcessor.getEventClosure(EventType.PRE_SUBMIT);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context, model);
			}
		} catch (EventException e) {
			throw new ExecutorException(400, "Post Load Error: " + e.getMessage(), e, path, "presubmit");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, path, "presubmit");
		}
		model.cleanUp();
		routeProcessor.cleanUp();

	}

	private void submitValues() throws IOException, JsonException {
		JsonPayloadProcessor.INSTANCE.processJsonPayload(resultPayload, context.getResponse());
	}

	abstract protected void executeMethodeSpecific(Context context, DataModel<?> model) throws ExecutorException;

	public void setResultPayload(Object rp) {
		resultPayload = rp;
	}

	public void setModel(DataModel<?> model) {
		this.model = model;
	}

	protected JsonObject buildJsonFromDocument(Document doc) throws NotesException {
		Document2JsonConverter d2jc = new Document2JsonConverter(doc, routeProcessor, context);
		return d2jc.buildJsonFromDocument();
	}

	protected RouteProcessor getRouteProcessor() {
		return routeProcessor;
	}

	protected String getPath() {
		return path;
	}
}