package org.openntf.xrest.xsp.testsuite;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.model.Router;


public class TestRouterBasics {

	@Test()
	public void testLoadDSLFile() throws IOException {
		String dsl = readFile();
		System.out.println(dsl);
		Assert.assertNotNull(dsl);
	}
	
	@Test()
	public void testBuildRouterBasic() throws IOException {
		String dsl = readFile();
		Router router = DSLBuilder.buildRouterFromDSL(dsl);
		Assert.assertNotNull(router);
		
	}
	
	private String readFile() throws IOException {
		InputStream is = getClass().getResourceAsStream("router.groovy");
		return IOUtils.toString(is, "utf-8");
	}
}
