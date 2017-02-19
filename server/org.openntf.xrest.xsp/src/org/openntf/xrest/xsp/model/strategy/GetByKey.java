package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;

import com.ibm.commons.util.StringUtil;

import groovy.lang.Closure;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.View;

public class GetByKey extends AbstractKeyViewDatabaseStrategy implements StrategyModel<Document> {

	private Database dbAccess;
	private View viewAccess;
	private String formValue;
	private Closure<?> formCl;


	public void form(String name) {
		this.formValue = name;
	}

	public void form(Closure<?> formCl) {
		this.formCl = formCl;
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
			viewAccess = dbAccess.getView(getViewNameValue(context));

			String key = context.getRouterVariables().get(getKeyVariableValue(context));
			if (key.equalsIgnoreCase("@new")) {
				Document doc = dbAccess.createDocument();
				String form = getFormValue(context);
				if (!StringUtil.isEmpty(form)) {
					doc.replaceItemValue("Form", form);
				}
				return doc;
			}
			return viewAccess.getDocumentByKey(key, true);
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}

	}

	@Override
	public void cleanUp() {
		try {
			viewAccess.recycle();
			dbAccess.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
