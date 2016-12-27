package org.openntf.xrest.xsp.model.strategy;

import java.util.List;

import org.openntf.xrest.xsp.exec.Context;

import lotus.domino.Document;

public class AllByKey implements StrategyModel<List<Document>>{

	private String databaseNameValue;
	private String viewNameValue;
	private String keyVariableValue;

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
	public List<Document> getModel(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

}
