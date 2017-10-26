package org.openntf.xrestapi.designer.testsuite.contentassist;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.junit.Test;
import org.openntf.xrest.designer.codeassist.ASTAnalyser;

import groovy.ui.SystemOutputInterceptor;

public class TestASTAnalyzer extends AbstractGroovyParserTest {

	@Test
	public void testFindAst() throws IOException {
		String dsl = readFile("router.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 8,7, null);
		assertTrue(analyser.parse());
		ASTNode node = analyser.getNode();
		List<ASTNode> hierarchie = analyser.getHierarchie();
		assertNotNull(node);
		assertNotNull(hierarchie);
	}

	@Test
	public void testCheckWordSegment() throws IOException {
		String dsl = readFile("router-middle.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 8,5, null);
		assertTrue(analyser.parse());
		ASTNode node = analyser.getNode();
		List<ASTNode> hierarchie = analyser.getHierarchie();
		assertNotNull(node);
		assertNotNull(hierarchie);
		assertTrue(node instanceof VariableExpression);
		VariableExpression exp = (VariableExpression) node;
		
	}
	@Test
	public void testCheckEndOfEntry() throws IOException {
		String dsl = readFile("routerEnd.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 20,8, null);
		assertFalse(analyser.parse());		
	}
	@Test
	public void testCheckRouterFail() throws IOException {
		String dsl = readFile("routerFail.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 20,7, null);
		assertFalse(analyser.parse());
		assertNotNull(analyser.getException());
	}
	
	@Test
	public void testCaclGroupsVariableInside() throws IOException {
		String dsl = readFile("router.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 47,28, null);
		assertTrue(analyser.parse());

		ASTNode node = analyser.getNode();
		List<ASTNode> hierarchie = analyser.getHierarchie();
		assertNotNull(node);
		assertNotNull(hierarchie);
		assertTrue(node instanceof VariableExpression);
	}

	@Test
	public void testFindClosureAsTop() throws IOException {
		String dsl = readFile("router.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 76,1, null);
		assertTrue(analyser.parse());
		ASTNode node = analyser.getNode();
		List<ASTNode> hierarchie = analyser.getHierarchie();
		assertNotNull(hierarchie);
		assertNotNull(node);
		assertTrue(node instanceof ClosureExpression);
		assertTrue(hierarchie.size() == 4);
	}

	@Test
	public void testFindEventNotation() throws IOException {
		String dsl = readFile("router.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 58,6, null);
		assertTrue(analyser.parse());
		ASTNode node = analyser.getNode();
		List<ASTNode> hierarchie = analyser.getHierarchie();
		assertNotNull(hierarchie);
		assertNotNull(node);
		assertTrue(node instanceof ConstantExpression);
		assertTrue(hierarchie.size() == 8);
	}

	@Test
	public void testFindEventMethodStart() throws IOException {
		String dsl = readFile("router.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 58,9, null);
		assertTrue(analyser.parse());
		ASTNode node = analyser.getNode();
		List<ASTNode> hierarchie = analyser.getHierarchie();
		assertNotNull(hierarchie);
		assertNotNull(node);
		assertTrue(node instanceof MethodCallExpression);
		assertTrue(hierarchie.size() == 7);
	}

	@Test
	public void testFindEventASTFile() throws IOException {
		String dsl = readFile("ast.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 16,9, null);
		assertTrue(analyser.parse());
		ASTNode node = analyser.getNode();
		List<ASTNode> hierarchie = analyser.getHierarchie();
		assertNotNull(hierarchie);
		assertNotNull(node);
		assertTrue(node instanceof MethodCallExpression);
		assertTrue(hierarchie.size() == 7);
	}
	
	@Test
	public void testFindDefInEvent() throws IOException {
		String dsl = readFile("router.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 59,30, null);
		assertTrue(analyser.parse());
		ASTNode node = analyser.getNode();
		List<ASTNode> hierarchie = analyser.getHierarchie();
		assertNotNull(hierarchie);
		assertNotNull(node);
		assertTrue(node instanceof VariableExpression);
		assertTrue(hierarchie.size() == 15);
	}

	@Test
	public void testFindContextInEvent() throws IOException {
		String dsl = readFile("router.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 59,9, null);
		assertTrue(analyser.parse());
		ASTNode node = analyser.getNode();
		List<ASTNode> hierarchie = analyser.getHierarchie();
		assertNotNull(hierarchie);
		assertNotNull(node);
		assertTrue(node instanceof Parameter);
		assertTrue(hierarchie.size() == 12);
		assertTrue("context".equals(((Parameter)node).getName()));
	}

	@Test
	public void testFindStartOfMethodInEvent() throws IOException {
		String dsl = readFile("router.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 59,50, null);
		assertTrue(analyser.parse());
		ASTNode node = analyser.getNode();
		List<ASTNode> hierarchie = analyser.getHierarchie();
		assertNotNull(hierarchie);
		assertNotNull(node);
		assertTrue(node instanceof ConstantExpression);
		assertEquals(16, hierarchie.size());
		assertEquals("getHttpResp",(((ConstantExpression)node).getValue()));
		
	}
}
