package org.openntf.xrest.designer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.openntf.xrest.designer.dsl.DSLRegistry;
import org.openntf.xrest.designer.dsl.MapContainer;
import org.openntf.xrest.designer.dsl.MethodContainer;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.MapJsonType;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.model.Router;
import org.openntf.xrest.xsp.model.Strategy;
import org.osgi.framework.BundleContext;

import groovy.transform.stc.MapEntryOrKeyValue;

/**
 * The activator class controls the plug-in life cycle
 */
public class XRestUIActivator extends AbstractUIPlugin {

	private static String[] icons = { "bullet_green.png", "link.png", "script_link.png" };
	// The plug-in ID
	public static final String PLUGIN_ID = "org.openntf.xrest.designer"; //$NON-NLS-1$

	// The shared instance
	private static XRestUIActivator plugin;

	private final DSLRegistry dslRegistry = new DSLRegistry("router", Router.class);

	/**
	 * The constructor
	 */
	public XRestUIActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		initRegistry();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static XRestUIActivator getDefault() {
		return plugin;
	}

	public Image getImageByKey(String key) {
		return plugin.getImageRegistry().get(key);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		super.initializeImageRegistry(registry);
		for (String icon : icons) {
			ImageDescriptor desc = imageDescriptorFromPlugin(PLUGIN_ID, "icons/" + icon);
			registry.put(icon, desc);
		}
	}

	public DSLRegistry getDSLRegistry() {
		return dslRegistry;
	}

	private void initRegistry() {
		dslRegistry.addClosureObjecForMethod("GET", RouteProcessor.class);
		dslRegistry.addClosureObjecForMethod("PUT", RouteProcessor.class);
		dslRegistry.addClosureObjecForMethod("POST", RouteProcessor.class);
		dslRegistry.addClosureObjecForMethod("DELETE", RouteProcessor.class);
		for (Strategy strat : Strategy.values()) {
			MethodContainer mc = MethodContainer.buildContainerWithCondition(RouteProcessor.class, "strategy", strat.name(), strat.getModelClass());
			dslRegistry.addClosureObjecForMethod(mc);
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
	}
}
