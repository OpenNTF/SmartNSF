package org.openntf.xrest.designer.dsl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DSLRegistry {

	private final Class<?> baseClass;
	private final String baseAlias;
	private final Map<String, MethodContainer> registeredMethods = new HashMap<String, MethodContainer>();
	private final Map<String, List<MapContainer>> registeredMaps = new HashMap<String, List<MapContainer>>();

	public DSLRegistry(String alias, Class<?> class1) {
		this.baseAlias = alias;
		this.baseClass = class1;
	}

	public void addClosureObjecForMethod(String method, Class<?> executorClass) {
		MethodContainer mc = MethodContainer.buildContainer(baseClass, method, executorClass);
		addClosureObjecForMethod(mc);
	}

	public void addClosureObjecForMethod(MethodContainer mc) {
		registeredMethods.put(mc.getContainerClass().getName() + "#" + mc.getMethodName(), mc);
	}

	public Object getObjectForClosureInMethod(String method) {
		return getObjectForClosureInMethod(baseClass, method);
	}

	public Class<?> getObjectForClosureInMethod(Class<?> cl, String method) {
		return registeredMethods.get(cl.getName() + "#" + method).getClosureClass();
	}

	public void addClosureObjectForMethodWithCondtion(String method, Object condition, Class<?> executorClass) {
		MethodContainer mc = MethodContainer.buildContainerWithCondition(baseClass, method, condition.toString(), executorClass);
		addClosureObjecForMethodWithCondition(mc);
	}

	public void addClosureObjecForMethodWithCondition(MethodContainer mc) {
		registeredMethods.put(mc.getContainerClass().getName() + "#" + mc.getMethodName() + "!" + mc.getCondition(), mc);
	}

	public Class<?> getObjectForClosureInMethodByCondition(Class<?> cl, String method, Object condition) {
		return registeredMethods.get(cl.getName() + "#" + method + "!" + condition.toString()).getClosureClass();
	}

	public Class<?> getObjectForClosureInMethodByCondition(String method, Object condition) {
		return getObjectForClosureInMethodByCondition(baseClass, method, condition);
	}

	public boolean isMethodConditioned(String method) {
		return isMethodConditioned(baseClass, method);
	}

	public boolean isMethodConditioned(Class<?> cl, String method) {
		String toSearchFor = cl.getName() + "#" + method + "!";
		for (String key : registeredMethods.keySet()) {
			if (key.startsWith(toSearchFor)) {
				return true;
			}
		}
		return false;
	}

	public void addMapKeyClosure(MapContainer mc) {
		String key = buildKeyForMapContainer(mc.getContainerClass(), mc.getMethodName());
		List<MapContainer> mcs = null;
		if (registeredMaps.containsKey(key)) {
			mcs = registeredMaps.get(key);
		} else {
			mcs = new ArrayList<MapContainer>();
			registeredMaps.put(key, mcs);
		}
		mcs.add(mc);
	}

	public List<String> getMapKeys(Class<?> cl, String method) {
		List<String> keys = new ArrayList<String>();
		String mcKey = buildKeyForMapContainer(cl, method);
		if (registeredMaps.containsKey(mcKey)) {
			List<MapContainer> mcs = registeredMaps.get(mcKey);
			for (MapContainer mc : mcs) {
				keys.add(mc.getKey());
			}
		}
		return keys;
	}

	public List<MapContainer> getMapContainers(Class<?> cl, String method) {
		String mcKey = buildKeyForMapContainer(cl, method);
		if (registeredMaps.containsKey(mcKey)) {
			return new ArrayList<MapContainer>(registeredMaps.get(mcKey));
		}
		return Collections.emptyList();
		
	}

	private String buildKeyForMapContainer(Class<?> cl, String method) {
		return cl.getName() + "#" + method;
	}

}
