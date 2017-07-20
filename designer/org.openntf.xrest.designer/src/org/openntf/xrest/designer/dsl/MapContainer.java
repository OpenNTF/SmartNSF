package org.openntf.xrest.designer.dsl;

import java.util.Arrays;
import java.util.List;

import groovy.lang.Closure;

public class MapContainer {

	private final Class<?> containerClass;
	private final String methodName;
	private final String key;
	private final Class<?> valueClass;
	private final List<Class<?>> closureParameters;
	private final boolean valueIsClosure;

	private MapContainer(Class<?> containerClass, String methodName, String key, Class<?> valueClass, List<Class<?>> closureParameters, boolean isClosure) {
		super();
		this.containerClass = containerClass;
		this.methodName = methodName;
		this.key = key;
		this.valueClass = valueClass;
		this.closureParameters = closureParameters;
		this.valueIsClosure = isClosure;
	}

	public static MapContainer buildMakKeyWithClosure(Class<?> containerClass, String methodName, String key,  Class<?>... closureParameters) {
		return new MapContainer(containerClass, methodName, key, Closure.class, Arrays.asList(closureParameters),true);
	}

	public static MapContainer buildMapKeyWithValue(Class<?> containerClass, String methodName, String key, Class<?> valueClass) {
		return new MapContainer(containerClass, methodName, key, valueClass, null, false);
	}


	public Class<?> getContainerClass() {
		return containerClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getKey() {
		return key;
	}

	public Class<?> getValueClass() {
		return valueClass;
	}

	public List<Class<?>> getClosureParameters() {
		return closureParameters;
	}

	public boolean isValueIsClosure() {
		return valueIsClosure;
	}
}
