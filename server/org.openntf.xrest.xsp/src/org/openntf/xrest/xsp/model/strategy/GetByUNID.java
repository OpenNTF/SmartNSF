package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.exec.Context;

import lotus.domino.Document;

public class GetByUNID implements StrategyModel<Document> {

	private String databaseNameValue;
	private String keyVariableValue;

	public void databaseName(String dbName) {
		databaseNameValue = dbName;
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

	public String getKeyVariableValue() {
		return keyVariableValue;
	}

	public void setKeyVariableValue(String keyVariableValue) {
		this.keyVariableValue = keyVariableValue;
	}

	@Override
	public Document getModel(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

}
