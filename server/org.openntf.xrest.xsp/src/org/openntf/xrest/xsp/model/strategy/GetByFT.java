package org.openntf.xrest.xsp.model.strategy;

public class GetByFT {

	private String databaseNameValue;
	private String ftQueryValue;

	public void databaseName(String dbName) {
		databaseNameValue = dbName;
	}


	public void ftQuery(String name) {
		this.ftQueryValue = name;
	}

	public String getDatabaseNameValue() {
		return databaseNameValue;
	}

	public void setDatabaseNameValue(String databaseNameValue) {
		this.databaseNameValue = databaseNameValue;
	}


	public String getFtQueryValue() {
		return ftQueryValue;
	}

	public void setFtQueryValue(String keyVariableValue) {
		this.ftQueryValue = keyVariableValue;
	}

}
