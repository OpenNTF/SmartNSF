package org.openntf.xrest.xsp.exec.impl;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.RouteProcessorExecutor;
import org.openntf.xrest.xsp.exec.convertor.datatypes.AttachmentProcessor;
import org.openntf.xrest.xsp.exec.datacontainer.AttachmentDataContainer;
import org.openntf.xrest.xsp.exec.datacontainer.DocumentDataContainer;
import org.openntf.xrest.xsp.exec.output.JsonPayloadProcessor;
import org.openntf.xrest.xsp.model.AttachmentUpdateType;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.EventException;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.model.strategy.SelectAttachment;

import com.ibm.commons.util.io.json.JsonException;

import groovy.lang.Closure;
import lotus.domino.Document;
import lotus.domino.NotesException;

public class POSTAttachmentRoutProcessorExecutor extends AbstractRouteProcessorExecutor
		implements RouteProcessorExecutor {

	public POSTAttachmentRoutProcessorExecutor(String path) {
		super(path);
	}

	@Override
	protected void submitValues(Context context, RouteProcessor routeProcessor, DataContainer<?> dataContainer)
			throws IOException, JsonException, ExecutorException {
		HttpServletResponse response = context.getResponse();
		response.setStatus(201);
		JsonPayloadProcessor.INSTANCE.processJsonPayload(context.getResultPayload(), context.getResponse());
	}

	@Override
	protected void preSubmitValues(Context context, RouteProcessor routeProcessor, DataContainer<?> dataContainer) throws ExecutorException {
		try {
			Closure<?> cl = routeProcessor.getEventClosure(EventType.PRE_SUBMIT);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context, dataContainer.getData());
			}
		} catch (EventException e) {
			throw new ExecutorException(e, path, "presubmit");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runtime Error: " + e.getMessage(), e, path, "presubmit");
		}

	}

	@Override
	protected void executeMethodeSpecific(Context context, DataContainer<?> container, RouteProcessor routeProcessor)
			throws ExecutorException {
		System.out.println(context.getFacesContext().getExternalContext().getContext().getClass().getName());
		AttachmentUpdateType updateType = ((SelectAttachment) routeProcessor.getStrategyModel()).getUpdateType();
		HttpServletRequest request = context.getRequest();
		AttachmentProcessor attachmentProcessor = AttachmentProcessor.getInstance();
		AttachmentDataContainer<?> adc = (AttachmentDataContainer<?>) container;
		executePreSave(context, adc.getDocumentDataContainer(), routeProcessor);
		try {
			String file = attachmentProcessor.storeFileUploadStream(request.getInputStream(), adc.getFileName());
			attachmentProcessor.addAttachment(adc.getDocumentDataContainer().document, adc.getFieldName(), file,
					adc.getFileName(), updateType);
			FileUtils.deleteDirectory(new File(file).getParentFile());
		} catch (Exception e) {
			throw new ExecutorException(500, e, path, "executeMethodSpecific");
		}
		executeDocumentSave(adc.getDocumentDataContainer());
		executePostSave(context, adc.getDocumentDataContainer(), routeProcessor);
		buildResultMapping(context, adc.getDocumentDataContainer(), routeProcessor);
	}

	private void executePreSave(final Context context, final DocumentDataContainer container,
			RouteProcessor routeProcessor) throws ExecutorException {
		try {
			Closure<?> cl = routeProcessor.getEventClosure(EventType.PRE_SAVE_DOCUMENT);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context, container.getData());
			}
		} catch (EventException e) {
			throw new ExecutorException(e, getPath(), "presavedocument");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runtime Error: " + e.getMessage(), e, getPath(), "presavedocument");
		}

	}

	private void executeDocumentSave(final DocumentDataContainer container) throws ExecutorException {
		try {
			Document doc = container.getData();
			doc.save(true, false, true);
		} catch (NotesException e) {
			throw new ExecutorException(500, "Runtime Error: " + e.getMessage(), e, getPath(), "savedocument");
		}
	}

	private void executePostSave(final Context context, final DocumentDataContainer container,
			RouteProcessor routeProcessor) throws ExecutorException {
		try {
			Closure<?> cl = routeProcessor.getEventClosure(EventType.POST_SAVE_DOCUMENT);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context, container.getData(), container);
			}
		} catch (EventException e) {
			throw new ExecutorException(e, getPath(), "postsavedocument");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runtime Error: " + e.getMessage(), e, getPath(), "postsavedocument");
		}

	}

	private void buildResultMapping(final Context context, final DataContainer<?> container,
			RouteProcessor routeProcessor) throws ExecutorException {
		try {
			SelectAttachment attachmentStrategy = (SelectAttachment) routeProcessor.getStrategyModel();
			context.setResultPayload(
					attachmentStrategy.getDocumentStrategyModel().buildResponse(context, routeProcessor, container));
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, getPath(), "buildResult");
		}
	}
}
