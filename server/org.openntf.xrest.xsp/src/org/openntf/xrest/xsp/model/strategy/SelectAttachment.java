package org.openntf.xrest.xsp.model.strategy;

import java.io.OutputStream;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.AttachmentSelectionType;
import org.openntf.xrest.xsp.model.AttachmentUpdateType;
import org.openntf.xrest.xsp.model.Strategy;

import groovy.lang.Closure;

public class SelectAttachment implements StrategyModel<OutputStream> {

	private Strategy strategyValue;
	private StrategyModel<?> strategyModel;

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
	public OutputStream getModel(Context context) throws ExecutorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

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

}
