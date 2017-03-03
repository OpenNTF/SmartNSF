package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;

import groovy.lang.Closure;

public class AbstractViewDatabaseStrategy extends AbstractDatabaseStrategy {

	protected static final int DEFAULT_START = 1;
	protected static final int DEFAULT_COUNT = 10;
	private String viewNameValue;
	private Closure<?> viewNameCl;

	public AbstractViewDatabaseStrategy() {
		super();
	}

	public void viewName(final String viewName) {
		this.viewNameValue = viewName;
	}

	public void viewName(final Closure<?> viewNameCl) {
		this.viewNameCl = viewNameCl;
	}

	public String getViewNameValue(final Context context) {
		if (this.viewNameCl != null) {
			return (String) DSLBuilder.callClosure(viewNameCl, context);
		} else {
			return viewNameValue;
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