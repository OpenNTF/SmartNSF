package org.openntf.xrest.xsp.model.strategy;

import java.util.ArrayList;
import java.util.List;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;

import groovy.lang.Closure;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;

public class AbstractDatabaseStrategy {

	protected static final int DEFAULT_START = 1;
	protected static final int DEFAULT_COUNT = 10;
	private String databaseNameValue;
	private Closure<?> databaseNameCl;

	public AbstractDatabaseStrategy() {
		super();
	}

	public void databaseName(final String dbName) {
		databaseNameValue = dbName;
	}

	public void databaseName(final Closure<?> dbNameCl) {
		databaseNameCl = dbNameCl;
	}

	public String getDatabaseNameValue(final Context context) {
		if (databaseNameCl != null) {
			return (String) DSLBuilder.callClosure(databaseNameCl, context);
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

	/**
	 * For given DocumentColletion returns {@code count} number of documents
	 * starting at {@code start}
	 * 
	 * @param dcl
	 * @param start
	 * @param count
	 * @return
	 * @throws Exception
	 */
	protected List<Document> getPagedListFromDocCollection(final DocumentCollection dcl, final int start, final int count)
			throws Exception {
		List<Document> docs = new ArrayList<Document>();
		Document docNext = null;
		if (start > 1) {
			docNext = dcl.getNthDocument(start);
		} else {
			docNext = dcl.getFirstDocument();
		}
		int i = 0;
		while (docNext != null && i < count) {
			Document docProcess = docNext;
			docNext = dcl.getNextDocument();
			docs.add(docProcess);
			i++;
		}
		dcl.recycle();
		return docs;
	}

}