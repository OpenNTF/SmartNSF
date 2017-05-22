package org.openntf.xrestapi.designer.testsuite.contentassist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openntf.xrest.designer.dsl.DSLRegistry;
import org.openntf.xrest.designer.dsl.MapContainer;
import org.openntf.xrest.designer.dsl.MethodContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.model.Router;
import org.openntf.xrest.xsp.model.Strategy;
import org.openntf.xrest.xsp.model.strategy.AllByKey;
import org.openntf.xrest.xsp.model.strategy.GetByUNID;

import groovy.lang.Closure;
import lotus.domino.Document;

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
		reg.addClosureObjecForMethod(MethodContainer.buildContainer(RouteProcessor.class, "STRATEGY", AllByKey.class));
		assertEquals(RouteProcessor.class, reg.getObjectForClosureInMethod("GET"));

	}

	@Test
	public void testRegistersConditionaSubObject() {
		DSLRegistry reg = new DSLRegistry("router", Router.class);
		assertNotNull(reg);
		reg.addClosureObjecForMethod("GET", RouteProcessor.class);
		reg.addClosureObjecForMethodWithCondition(MethodContainer.buildContainerWithCondition(RouteProcessor.class, "STRATEGY", Strategy.DOCUMENT_BY_UNID.name(), GetByUNID.class));
		reg.addClosureObjecForMethodWithCondition(
				MethodContainer.buildContainerWithCondition(RouteProcessor.class, "STRATEGY", Strategy.DOCUMENT_FROM_VIEW_BY_KEY.name(), Strategy.DOCUMENT_FROM_VIEW_BY_KEY.getModelClass()));
		assertEquals(RouteProcessor.class, reg.getObjectForClosureInMethod("GET"));
		assertEquals(GetByUNID.class, reg.getObjectForClosureInMethodByCondition(RouteProcessor.class, "STRATEGY", Strategy.DOCUMENT_BY_UNID));
		assertEquals(Strategy.DOCUMENT_FROM_VIEW_BY_KEY.getModelClass(), reg.getObjectForClosureInMethodByCondition(RouteProcessor.class, "STRATEGY", Strategy.DOCUMENT_FROM_VIEW_BY_KEY));
	}

	@Test
	public void testCheckIfMethodIsConidion() {
		DSLRegistry reg = new DSLRegistry("router", Router.class);
		assertNotNull(reg);
		reg.addClosureObjecForMethod("GET", RouteProcessor.class);
		reg.addClosureObjecForMethodWithCondition(MethodContainer.buildContainerWithCondition(RouteProcessor.class, "STRATEGY", Strategy.DOCUMENT_BY_UNID.name(), GetByUNID.class));
		reg.addClosureObjecForMethodWithCondition(
				MethodContainer.buildContainerWithCondition(RouteProcessor.class, "STRATEGY", Strategy.DOCUMENT_FROM_VIEW_BY_KEY.name(), Strategy.DOCUMENT_FROM_VIEW_BY_KEY.getModelClass()));
		assertFalse(reg.isMethodConditioned("GET"));
		assertTrue(reg.isMethodConditioned(RouteProcessor.class, "STRATEGY"));
	}

	@Test
	public void testRegisterMapKeyForMethod() {
		DSLRegistry reg = new DSLRegistry("router", Router.class);
		MapContainer mc = MapContainer.buildMakKeyWithClosure(RouteProcessor.class, "events", "VALIDATE", org.openntf.xrest.xsp.exec.Context.class, Document.class);
		MapContainer mc2 = MapContainer.buildMakKeyWithClosure(RouteProcessor.class, "events", "PRE_SUBMIT", org.openntf.xrest.xsp.exec.Context.class, Document.class);
		MapContainer mc3 = MapContainer.buildMakKeyWithClosure(RouteProcessor.class, "events", "POST_LOAD_DOCUMENT", org.openntf.xrest.xsp.exec.Context.class, Document.class);
		reg.addMapKeyClosure(mc);
		reg.addMapKeyClosure(mc2);
		reg.addMapKeyClosure(mc3);
		assertEquals(3, reg.getMapKeys(RouteProcessor.class, "events").size());
		assertTrue(reg.getMapKeys(RouteProcessor.class,"events").contains("PRE_SUBMIT"));
	}
}
