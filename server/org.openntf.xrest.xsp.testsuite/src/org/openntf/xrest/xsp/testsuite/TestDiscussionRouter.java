package org.openntf.xrest.xsp.testsuite;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.Router;

public class TestDiscussionRouter extends AbstractRouterBasics {

	@Override
	protected String getRouterDSLFileName() {
		return "discussion_router.groovy";
	}

	@Test
	public void testStrategyClosureGET() throws ExecutorException {
		Router router = getRouter();
		assertNotNull(router);
	}
}
