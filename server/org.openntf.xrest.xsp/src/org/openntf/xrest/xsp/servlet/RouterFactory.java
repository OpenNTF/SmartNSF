package org.openntf.xrest.xsp.servlet;

import java.io.InputStream;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.model.Router;

import com.ibm.commons.util.io.StreamUtil;
import com.ibm.designer.runtime.domino.adapter.ComponentModule;

public class RouterFactory {

	private final ComponentModule module;
	private Router router;
	private Throwable error;

	public RouterFactory(ComponentModule module) {
		this.module = module;
		this.router = buildRouter();
	}

	public void refresh() {
		error = null;
		this.router = buildRouter();
	}

	private Router buildRouter() {
		try {
			InputStream is = module.getResourceAsStream("/WEB-INF/routes.groovy");
			if (is != null) {
				String dsl = StreamUtil.readString(is);
				return DSLBuilder.buildRouterFromDSL(dsl, Thread.currentThread().getContextClassLoader());
			}
		} catch (Exception e) {
			error = e;
		}
		return null;
	}

	public Router getRouter() {
		return router;
	}

	public boolean hasError() {
		return error != null;
	}

	public Throwable getError() {
		return error;
	}
}
