package org.openntf.xrest.xsp.testsuite;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.NSFHelper;
import org.openntf.xrest.xsp.model.EventException;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.model.Router;

import com.ibm.commons.util.io.json.JsonObject;

import groovy.lang.Closure;
import lotus.domino.Document;
import lotus.domino.NotesException;

public class TestEvents extends AbstractRouterBasics {

	@Override
	protected String getRouterDSLFileName() {
		return "router2.groovy";
	}
	
	@Test
	public void testEventValidate() {
		Router router = getRouter();
		Context context = createNiceMock(Context.class);
		JsonObject jso = createNiceMock(JsonObject.class);
		expect(context.getJsonPayload()).andReturn(jso);
		expect(jso.getJsonProperty("id")).andReturn("this is my id");
		replay(context, jso);

		RouteProcessor rp = router.find("POST", "customers/123");
		assertNotNull(rp);
		Closure<?> cl = rp.getEventClosure(EventType.VALIDATE);
		assertNotNull(cl);
		DSLBuilder.callClosure(cl, context);
		assertTrue(true);
		verify(context, jso);
	}

	@Test
	public void testEventValidateThrowException() throws IOException {
		Router router = getRouter();
		Context context = createNiceMock(Context.class);
		JsonObject jso = createNiceMock(JsonObject.class);
		expect(context.getJsonPayload()).andReturn(jso);
		expect(jso.getJsonProperty("id")).andReturn("");
		expect(context.throwException("ID should not be null or empty")).andThrow(new EventException("ID should not be null or empty"));
		replay(context, jso);

		RouteProcessor rp = router.find("POST", "customers/123");
		assertNotNull(rp);
		Closure<?> cl = rp.getEventClosure(EventType.VALIDATE);
		assertNotNull(cl);
		try {
			DSLBuilder.callClosure(cl, context);
			assertFalse("This block should not be reached..", true);
		} catch (Exception ex) {
			assertTrue(ex instanceof EventException);
			verify(context, jso);
		}
	}

	@Test
	public void testEventCallMakeChildInPreSave() throws NotesException {
		Map<String, String> rv = new HashMap<String, String>();
		rv.put("customerid", "123");
		Router router = getRouter();
		Context context = createNiceMock(Context.class);
		Document doc = createNiceMock(Document.class);
		expect(context.getRouterVariables()).andReturn(rv);
		NSFHelper nsfHelper = createNiceMock(NSFHelper.class);
		expect(context.getNSFHelper()).andReturn(nsfHelper);
		//expect(nsfHelper.makeDocumentAsChild("123", doc)).andReturn(true);
		replay(context, nsfHelper, doc);

		RouteProcessor rp = router.find("POST", "customers/123/phonecall/@new");
		assertNotNull(rp);
		Closure<?> cl = rp.getEventClosure(EventType.PRE_SAVE_DOCUMENT);
		assertNotNull(cl);
		DSLBuilder.callClosure(cl, context, doc);
		verify(context, nsfHelper, doc);

	}

	@Test
	public void testEventCallExecuteAgentInPostSave() throws NotesException {
		Router router = getRouter();
		Context context = createNiceMock(Context.class);
		Document doc = createNiceMock(Document.class);
		NSFHelper nsfHelper = createNiceMock(NSFHelper.class);
		expect(context.getNSFHelper()).andReturn(nsfHelper);
		//expect(nsfHelper.executeAgent("processHistory", doc)).andReturn(true);
		replay(context, nsfHelper, doc);

		RouteProcessor rp = router.find("POST", "customers/123/phonecall/@new");
		assertNotNull(rp);
		Closure<?> cl = rp.getEventClosure(EventType.POST_SAVE_DOCUMENT);
		assertNotNull(cl);
		DSLBuilder.callClosure(cl, context, doc);
		verify(context, nsfHelper, doc);

	}



}
