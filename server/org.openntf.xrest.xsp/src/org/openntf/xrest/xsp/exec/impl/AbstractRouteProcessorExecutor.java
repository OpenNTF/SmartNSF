package org.openntf.xrest.xsp.exec.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.RouteProcessorExecutor;
import org.openntf.xrest.xsp.exec.output.ExecutorExceptionProcessor;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.EventException;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonException;

import groovy.lang.Closure;
import lotus.domino.Document;

public abstract class AbstractRouteProcessorExecutor implements RouteProcessorExecutor {



	protected final String path;

	public AbstractRouteProcessorExecutor(final String path) {
		this.path = path;
	}

	@Override
	public void execute(Context context, RouteProcessor rp) {
		try {
			checkAccess(context,rp);
			validateRequest(context,rp);
			preLoadDocument(context,rp);
			DataContainer<?> dataContainer = loadDocument(context,rp);
			postNewDocument(context,rp, dataContainer);
			postLoadDocument(context,rp, dataContainer);
			executeMethodeSpecific(context, dataContainer, rp);
			preSubmitValues(context,rp, dataContainer);
			submitValues(context,rp, dataContainer);
			dataContainer.cleanUp();
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
		}
	}

	protected abstract void submitValues(Context context,RouteProcessor routeProcessor, DataContainer<?> dataContainer) throws IOException, JsonException, ExecutorException;

	protected abstract void preSubmitValues(Context context,RouteProcessor routeProcessor, DataContainer<?> dataContainer) throws ExecutorException;
	private void checkAccess(Context context, RouteProcessor routeProcessor) throws ExecutorException {
		Closure<?> clAllowedAccess = routeProcessor.getAllowedAccessClosure();
		if (clAllowedAccess != null) {
			try {
				boolean result = (Boolean)DSLBuilder.callClosure(clAllowedAccess, context);
				if (!result) {
					throw new ExecutorException(403, "Access denied for user " + context.getUserName(), path, "checkAccess");
				}
			} catch (EventException e) {
				throw new ExecutorException(e, path, "checkAccess");
			} catch (Exception e) {
				throw new ExecutorException(500, "Runtime Error: " + e.getMessage(), e, path, "checkAccess");
			}
		}
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

	private void validateRequest(Context context, RouteProcessor routeProcessor) throws ExecutorException {
		try {
			Closure<?> cl = routeProcessor.getEventClosure(EventType.VALIDATE);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context);
			}
		} catch (EventException e) {
			throw new ExecutorException(e, path, "validation");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runtime Error: " + e.getMessage(), e, path, "validation");
		}
	}

	private void preLoadDocument(Context context, RouteProcessor routeProcessor) throws ExecutorException {
		try {
			Closure<?> cl = routeProcessor.getEventClosure(EventType.PRE_LOAD_DOCUMENT);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context);
			}
		} catch (EventException e) {
			throw new ExecutorException(e, path, "preloadmodel");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runtime Error: " + e.getMessage(), e, path, "preloadmodel");
		}
	}

	private DataContainer<?> loadDocument(Context context, RouteProcessor routeProcessor) throws ExecutorException {
		return routeProcessor.getDataContainer(context);
	}

	private void postNewDocument(Context context, RouteProcessor routeProcessor, DataContainer<?> dataContainer) throws ExecutorException {
		if (dataContainer.isList() || dataContainer.isBinary()) {
			return;
		}
		try {
			Document doc = (Document) dataContainer.getData();
			Closure<?> cl = routeProcessor.getEventClosure(EventType.POST_NEW);
			if (cl != null && doc.isNewNote()) {
				DSLBuilder.callClosure(cl, context, doc);
			}
		} catch (EventException e) {
			throw new ExecutorException(e, path, "postloadmodel");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runtime Error: " + e.getMessage(), e, path, "postloadmodel");
		}
	}

	private void postLoadDocument(Context context, RouteProcessor routeProcessor, DataContainer<?> dataContainer) throws ExecutorException {
		if (dataContainer.isList()) {
			return;
		}
		try {
			Closure<?> cl = routeProcessor.getEventClosure(EventType.POST_LOAD_DOCUMENT);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context, dataContainer.getData());
			}
		} catch (EventException e) {
			throw new ExecutorException(e, path, "postloadmodel");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runtime Error: " + e.getMessage(), e, path, "postloadmodel");
		}
	}

	protected abstract void executeMethodeSpecific(Context context, DataContainer<?> container, RouteProcessor routeProcessor) throws ExecutorException;

/*	public void setDataContainer(final DataContainer<?> container) {
		this.dataContainer = container;
	}
	*/

	protected String getPath() {
		return path;
	}

}