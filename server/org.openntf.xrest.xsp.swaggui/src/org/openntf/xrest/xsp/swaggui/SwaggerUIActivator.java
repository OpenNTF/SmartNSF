package org.openntf.xrest.xsp.swaggui;

import org.eclipse.core.runtime.Plugin;

public class SwaggerUIActivator extends Plugin {

	private static SwaggerUIActivator m_Instance;

	public SwaggerUIActivator() {
		m_Instance = this;
	}

	public static Plugin getInstance() {
		return m_Instance;
	}


}
