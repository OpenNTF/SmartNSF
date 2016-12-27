package org.openntf.xrest.xsp.model.strategy;

import java.util.List;

import org.openntf.xrest.xsp.exec.Context;

import lotus.domino.Document;

public class GetBySelect implements StrategyModel<List<Document>>{

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


	@Override
	public List<Document> getModel(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

}
