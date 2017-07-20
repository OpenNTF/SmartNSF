package org.openntf.xrest.designer.codeassist;

import java.util.Map;

import org.openntf.xrest.designer.dsl.DSLRegistry;

public class ClassContextAnalyzerParameter {
	public DSLRegistry dslRegistry;
	public Map<String, Class<?>> declaredVariables;
	public String currentMethodName;
	public String currentVariable;
	public Class<?> currentClass;
	public Class<?> currentVariableClass;
	public String currentKey;
	public String currentFirstArgument;
	public boolean isMapKeyBased;

	public ClassContextAnalyzerParameter(DSLRegistry dslRegistry, Map<String, Class<?>> declaredVariables) {
		this.dslRegistry = dslRegistry;
		this.declaredVariables = declaredVariables;
	}
}