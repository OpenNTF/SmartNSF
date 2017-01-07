package org.openntf.xrest.xsp.testsuite;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.RouteProcessorExecutor;
import org.openntf.xrest.xsp.exec.RouteProcessorExecutorFactory;
import org.openntf.xrest.xsp.exec.impl.DELETERouteProcessorExecutor;
import org.openntf.xrest.xsp.exec.impl.GETRouteProcessorExecutor;
import org.openntf.xrest.xsp.exec.impl.POSTRouteProcessorExecutor;
import org.openntf.xrest.xsp.model.RouteProcessor;

public class TestExecutor {

	@Test
	public void testGetGETExecutor() {
		RouteProcessorExecutor executor = RouteProcessorExecutorFactory.getExecutor("GET", "customers", getContext(), getRouterProcessor());
		assertTrue(executor instanceof GETRouteProcessorExecutor);
	}

	@Test
	public void testGetPOSTExecutor() {
		RouteProcessorExecutor executor = RouteProcessorExecutorFactory.getExecutor("POST", "customers/121", getContext(), getRouterProcessor());
		assertTrue(executor instanceof POSTRouteProcessorExecutor);
	}

	@Test
	public void testGetPUTExecutor() {
		RouteProcessorExecutor executor = RouteProcessorExecutorFactory.getExecutor("PUT", "customers/121", getContext(), getRouterProcessor());
		assertTrue(executor instanceof POSTRouteProcessorExecutor);
	}

	@Test
	public void testGetDELETEExecutor() {
		RouteProcessorExecutor executor = RouteProcessorExecutorFactory.getExecutor("DELETE", "customers/121", getContext(), getRouterProcessor());
		assertTrue(executor instanceof DELETERouteProcessorExecutor);
	}

	
	private RouteProcessor getRouterProcessor() {
		// TODO Auto-generated method stub
		return null;
	}

	private Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}
}
