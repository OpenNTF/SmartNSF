package org.openntf.xrest.designer.dsl;

import java.util.HashMap;
import java.util.Map;

public class DSLRegistry {

	private final Class<?> baseClass;
	private final String baseAlias;
	private final Map<String, Class<?>> registeredMethods = new HashMap<String, Class<?>>();

	public DSLRegistry(String alias, Class<?> class1) {
		this.baseAlias = alias;
		this.baseClass = class1;
	}

	public void addClosureObjecForMethod(String method, Class<?> executorClass) {
		addClosureObjecForMethod(baseClass, method, executorClass);
	}

	public void addClosureObjecForMethod(Class<?> cl, String method, Class<?> executorClass) {
		registeredMethods.put(cl.getName() + "#" + method, executorClass);
	}

	public Object getObjectForClosureInMethod(String method) {
		return getObjectForClosureInMethod(baseClass, method);
	}

	public Class<?> getObjectForClosureInMethod(Class<?> cl, String method) {
		return registeredMethods.get(cl.getName() + "#" + method);
	}

	public void addClosureObjectForMethodWithCondtion(String method, Object condition, Class<?> executorClass) {
		addClosureObjecForMethodWithCondition(baseClass, method, condition, executorClass);
	}

	public void addClosureObjecForMethodWithCondition(Class<?> cl, String method, Object condition, Class<?> executorClass) {
		registeredMethods.put(cl.getName() + "#" + method + "!" + condition.toString(), executorClass);
	}

	public Class<?> getObjectForClosureInMethodByCondition(Class<?> cl, String method, Object condition) {
		return registeredMethods.get(cl.getName() + "#" + method + "!" + condition.toString());
	}

	public Class<?> getObjectForClosureInMethodByCondition(String method, Object condition) {
		return getObjectForClosureInMethodByCondition(baseClass, method, condition);
	}
	
}
