package org.openntf.xrest.xsp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openntf.xrest.xsp.dsl.DSLBuilder;

import groovy.lang.Closure;

public class Router {

	private final List<RouteProcessor> routesGET = new ArrayList<RouteProcessor>();
	private final List<RouteProcessor> routesPUT = new ArrayList<RouteProcessor>();
	private final List<RouteProcessor> routesPOST = new ArrayList<RouteProcessor>();
	private final List<RouteProcessor> routesDELETE = new ArrayList<RouteProcessor>();
	private final Map<String, List<RouteProcessor>> allroutes = new HashMap<String, List<RouteProcessor>>();
	private String versionValue = "1.0.0";
	private String descriptionValue = "";
	private boolean traceValue = false;

	public Router() {
		allroutes.put("GET", routesGET);
		allroutes.put("PUT", routesPUT);
		allroutes.put("POST", routesPOST);
		allroutes.put("DELETE", routesDELETE);
	}

	public void GET(String route, Closure<Void> cl) {
		RouteProcessor rp = new RouteProcessor(route, "GET");
		routesGET.add(rp);
		DSLBuilder.applyClosureToObject(cl, rp);
	}

	public void PUT(String route, Closure<Void> cl) {
		RouteProcessor rp = new RouteProcessor(route, "PUT");
		routesPUT.add(rp);
		DSLBuilder.applyClosureToObject(cl, rp);
	}

	public void POST(String route, Closure<Void> cl) {
		RouteProcessor rp = new RouteProcessor(route, "POST");
		routesPOST.add(rp);
		DSLBuilder.applyClosureToObject(cl, rp);
	}

	public void DELETE(String route, Closure<Void> cl) {
		RouteProcessor rp = new RouteProcessor(route, "DELETE");
		routesDELETE.add(rp);
		DSLBuilder.applyClosureToObject(cl, rp);
	}

	public void version(String version) {
		this.versionValue = version;
	}

	public void trace(boolean trace) {
		this.traceValue = trace;
	}

	public void description(String description) {
		this.descriptionValue = description;
	}

	public List<RouteProcessor> getRoutesGET() {
		return routesGET;
	}

	public List<RouteProcessor> getRoutesPUT() {
		return routesPUT;
	}

	public List<RouteProcessor> getRoutesPOST() {
		return routesPOST;
	}

	public List<RouteProcessor> getRoutesDELETE() {
		return routesDELETE;
	}

	public RouteProcessor find(String method, String path) {
		String[] pathParts = path.split("/");
		List<RouteProcessor> routeProcessors = allroutes.get(method.toUpperCase());
		int hitCount = 0;
		RouteProcessor result = null;
		for (RouteProcessor rp : routeProcessors) {
			int count = rp.matchRoute(pathParts);
			if (count > hitCount) {
				result = rp;
				hitCount = count;
			}
		}
		return result;
	}

	public Map<String, List<RouteProcessor>> routesMapping() {
		Map<String, List<RouteProcessor>> mapping = new TreeMap<String, List<RouteProcessor>>();
		applyRouterProcessorToMapping(mapping, getRoutesGET());
		applyRouterProcessorToMapping(mapping, getRoutesPOST());
		applyRouterProcessorToMapping(mapping, getRoutesPUT());
		applyRouterProcessorToMapping(mapping, getRoutesDELETE());
		return mapping;
	}

	private void applyRouterProcessorToMapping(Map<String, List<RouteProcessor>> mapping, List<RouteProcessor> processors) {
		for (RouteProcessor rp : processors) {
			String route = rp.getRoute();
			if (mapping.containsKey(route)) {
				List<RouteProcessor> routes = mapping.get(route);
				routes.add(rp);
			} else {
				List<RouteProcessor> routes = new LinkedList<RouteProcessor>();
				routes.add(rp);
				mapping.put(route, routes);
			}
		}
	}

	public String getVersionValue() {
		return versionValue;
	}

	public String getDescriptionValue() {
		return descriptionValue;
	}

	public boolean isTrace() {
		return traceValue;
	}
	
	
}
