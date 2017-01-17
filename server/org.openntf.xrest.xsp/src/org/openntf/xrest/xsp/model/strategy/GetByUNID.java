package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;

import groovy.lang.Closure;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;

import lotus.domino.Database;
import lotus.domino.Document;

public class GetByUNID extends AbstractDatabaseStrategy implements StrategyModel<Document> {

	private String keyVariableValue;
	private Closure<?> keyVariableCl;
	private Database dbAccess;

	public void keyVariableName(String name) {
		this.keyVariableValue = name;
	}

	public void keyVariableName(Closure<?> keyVariableCl) {
		this.keyVariableCl = keyVariableCl;
	}

	public String getKeyVariableValue(Context context) {
		if (keyVariableCl != null) {
			return (String) DSLBuilder.callClosure(keyVariableCl, context);
		} else {
			return keyVariableValue;
		}
	}

	@Override
	public Document getModel(Context context) throws ExecutorException {
		try {
			dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), context.getSession());
			String unid = context.getRouterVariables().get(keyVariableValue);
			if (unid.equalsIgnoreCase("@new")) {
				return dbAccess.createDocument();
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
