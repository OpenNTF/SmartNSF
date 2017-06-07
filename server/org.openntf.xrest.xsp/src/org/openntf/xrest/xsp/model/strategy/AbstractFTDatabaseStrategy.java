package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;

import groovy.lang.Closure;

public class AbstractFTDatabaseStrategy extends AbstractDatabaseStrategy {

	private String ftQueryValue;
	private Closure<?> ftQueryValueCl;

	public AbstractFTDatabaseStrategy() {
		super();
	}

	public String getFtQueryValue(final Context context) {
		if (ftQueryValueCl != null) {
			return (String) DSLBuilder.callClosure(ftQueryValueCl, context);
		} else {
			return ftQueryValue;
		}
	}

	public void ftQuery(final String ftQueryValue) {
		this.ftQueryValue = ftQueryValue;
	}

	public void ftQuery(final Closure<?> ftQueryValueCl) {
		this.ftQueryValueCl = ftQueryValueCl;
	}

}