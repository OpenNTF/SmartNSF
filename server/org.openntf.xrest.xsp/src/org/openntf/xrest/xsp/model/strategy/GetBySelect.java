package org.openntf.xrest.xsp.model.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;

import groovy.lang.Closure;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;

public class GetBySelect extends AbstractDatabaseStrategy implements StrategyModel<List<Document>> {

	private String selectQueryValue;
	private Closure<?> selectQueryCl;
	private Database dbAccess;

	public void selectQuery(String name) {
		this.selectQueryValue = name;
	}

	public void selectQuery(Closure<?> queryCl) {
		this.selectQueryCl = queryCl;
	}

	public String getSelectQueryValue(Context context) {
		if (selectQueryCl != null) {
			return (String) DSLBuilder.callClosure(selectQueryCl, context);
		} else {
			return selectQueryValue;
		}
	}

	@Override
	public List<Document> getModel(Context context) throws ExecutorException {
		try {
			dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), context.getSession());
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
		String rc = getSelectQueryValue(context);
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
