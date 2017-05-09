package org.openntf.xrest.xsp.testsuite;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.model.Router;

import groovy.lang.Closure;

public class TestRouterBasics {

	@Test()
	public void testLoadDSLFile() throws IOException {
		String dsl = readFile();
		Assert.assertNotNull(dsl);
	}

	@Test()
	public void testBuildRouterBasic() throws IOException {
		String dsl = readFile();
		Router router = DSLBuilder.buildRouterFromDSL(dsl, getClass().getClassLoader());
		Assert.assertNotNull(router);
		Assert.assertEquals(3, router.getRoutesGET().size());
		Assert.assertEquals(1, router.getRoutesPUT().size());
		Assert.assertEquals(2, router.getRoutesPOST().size());
		Assert.assertEquals(1, router.getRoutesDELETE().size());
		Assert.assertEquals("customers", router.getRoutesGET().get(2).getRoute());
		Assert.assertEquals("customers/{id}", router.getRoutesPUT().get(0).getRoute());
		Assert.assertEquals("comment/{id}", router.getRoutesPOST().get(1).getRoute());
		Assert.assertEquals("quote/{id}", router.getRoutesDELETE().get(0).getRoute());
	}

	@Test
	public void testRouteProcessorVariables() {
		RouteProcessor rp = new RouteProcessor("customer/{id}", "GET");
		Assert.assertEquals(1, rp.getVariables().size());
		Assert.assertEquals("id",rp.getVariables().get(0));
		
		RouteProcessor rp2 = new RouteProcessor("customer/{id}/comment/{commentid}","GET");
		Assert.assertEquals(2, rp2.getVariables().size());
		Assert.assertEquals("commentid",rp2.getVariables().get(1));

	}

	@Test
	public void testRouteProcessorExtractVariablesValues() {
		RouteProcessor rp = new RouteProcessor("customer/{id}","GET");
		Map<String,String> extrValues = rp.extractValuesFromPath("customer/99182");
		Assert.assertEquals("99182", extrValues.get("id"));
		
		RouteProcessor rp2 = new RouteProcessor("customer/{id}/comment/{commentid}","GET");
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
		RouteProcessor rp2 = router.getRoutesGET().get(2);
		Assert.assertEquals(4,rp2.getAccessGroups().size());
	}

	@Test
	public void testRouterFindRoute() throws IOException {
		String dsl = readFile();
		Router router = DSLBuilder.buildRouterFromDSL(dsl, getClass().getClassLoader());
		RouteProcessor rp = router.find("GET","customers");
		Assert.assertNotNull(rp);
		Assert.assertEquals("customers", rp.getRoute());
		RouteProcessor rp2 = router.find("GET","customers/8109271");
		Assert.assertNotNull(rp2);
		Assert.assertEquals("customers/{id}", rp2.getRoute());
		RouteProcessor rp3 = router.find("GET","customers/8109271/idh");
		Assert.assertNull(rp3);
	}
	
	@Test
	public void testEvents() throws IOException {
		String dsl = readFile();
		Router router = DSLBuilder.buildRouterFromDSL(dsl, getClass().getClassLoader());
		RouteProcessor rp = router.find("GET","customers/3322");
		Closure<?> cl = rp.getEventClosure(EventType.VALIDATE);
		Assert.assertNotNull(cl);
		
		RouteProcessor rpPut = router.find("PUT","customers/3322");
		Closure<?> clPOSTSAVE = rpPut.getEventClosure(EventType.POST_SAVE_DOCUMENT);
		Closure<?> clPRESAVE = rpPut.getEventClosure(EventType.PRE_SAVE_DOCUMENT);
		Assert.assertNotNull(clPOSTSAVE);
		Assert.assertNotNull(clPRESAVE);

	}
	
	private String readFile() throws IOException {
		InputStream is = getClass().getResourceAsStream("router.groovy");
		return IOUtils.toString(is, "utf-8");
	}
	
	
}
