package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.datatypes.AttachmentProcessor;
import org.openntf.xrest.xsp.exec.datacontainer.AttachmentDataContainer;
import org.openntf.xrest.xsp.exec.datacontainer.DocumentDataContainer;
import org.openntf.xrest.xsp.model.AttachmentSelectionType;
import org.openntf.xrest.xsp.model.AttachmentUpdateType;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.model.Strategy;

import groovy.lang.Closure;
import lotus.domino.EmbeddedObject;
import lotus.domino.Item;
import lotus.domino.MIMEEntity;
import lotus.domino.NotesException;

public class SelectAttachment implements StrategyModel<AttachmentDataContainer<?>, MIMEEntity> {

	private Strategy strategyValue;
	private StrategyModel<?, ?> strategyModel;

	private String fieldName;
	private Closure<?> fieldNameCl;
	private AttachmentSelectionType selectionTypeValue;
	private String attachmentNameVariableNameValue;
	private Closure<?> attachmentNameVariableNameValueCl;
	private AttachmentUpdateType updateTypeValue;

	public void documentStrategy(Strategy strat, Closure<Void> cl) throws InstantiationException, IllegalAccessException {
		strategyValue = strat;
		strategyModel = strat.constructModel();
		DSLBuilder.applyClosureToObject(cl, strategyModel);
	}

	public void fieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void fieldName(Closure<?> fieldNameCl) {
		this.fieldNameCl = fieldNameCl;
	}

	public void selectionType(AttachmentSelectionType type) {
		this.selectionTypeValue = type;
	}

	public void attachmentNameVariableName(String attachmentNaveVariableName) {
		this.attachmentNameVariableNameValue = attachmentNaveVariableName;
	}

	public void attachmentNameVariableName(Closure<?> attachmentNaveVariableNameCl) {
		this.attachmentNameVariableNameValueCl = attachmentNaveVariableNameCl;
	}

	public void updateType(AttachmentUpdateType type) {
		this.updateTypeValue = type;
	}

	@Override
	public MIMEEntity buildResponse(Context context, RouteProcessor routeProcessor, DataContainer<?> dc) throws NotesException {
		return null;
	}

	public String getFieldName(Context context) {
		if (fieldNameCl != null) {
			return (String) DSLBuilder.callClosure(fieldNameCl, context);
		} else {
			return fieldName;
		}
	}

	public String getAttachmentNameVariableName(Context context) {
		if (attachmentNameVariableNameValueCl != null) {
			return (String) DSLBuilder.callClosure(attachmentNameVariableNameValueCl, context);
		} else {
			return attachmentNameVariableNameValue;
		}
	}

	public AttachmentSelectionType getSelectionType() {
		return selectionTypeValue;
	}

	public AttachmentUpdateType getUpdateType() {
		return updateTypeValue;
	}

	@Override
	public AttachmentDataContainer<?> buildDataContainer(Context context) throws ExecutorException {
		try {
			DocumentDataContainer dd = (DocumentDataContainer) strategyModel.buildDataContainer(context);
			String calcFieldName = getFieldName(context);
			String calcFileName = context.getRouterVariables().get(getAttachmentNameVariableName(context));
			if (AttachmentProcessor.getInstance().isMime(dd.getData(), calcFieldName)) {
				MIMEEntity mimeEntity = AttachmentProcessor.getInstance().getMimeAttachment(dd.getData(), calcFieldName, calcFileName);
				return new AttachmentDataContainer<MIMEEntity>(dd, mimeEntity, calcFieldName, calcFileName, null);
			} else {
				Item item = dd.getData().getFirstItem(calcFieldName);
				EmbeddedObject embo = AttachmentProcessor.getInstance().getEmbeddedObjectAttachment(dd.getData(), item, calcFileName);
				return new AttachmentDataContainer<EmbeddedObject>(dd, embo, calcFieldName, calcFileName, item);

			}
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}
	}

}
