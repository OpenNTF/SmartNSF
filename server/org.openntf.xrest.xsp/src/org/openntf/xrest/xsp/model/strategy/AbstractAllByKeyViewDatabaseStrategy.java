package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;

import groovy.lang.Closure;

public class AbstractAllByKeyViewDatabaseStrategy extends AbstractKeyViewDatabaseStrategy {

	private String modeValue;
	private Closure<?> modeMatchCl;

	public AbstractAllByKeyViewDatabaseStrategy() {
		super();
	}

	public void mode(final String mode) {
		this.modeValue = mode;
	}

	public void mode(final Closure<?> modeCl) {
		this.modeMatchCl = modeCl;
	}

	public String getModeValue(final Context context) {
		if (this.modeMatchCl != null) {
			return (String) DSLBuilder.callClosure(modeMatchCl, context);
		} else {
			return modeValue;
		}
	}

	public boolean isExact(final Context context) {
		String mode = getModeValue(context);
		if (null != mode && mode.equalsIgnoreCase("exact")) {
			return true;
		}
		return false;
	}

}