package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;

import com.ibm.commons.util.StringUtil;

import groovy.lang.Closure;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;

import lotus.domino.Database;
import lotus.domino.Document;

public class GetByUNID extends AbstractDatabaseStrategy implements StrategyModel<Document> {

	private String keyVariableValue;
	private Closure<?> keyVariableCl;
	private String formValue;
	private Closure<?> formCl;

	private Database dbAccess;

	public void keyVariableName(String name) {
		this.keyVariableValue = name;
	}

	public void keyVariableName(Closure<?> keyVariableCl) {
		this.keyVariableCl = keyVariableCl;
	}

	public void form(String name) {
		this.formValue = name;
	}

	public void form(Closure<?> formCl) {
		this.formCl = formCl;
	}

	public String getKeyVariableValue(Context context) {
		if (keyVariableCl != null) {
			return (String) DSLBuilder.callClosure(keyVariableCl, context);
		} else {
			return keyVariableValue;
		}
	}

	public String getFormValue(Context context) {
		if (formCl != null) {
			return (String) DSLBuilder.callClosure(formCl, context);
		} else {
			return formValue;
		}
	}

	@Override
	public Document getModel(Context context) throws ExecutorException {
		try {
			dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), context.getSession());
			String unid = context.getRouterVariables().get(keyVariableValue);
			if (unid.equalsIgnoreCase("@new")) {
				Document doc = dbAccess.createDocument();
				String form = getFormValue(context);
				if (!StringUtil.isEmpty(form)) {
					doc.replaceItemValue("Form", form);
				}
				return doc;
			}
			return dbAccess.getDocumentByUNID(unid);
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}
	}

	@Override
	public void cleanUp() {
		try {
			dbAccess.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
