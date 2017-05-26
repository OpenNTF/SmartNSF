package org.openntf.xrestapi.designer.testsuite.contentassist;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.ast.expr.VariableExpression;
import org.easymock.EasyMock;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.junit.Before;
import org.junit.Test;
import org.openntf.xrest.designer.XRestUIActivator;
import org.openntf.xrest.designer.codeassist.ASTAnalyser;
import org.openntf.xrest.designer.codeassist.CodeProposal;
import org.openntf.xrest.designer.codeassist.ProposalParameter;
import org.openntf.xrest.designer.codeassist.VEProposal;
import org.openntf.xrest.designer.dsl.DSLRegistry;
import org.openntf.xrest.designer.dsl.DSLRegistryFactory;
import org.openntf.xrest.xsp.model.Router;
import org.openntf.xrest.xsp.model.Strategy;

public class VariableContentAssistProposalTest extends AbstractGroovyParserTest {

	@Test
	public void testRouterVariable() throws IOException {
		String dsl = readFile("router.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 8, 7);
		assertTrue(analyser.parse());
		assertTrue(analyser.getNode() instanceof VariableExpression);
		VariableExpression ve = (VariableExpression) analyser.getNode();
		assertTrue("router".equals(ve.getName()));
		DSLRegistry dslRegistry = DSLRegistryFactory.buildRegistry();
		ImageRegistry imgRegistry = EasyMock.createNiceMock(ImageRegistry.class);
		expect(imgRegistry.get("bullet_green.png")).andReturn(null);
		replay(imgRegistry);
		ProposalParameter pp = new ProposalParameter();
		pp.add(ve);
		pp.add(analyser.getHierarchie());
		pp.add(dslRegistry);
		pp.add(imgRegistry);
		CodeProposal veproposal = new VEProposal(pp);
		List<ICompletionProposal> proposals = veproposal.suggestions(0);
		assertNotNull(proposals);
	}
	
	@Test
	public void testMapJson() throws IOException {
		String dsl = readFile("ast.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 17, 8);
		assertTrue(analyser.parse());
		assertTrue(analyser.getNode() instanceof VariableExpression);
		VariableExpression ve = (VariableExpression) analyser.getNode();
		assertTrue("mapJson".equals(ve.getName()));

	}
	@Test
	public void testStrategy() throws IOException {
		String dsl = readFile("ast.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 18, 9);
		assertTrue(analyser.parse());
		assertTrue(analyser.getNode() instanceof VariableExpression);
		VariableExpression ve = (VariableExpression) analyser.getNode();
		assertTrue("strategy".equals(ve.getName()));
		ImageRegistry imgRegistry = EasyMock.createNiceMock(ImageRegistry.class);
		expect(imgRegistry.get("bullet_green.png")).andReturn(null);
		replay(imgRegistry);
		DSLRegistry dslRegistry = DSLRegistryFactory.buildRegistry();
		ProposalParameter pp = new ProposalParameter();
		pp.add(ve);
		pp.add(analyser.getHierarchie());
		pp.add(dslRegistry);
		pp.add(imgRegistry);
		CodeProposal veproposal = new VEProposal(pp);
		List<ICompletionProposal> proposals = veproposal.suggestions(0);
		assertEquals(Strategy.values().length, proposals.size());
	}
}
