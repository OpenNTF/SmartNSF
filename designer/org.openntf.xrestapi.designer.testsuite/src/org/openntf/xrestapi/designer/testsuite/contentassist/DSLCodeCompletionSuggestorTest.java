package org.openntf.xrestapi.designer.testsuite.contentassist;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openntf.xrest.designer.dsl.DSLRegistry;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.model.Router;
import org.openntf.xrest.xsp.model.Strategy;
import org.openntf.xrest.xsp.model.strategy.AllByKey;
import org.openntf.xrest.xsp.model.strategy.GetByUNID;

public class DSLCodeCompletionSuggestorTest {

	@Test
	public void testRegisterObjectWithAlias() {
		DSLRegistry reg = new DSLRegistry("router", Router.class);
		assertNotNull(reg);
		reg.addClosureObjecForMethod("GET", RouteProcessor.class);
		assertEquals(RouteProcessor.class, reg.getObjectForClosureInMethod("GET"));
	}

	@Test
	public void testRegisterSubobject() {
		DSLRegistry reg = new DSLRegistry("router", Router.class);
		assertNotNull(reg);
		reg.addClosureObjecForMethod("GET", RouteProcessor.class);
		reg.addClosureObjecForMethod(RouteProcessor.class, "STRATEGY", AllByKey.class);
		assertEquals(RouteProcessor.class, reg.getObjectForClosureInMethod("GET"));

	}

	@Test
	public void testRegistersConditionaSubObject() {
		DSLRegistry reg = new DSLRegistry("router", Router.class);
		assertNotNull(reg);
		reg.addClosureObjecForMethod("GET", RouteProcessor.class);
		reg.addClosureObjecForMethodWithCondition(RouteProcessor.class, "STRATEGY", Strategy.DOCUMENT_BY_UNID, GetByUNID.class);
		reg.addClosureObjecForMethodWithCondition(RouteProcessor.class, "STRATEGY", Strategy.DOCUMENT_FROM_VIEW_BY_KEY, Strategy.DOCUMENT_FROM_VIEW_BY_KEY.getModelClass());
		assertEquals(RouteProcessor.class, reg.getObjectForClosureInMethod("GET"));
		assertEquals(GetByUNID.class, reg.getObjectForClosureInMethodByCondition(RouteProcessor.class, "STRATEGY", Strategy.DOCUMENT_BY_UNID));
		assertEquals(Strategy.DOCUMENT_FROM_VIEW_BY_KEY.getModelClass(), reg.getObjectForClosureInMethodByCondition(RouteProcessor.class, "STRATEGY", Strategy.DOCUMENT_FROM_VIEW_BY_KEY));
	}
}
