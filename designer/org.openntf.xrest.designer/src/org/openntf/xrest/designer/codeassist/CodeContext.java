package org.openntf.xrest.designer.codeassist;

import java.util.Map;

public class CodeContext {

	private final Map<String, Class<?>> declaredVariables;
	private final Class<?> currentClassContext;

	public CodeContext(Map<String, Class<?>> declaredVariables, Class<?> currentClassContext) {
		this.declaredVariables = declaredVariables;
		this.currentClassContext = currentClassContext;
	}

	public Map<String, Class<?>> getDeclaredVariables() {
		return declaredVariables;
	}

	public Class<?> currentClassContext() {
		return currentClassContext;
	}

}
