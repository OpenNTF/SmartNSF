package org.openntf.xrest.xsp.testsuite;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.model.RouteProcessor;
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
		Router router = DSLBuilder.buildRouterFromDSL(dsl, getClass().getClassLoader());
		Assert.assertNotNull(router);
		Assert.assertEquals(2, router.getRoutesGET().size());
		Assert.assertEquals(1, router.getRoutesPUT().size());
		Assert.assertEquals(1, router.getRoutesPOST().size());
		Assert.assertEquals(1, router.getRoutesDELETE().size());
		Assert.assertEquals("customers", router.getRoutesGET().get(1).getRoute());
		Assert.assertEquals("customers/{id}", router.getRoutesPUT().get(0).getRoute());
		Assert.assertEquals("comment/{id}", router.getRoutesPOST().get(0).getRoute());
		Assert.assertEquals("quote/{id}", router.getRoutesDELETE().get(0).getRoute());
	}

	@Test
	public void testRouteProcessorVariables() {
		RouteProcessor rp = new RouteProcessor("customer/{id}");
		Assert.assertEquals(1, rp.getVariables().size());
		Assert.assertEquals("id",rp.getVariables().get(0));
		
		RouteProcessor rp2 = new RouteProcessor("customer/{id}/comment/{commentid}");
		Assert.assertEquals(2, rp2.getVariables().size());
		Assert.assertEquals("commentid",rp2.getVariables().get(1));

	}

	@Test
	public void testRouteProcessorExtractVariablesValues() {
		RouteProcessor rp = new RouteProcessor("customer/{id}");
		Map<String,String> extrValues = rp.extractValuesFromPath("customer/99182");
		Assert.assertEquals("99182", extrValues.get("id"));
		
		RouteProcessor rp2 = new RouteProcessor("customer/{id}/comment/{commentid}");
		Map<String,String> extrValue2 = rp2.extractValuesFromPath("customer/9182/comment/99112");
		Assert.assertEquals("9182", extrValue2.get("id"));
		Assert.assertEquals("99112", extrValue2.get("commentid"));

	}
	
	@Test
	public void testRouterAccessDefinition() throws IOException {
		String dsl = readFile();
		Router router = DSLBuilder.buildRouterFromDSL(dsl, getClass().getClassLoader());
		RouteProcessor rp = router.getRoutesGET().get(0);
		Assert.assertEquals(2, rp.getAccessGroups().size());
		RouteProcessor rp2 = router.getRoutesGET().get(1);
		Assert.assertEquals(4,rp2.getAccessGroups().size());
	}

	private String readFile() throws IOException {
		InputStream is = getClass().getResourceAsStream("router.groovy");
		return IOUtils.toString(is, "utf-8");
	}
	
	
}
