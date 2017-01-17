package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;

import groovy.lang.Closure;

public class AbstractDatabaseStrategy {

	private String databaseNameValue;
	private Closure<?> databaseNameCl;

	public AbstractDatabaseStrategy() {
		super();
	}

	public void databaseName(String dbName) {
		databaseNameValue = dbName;
	}

	public void databaseName(Closure<?> dbNameCl) {
		databaseNameCl = dbNameCl;
	}

	
	public String getDatabaseNameValue(Context context) {
		if (databaseNameCl != null) {
			return (String)DSLBuilder.callClosure(databaseNameCl, context);
		} else {
			return databaseNameValue;
		}
	}

}