package org.openntf.xrest.xsp.servlet;

import java.io.InputStream;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.model.Router;

import com.ibm.commons.util.io.StreamUtil;
import com.ibm.designer.runtime.domino.adapter.ComponentModule;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;

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

	public Histogram buildHistogram() {
		if (router == null || error != null) {
			return null;
		}
		CollectorRegistry.defaultRegistry.clear();
		Histogram.Builder builder =  Histogram.build()
				.labelNames("path", "method");
		builder.help("SmartNSF Execution Data");
		builder.name("http_xrest_request_duration_seconds");
		builder.buckets(new double[]{0.005,0.01,0.025,0.05,0.075,0.1,0.25,0.5,0.75,1,2.5,5,7.5,10});
		return builder.register();
	}
}
