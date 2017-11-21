package org.openntf.xrest.designer.dsl;

import java.util.Arrays;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.MapJsonType;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.model.Router;
import org.openntf.xrest.xsp.model.Strategy;
import org.openntf.xrest.xsp.model.strategy.ViewEntriesByCategory;

public class DSLRegistryFactory {

	public static DSLRegistry buildRegistry() {
		DSLRegistry dslRegistry = new DSLRegistry("router", Router.class);
		dslRegistry.addClosureObjecForMethod("GET", RouteProcessor.class);
		dslRegistry.addClosureObjecForMethod("PUT", RouteProcessor.class);
		dslRegistry.addClosureObjecForMethod("POST", RouteProcessor.class);
		dslRegistry.addClosureObjecForMethod("DELETE", RouteProcessor.class);
		dslRegistry.addClosureObjecForMethod(MethodContainer.buildContainerWithConditionAndParams(ViewEntriesByCategory.class, "calculateKey", "", Object.class, Arrays.asList(new Class<?>[]{Context.class,})));
		for (Strategy strat : Strategy.values()) {
			MethodContainer mc = MethodContainer.buildContainerWithCondition(RouteProcessor.class, "strategy", strat.name(), strat.getModelClass());
			dslRegistry.addClosureObjecForMethodWithCondition(mc);
		}
		for (EventType event : EventType.values()) {
			MapContainer mc = MapContainer.buildMakKeyWithClosure(RouteProcessor.class, "events", event.name(), Context.class, Object.class);
			dslRegistry.addMapKeyClosure(mc);
		}
		dslRegistry.addMapKeyClosure(MapContainer.buildMapKeyWithValue(RouteProcessor.class, "mapJson", "json", String.class));
		dslRegistry.addMapKeyClosure(MapContainer.buildMapKeyWithValue(RouteProcessor.class, "mapJson", "type", MapJsonType.class));
		dslRegistry.addMapKeyClosure(MapContainer.buildMapKeyWithValue(RouteProcessor.class, "mapJson", "isformula", Boolean.class));
		dslRegistry.addMapKeyClosure(MapContainer.buildMapKeyWithValue(RouteProcessor.class, "mapJson", "readonly", Boolean.class));
		dslRegistry.addMapKeyClosure(MapContainer.buildMapKeyWithValue(RouteProcessor.class, "mapJson", "writeonly", Boolean.class));
		dslRegistry.addMapKeyClosure(MapContainer.buildMapKeyWithValue(RouteProcessor.class, "mapJson", "formula", String.class));
		return dslRegistry;
	}
}
