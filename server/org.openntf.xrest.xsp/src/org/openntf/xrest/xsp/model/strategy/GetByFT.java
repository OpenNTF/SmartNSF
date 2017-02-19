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

public class GetByFT extends AbstractDatabaseStrategy implements StrategyModel<List<Document>> {

	private String ftQueryValue;
	private Closure<?> ftQueryValueCl;
	private Database dbAccess;

	public String getFtQueryValue(Context context) {
		if (ftQueryValueCl != null) {
			return (String) DSLBuilder.callClosure(ftQueryValueCl, context);
		} else {
			return ftQueryValue;
		}
	}

	public void ftQueryValue(String keyVariableValue) {
		this.ftQueryValue = keyVariableValue;
	}

	public void ftQueryValue(Closure<?> keyVariableCl) {
		this.ftQueryValueCl = keyVariableCl;
	}

	@Override
	public List<Document> getModel(Context context) throws ExecutorException {
		try {
			dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), context.getSession());
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
		String rc = getFtQueryValue(context);
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
