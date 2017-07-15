package org.openntf.xrestapi.designer.testsuite.contentassist;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.junit.Ignore;
import org.junit.Test;
import org.openntf.xrest.designer.codeassist.ASTAnalyser;

public class WorkbenchASTTest extends AbstractGroovyParserTest {

	@Test
	public void testFindEventNotation() throws IOException {
		String dsl = readFile("router.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 58,6);
		assertTrue(analyser.parse());
		ASTNode node = analyser.getNode();
		List<ASTNode> hierarchie = analyser.getHierarchie();
		assertNotNull(hierarchie);
		assertNotNull(node);
		System.out.println(node.getText() +" // "+ node.getClass());
		assertTrue(node instanceof ConstantExpression);
		assertTrue(hierarchie.size() == 8);
	}

	@Test
	public void testFindEventMethodStart() throws IOException {
		String dsl = readFile("router.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 58,9);
		assertTrue(analyser.parse());
		ASTNode node = analyser.getNode();
		List<ASTNode> hierarchie = analyser.getHierarchie();
		assertNotNull(hierarchie);
		assertNotNull(node);
		System.out.println("REST: "+node.getText() +" // "+ node.getClass());
		assertTrue(node instanceof MethodCallExpression);
		assertTrue(hierarchie.size() == 7);
	}

	@Test
	public void testFindContextInEvent() throws IOException {
		String dsl = readFile("router.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 59,9);
		assertTrue(analyser.parse());
		ASTNode node = analyser.getNode();
		List<ASTNode> hierarchie = analyser.getHierarchie();
		assertNotNull(hierarchie);
		assertNotNull(node);
		System.out.println("TFC: "+node.getText() +"//"+node.getClass());
		assertTrue(node instanceof Parameter);
		assertTrue(hierarchie.size() == 12);
		assertTrue("context".equals(((Parameter)node).getName()));
	}


}
