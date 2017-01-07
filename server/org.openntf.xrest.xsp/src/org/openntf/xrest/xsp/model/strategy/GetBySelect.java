package org.openntf.xrest.xsp.model.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.Context;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;

public class GetBySelect implements StrategyModel<List<Document>>{

	private String databaseNameValue;
	private String selectQueryValue;
	private Database dbAccess;

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
	public List<Document> getModel(Context context) throws ExecutorException{
		try {
			dbAccess = DatabaseProvider.INSTANCE.getDatabase(databaseNameValue, context.getDatabase(), context.getSession());
			List<Document> docs = new ArrayList<Document>();
			String search = buildSelectString(context);
			DocumentCollection dcl = dbAccess.search(search);
			Document docNext = dcl.getFirstDocument();
			while (docNext != null) {
				Document docProcess = docNext;
				docNext = dcl.getNextDocument();
				docs.add(docProcess);
			}
			return docs;
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}

	}

	private String buildSelectString(Context context) {
		String rc = this.selectQueryValue;
		for (Entry<String, String> routeEntry : context.getRouterVariables().entrySet()) {
			rc = rc.replace("{" + routeEntry.getKey() + "}", routeEntry.getValue());
		}
		return rc;
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
