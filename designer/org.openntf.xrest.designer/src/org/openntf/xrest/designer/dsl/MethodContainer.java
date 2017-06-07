package org.openntf.xrest.designer.dsl;

import java.util.Collections;
import java.util.List;

public class MethodContainer {

	private final Class<?> containerClass;
	private final String methodName;
	private final String condition;
	private final Class<?> closureClass;
	private final List<Class<?>> closureParameters;

	public static MethodContainer buildContainerWithConditionAndParams(Class<?> containerClass, String methodName, String condition, Class<?> closureClass, List<Class<?>> closureParameters) {
		return new MethodContainer(containerClass, methodName, condition, closureClass, closureParameters);
	}

	public static MethodContainer buildContainerWithCondition(Class<?> containerClass, String methodName, String condition, Class<?> closureClass) {
		return new MethodContainer(containerClass, methodName, condition, closureClass, null);
	}

	public static MethodContainer buildContainer(Class<?> containerClass, String methodName, Class<?> closureClass) {
		return new MethodContainer(containerClass, methodName, null, closureClass, null);
	}

	private MethodContainer(Class<?> containerClass, String methodName, String condition, Class<?> closure, List<Class<?>> closureParameters) {
		super();
		this.containerClass = containerClass;
		this.methodName = methodName;
		this.condition = condition;
		this.closureClass = closure;
		this.closureParameters = closureParameters;
	}

	public Class<?> getContainerClass() {
		return containerClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getCondition() {
		return condition;
	}

	public Class<?> getClosureClass() {
		return closureClass;
	}

	public List<Class<?>> getClosureParameters() {
		return closureParameters;
	}

}
