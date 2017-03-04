package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;

import groovy.lang.Closure;

public class AbstractDatabaseStrategy {

	protected static final int DEFAULT_START = 1;
	protected static final int DEFAULT_COUNT = 10;
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

	/**
	 * Converts given param to int, assings defaultVal when it is empty or less
	 * than 1
	 * 
	 * @param param
	 * @param defaultVal
	 * @return int value of param or defaulVal
	 */
	protected int getParamIntValue(final String param, final int defaultVal) {
		int ret = defaultVal;
		if (null != param && !param.isEmpty()) {
			try {
				ret = Integer.parseInt(param);
				if (ret < 1) {
					ret = defaultVal;
				}
			} catch (NumberFormatException e) {
			}
		}
		return ret;
	}

}