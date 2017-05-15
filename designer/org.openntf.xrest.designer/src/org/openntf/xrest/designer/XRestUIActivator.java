package org.openntf.xrest.designer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class XRestUIActivator extends AbstractUIPlugin {

	private static String[] icons = { "bullet_green.png", "link.png", "script_link.png" };
	// The plug-in ID
	public static final String PLUGIN_ID = "org.openntf.xrest.designer"; //$NON-NLS-1$

	// The shared instance
	private static XRestUIActivator plugin;

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
}
