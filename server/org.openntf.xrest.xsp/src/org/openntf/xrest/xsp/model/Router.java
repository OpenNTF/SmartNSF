package org.openntf.xrest.xsp.model;

import java.util.ArrayList;
import java.util.List;

import org.openntf.xrest.xsp.dsl.DSLBuilder;

import groovy.lang.Closure;

public class Router {

	private final List<RouteProcessor> routesGET = new ArrayList<RouteProcessor>();
	private final List<RouteProcessor> routesPUT = new ArrayList<RouteProcessor>();
	private final List<RouteProcessor> routesPOST = new ArrayList<RouteProcessor>();
	private final List<RouteProcessor> routesDELETE = new ArrayList<RouteProcessor>();

	
	public void GET(String route, Closure<Void> cl) {
		RouteProcessor rp = new RouteProcessor(route);
		routesGET.add(rp);
		DSLBuilder.applyClosureToObject(cl, rp);
	}
	
	public void PUT(String route, Closure<Void> cl) {
		RouteProcessor rp = new RouteProcessor(route);
		routesPUT.add(rp);
		DSLBuilder.applyClosureToObject(cl, rp);
	}

	public void POST(String route, Closure<Void> cl) {
		RouteProcessor rp = new RouteProcessor(route);
		routesPOST.add(rp);
		DSLBuilder.applyClosureToObject(cl, rp);
	}
	public void DELETE(String route, Closure<Void> cl) {
		RouteProcessor rp = new RouteProcessor(route);
		routesDELETE.add(rp);
		DSLBuilder.applyClosureToObject(cl, rp);
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


}
