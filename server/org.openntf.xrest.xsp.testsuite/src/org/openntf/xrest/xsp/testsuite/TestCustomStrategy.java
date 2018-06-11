package org.openntf.xrest.xsp.testsuite;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openntf.xrest.xsp.exec.CustomRestHandler;
import org.openntf.xrest.xsp.model.strategy.Custom;

public class TestCustomStrategy {

	@Test
	public void testInstanceOfAClassInheritsInterface() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Custom csStrategy = new Custom();
		csStrategy.javaClass("org.openntf.xrest.xsp.testsuite.mock.UsingTheInterfaceDirectMockRestHandler");
		assertNotNull(csStrategy.getCustomRestHandler());

	}

	@Test
	public void testInstanceOfanAbstractClassInheritsInterface() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Custom csStrategy = new Custom();
		csStrategy.javaClass("org.openntf.xrest.xsp.testsuite.mock.UsingViaAbstractClassMockRestHandler");
		assertNotNull(csStrategy.getCustomRestHandler());
	}

	@Test
	public void testNoInstanceCustomRestHandler() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Custom csStrategy = new Custom();
		csStrategy.javaClass("org.openntf.xrest.xsp.testsuite.mock.BlankClassFromArrayList");
		try {
			@SuppressWarnings("unused")
			CustomRestHandler handler = csStrategy.getCustomRestHandler();
			assertFalse("You should not land here!", true);
		} catch (ClassCastException ex) {
			assertTrue(true);

		} catch (Exception ex) {
			assertTrue("Something went wrong during cloning", false);
		}
	}
}
