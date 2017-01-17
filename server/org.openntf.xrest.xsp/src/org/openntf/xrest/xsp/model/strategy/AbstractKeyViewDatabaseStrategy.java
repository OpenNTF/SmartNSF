package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;

import groovy.lang.Closure;

public class AbstractKeyViewDatabaseStrategy extends AbstractViewDatabaseStrategy {

	private String keyVariableValue;
	private Closure<?> keyVariableCl;

	public AbstractKeyViewDatabaseStrategy() {
		super();
	}

	public void keyVariableName(String name) {
		this.keyVariableValue = name;
	}

	public void keyVariableName(Closure<?> keyCl) {
		this.keyVariableCl = keyCl;
	}

	public String getKeyVariableValue(Context context) {
		if (keyVariableCl != null) {
			return (String) DSLBuilder.callClosure(keyVariableCl, context);
		} else {
			return keyVariableValue;
		}
	}

}