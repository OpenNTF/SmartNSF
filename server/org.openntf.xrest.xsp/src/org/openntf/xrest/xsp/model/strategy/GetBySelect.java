package org.openntf.xrest.xsp.model.strategy;

public class GetBySelect implements StrategyModel{

	private String databaseNameValue;
	private String selectQueryValue;

	public void databaseName(String dbName) {
		databaseNameValue = dbName;
	}


	public void selectQuery(String name) {
		this.selectQueryValue = name;
	}

	public String getDatabaseNameValue() {
		return databaseNameValue;
	}

	public void setDatabaseNameValue(String databaseNameValue) {
		this.databaseNameValue = databaseNameValue;
	}


	public String getSelectQueryValue() {
		return selectQueryValue;
	}

	public void setSelectQueryValue(String keyVariableValue) {
		this.selectQueryValue = keyVariableValue;
	}

}
