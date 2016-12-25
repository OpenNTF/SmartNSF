package org.openntf.xrest.xsp.model.strategy;

public class GetByUNID implements StrategyModel {

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

}
