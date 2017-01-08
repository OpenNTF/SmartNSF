package org.openntf.xrest.xsp.testsuite;

import java.io.IOException;
import java.io.InputStream;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.EventException;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.model.Router;

import com.ibm.commons.util.io.json.JsonObject;

import groovy.lang.Closure;


public class TestEvents {

	@Test
	public void testEventValidate() throws IOException {
		Router router = buildRouter();
		Context context = createNiceMock(Context.class);
		JsonObject jso = createNiceMock(JsonObject.class);
		expect(context.getJsonPayload()).andReturn(jso);
		expect(jso.getJsonProperty("id")).andReturn("this is my id");
		replay(context, jso);
		
		RouteProcessor rp = router.find("POST","customers/123");
		assertNotNull(rp);
		Closure<?> cl = rp.getEventClosure(EventType.VALIDATE);
		assertNotNull(cl);
		DSLBuilder.callClosure(cl, context);
		assertTrue(true);
		verify(context, jso);
	}

	@Test
	public void testEventValidateThrowException() throws IOException {
		Router router = buildRouter();
		Context context = createNiceMock(Context.class);
		JsonObject jso = createNiceMock(JsonObject.class);
		expect(context.getJsonPayload()).andReturn(jso);
		expect(jso.getJsonProperty("id")).andReturn("");
		expect(context.throwException("ID should not be null or empty")).andThrow(new EventException("ID should not be null or empty"));
		replay(context, jso);
		
		RouteProcessor rp = router.find("POST","customers/123");
		assertNotNull(rp);
		Closure<?> cl = rp.getEventClosure(EventType.VALIDATE);
		assertNotNull(cl);
		try {
			DSLBuilder.callClosure(cl, context);
			assertFalse("This block should not be reached..",true);
		} catch(Exception ex) {
			ex.printStackTrace();
			assertTrue (ex instanceof EventException);
			verify(context, jso);
		}
	}

	
	private Router buildRouter() throws IOException {
		String dsl = readFile();
		Router router = DSLBuilder.buildRouterFromDSL(dsl, getClass().getClassLoader());
		Assert.assertNotNull(router);
		return router;
	}

	private String readFile() throws IOException {
		InputStream is = getClass().getResourceAsStream("router2.groovy");
		return IOUtils.toString(is, "utf-8");
	}

	
}
