package org.openntf.xrest.xsp.model.strategy;

import java.util.List;

import org.openntf.xrest.xsp.exec.Context;

import lotus.domino.Document;

public class GetByFT implements StrategyModel<List<Document>>{

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


	@Override
	public List<Document> getModel(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

}
