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

public class GetByFT implements StrategyModel<List<Document>> {

	private String databaseNameValue;
	private String ftQueryValue;
	private Database dbAccess;

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
	public List<Document> getModel(Context context) throws ExecutorException {
		try {
			dbAccess = DatabaseProvider.INSTANCE.getDatabase(databaseNameValue, context.getDatabase(), context.getSession());
			List<Document> docs = new ArrayList<Document>();
			String search = buildSearchString(context);
			DocumentCollection dcl = dbAccess.FTSearch(search);
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

	private String buildSearchString(Context context) {
		String rc = this.ftQueryValue;
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
