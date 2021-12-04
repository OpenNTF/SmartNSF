package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;

import groovy.lang.Closure;

public class AbstractKeyViewDatabaseStrategy extends AbstractViewDatabaseStrategy {

	private String keyVariableValue;
	private Closure<?> keyVariableCl;
	private Closure<?> calcKeyCL;

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
	
	public void calculateKey(Closure<?> calcCL) {
		this.calcKeyCL = calcCL;
	}
	
	public boolean hasCalculateKey() {
		return calcKeyCL != null;
	}
	
	public String evaluateKey(Context context) {
		if (calcKeyCL == null) {
			throw new IllegalArgumentException("No calculateKeyClosure specified!");
		}
		return (String)DSLBuilder.callClosure(calcKeyCL, context);
	}
	
	public String getKeyValue(Context context) {
		if (hasCalculateKey()) {
			return evaluateKey(context);
		} else {
			String keyVariable = getKeyVariableValue(context);
			if (context.getRouterVariables().containsKey(keyVariable)) {
				return context.getRouterVariables().get(keyVariable);
			} else {
				return context.getQueryStringVariables().get(keyVariable);
			}
		}
	}
}