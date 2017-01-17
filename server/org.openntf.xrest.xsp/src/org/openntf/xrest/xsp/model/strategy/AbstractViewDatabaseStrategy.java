package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;

import groovy.lang.Closure;

public class AbstractViewDatabaseStrategy extends AbstractDatabaseStrategy {

	private String viewNameValue;
	private Closure<?> viewNameCl;

	public AbstractViewDatabaseStrategy() {
		super();
	}

	public void viewName(String viewName) {
		this.viewNameValue = viewName;
	}

	public void viewName(Closure<?> viewNameCl) {
		this.viewNameCl = viewNameCl;
	}

	public String getViewNameValue(Context context) {
		if (this.viewNameCl != null) {
			return (String) DSLBuilder.callClosure(viewNameCl, context);
		} else {
			return viewNameValue;
		}
	}

}