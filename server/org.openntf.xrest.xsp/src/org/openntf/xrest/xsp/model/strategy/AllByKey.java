package org.openntf.xrest.xsp.model.strategy;

import java.util.ArrayList;
import java.util.List;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.View;
import lotus.domino.DocumentCollection;

public class AllByKey implements StrategyModel<List<Document>> {

	private String databaseNameValue;
	private String viewNameValue;
	private String keyVariableValue;

	private Database dbAccess;
	private View viewAccess;

	public void databaseName(String dbName) {
		databaseNameValue = dbName;
	}

	public void viewName(String viewName) {
		this.viewNameValue = viewName;
	}

	public void keyVariableName(String name) {
		this.keyVariableValue = name;
	}

	public String getDatabaseNameValue() {
		return databaseNameValue;
	}

	public void setDatabaseNameValue(String databaseNameValue) {
		this.databaseNameValue = databaseNameValue;
	}

	public String getViewNameValue() {
		return viewNameValue;
	}

	public void setViewNameValue(String viewNameValue) {
		this.viewNameValue = viewNameValue;
	}

	public String getKeyVariableValue() {
		return keyVariableValue;
	}

	public void setKeyVariableValue(String keyVariableValue) {
		this.keyVariableValue = keyVariableValue;
	}

	@Override
	public List<Document> getModel(Context context) throws ExecutorException {
		try {
			dbAccess = DatabaseProvider.INSTANCE.getDatabase(databaseNameValue, context.getDatabase(), context.getSession());
			viewAccess = dbAccess.getView(viewNameValue);
			List<Document> docs = new ArrayList<Document>();
			String varValue = context.getRouterVariables().get(keyVariableValue);
			DocumentCollection dcl = viewAccess.getAllDocumentsByKey(varValue);
			
			Document docNext = dcl.getFirstDocument();
			while (docNext != null) {
				Document docProcess = docNext;
				docNext = dcl.getNextDocument();
				docs.add(docProcess);
			}
			dcl.recycle();
			return docs;
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
