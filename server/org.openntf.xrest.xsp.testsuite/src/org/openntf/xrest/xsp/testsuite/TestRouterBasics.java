package org.openntf.xrest.xsp.testsuite;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;


public class TestRouterBasics {

	@Test()
	public void testLoadDSLFile() {
		Assert.assertTrue(true);
	}
	
	
	private String readFile() {
		InputStream is = getClass().getResourceAsStream("router.groovy");
		
	}
}
