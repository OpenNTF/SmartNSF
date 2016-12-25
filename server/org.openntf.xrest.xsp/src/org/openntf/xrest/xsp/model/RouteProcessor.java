package org.openntf.xrest.xsp.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.openntf.xrest.xsp.dsl.DSLBuilder;

import groovy.lang.Closure;

public class RouteProcessor {
	private final String[] pathElements;
	private final String route;
	private final Map<Integer, String> variablePositionMap = new TreeMap<Integer, String>();
	private List<String> accessGroups = new ArrayList<String>();
	private Closure<?> accessGroupsCL;

	public RouteProcessor(String path) {
		route = path;
		pathElements = path.split("/");
		extratVariableFromPath();
	}

	private void extratVariableFromPath() {
		for (int index = 0; index < pathElements.length; index++) {
			String element = pathElements[index];
			if (element.startsWith("{") && element.endsWith("}")) {
				String variable = element.substring(1, element.length() - 1);
				variablePositionMap.put(Integer.valueOf(index), variable);
			}
		}
	}

	public void strategy(Strategy strat, Closure<Void> cl) {
		System.out.println("strat: " + strat);
	}

	public void events(Map<String, Object> events) {
		System.out.println("Events: " + events);
	}

	public void accessPermission(String[] acc) {
		if (acc != null) {
			accessGroups.addAll(Arrays.asList(acc));
		}
	}

	public void accessPermission(Closure<?> cl) {
		accessGroupsCL = cl;
	}

	public void mapJson(Map<String, Object> options, String fieldName) {
		System.out.println("FN: " + fieldName);
	}

	public void mapJson(String fieldName) {
		System.out.println("FN: " + fieldName);
	}

	public String getRoute() {
		return route;
	}

	public List<String> getVariables() {
		return new ArrayList<String>(variablePositionMap.values());
	}

	public Map<String, String> extractValuesFromPath(String path) {
		String[] parts = path.split("/");
		Map<String, String> extract = new HashMap<String, String>();
		for (Entry<Integer, String> entry : variablePositionMap.entrySet()) {
			String value = entry.getValue();
			Integer position = entry.getKey();
			extract.put(value, parts[position]);
		}
		return extract;
	}

	@SuppressWarnings("unchecked")
	public List<String> getAccessGroups() {
		if (accessGroupsCL != null) {
			return (List<String>) DSLBuilder.callClosure(accessGroupsCL);
		} else {
			return accessGroups;
		}
	}

}
