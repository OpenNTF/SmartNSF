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

	protected boolean isReturnTotals(final Context context) {
		String totals = context.getRequest().getParameter("totals");
		// skip counting only if we have parameter totals=off
		// if parameter is omitted, we assume it is on
		if (null == totals || totals.equals("on"))
			return true;
		return false;
	}

}