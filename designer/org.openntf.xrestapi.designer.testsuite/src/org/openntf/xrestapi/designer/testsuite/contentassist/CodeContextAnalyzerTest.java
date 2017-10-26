package org.openntf.xrestapi.designer.testsuite.contentassist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.junit.Test;
import org.openntf.xrest.designer.codeassist.ASTAnalyser;
import org.openntf.xrest.designer.codeassist.CodeContext;
import org.openntf.xrest.designer.codeassist.CodeContextAnalyzer;
import org.openntf.xrest.designer.dsl.DSLRegistry;
import org.openntf.xrest.designer.dsl.DSLRegistryFactory;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.NSFHelper;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.model.strategy.GetByKey;

public class CodeContextAnalyzerTest extends AbstractGroovyParserTest {

	@Test
	public void testBuildCodeContextForRouter() throws IOException {
		String dsl = readFile("router.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 8, 7, null);
		assertTrue(analyser.parse());
		DSLRegistry dslRegistry = DSLRegistryFactory.buildRegistry();
		CodeContextAnalyzer cca = new CodeContextAnalyzer(analyser, dslRegistry);
		CodeContext codeContext = cca.build();
		assertNotNull(codeContext);
		Map<String, Class<?>> variables = codeContext.getDeclaredVariables();
		assertNotNull(variables);
		assertTrue(variables.containsKey("router"));
		assertTrue(variables.containsKey("blubber"));
	}

	@Test
	public void testBuildCodeContextForMapContext() throws IOException {
		String dsl = readFile("ast.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 17, 8, null);
		assertTrue(analyser.parse());
		assertTrue(analyser.getNode() instanceof VariableExpression);
		DSLRegistry dslRegistry = DSLRegistryFactory.buildRegistry();
		CodeContextAnalyzer cca = new CodeContextAnalyzer(analyser, dslRegistry);
		CodeContext codeContext = cca.build();
		assertNotNull(codeContext);
		Map<String, Class<?>> variables = codeContext.getDeclaredVariables();
		assertNotNull(variables);
		assertTrue(variables.containsKey("router"));
		assertTrue(variables.containsKey("blubber"));
		assertEquals(RouteProcessor.class, codeContext.currentClassContext());

	}

	@Test
	public void testBuildCodeContextForContextInEvent() throws IOException {
		String dsl = readFile("ast.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 27, 9, null);
		assertTrue(analyser.parse());
		assertTrue(analyser.getNode() instanceof VariableExpression);
		VariableExpression ve = (VariableExpression) analyser.getNode();
		assertEquals("context", ve.getName());
		DSLRegistry dslRegistry = DSLRegistryFactory.buildRegistry();
		CodeContextAnalyzer cca = new CodeContextAnalyzer(analyser, dslRegistry);
		CodeContext codeContext = cca.build();
		assertNotNull(codeContext);
		Map<String, Class<?>> variables = codeContext.getDeclaredVariables();
		assertNotNull(variables);
		assertTrue(variables.containsKey("router"));
		assertTrue(variables.containsKey("blubber"));
		assertEquals(RouteProcessor.class, codeContext.currentClassContext());
		assertTrue(variables.containsKey("context"));
		assertEquals(variables.get("context"), Context.class);
	}
	@Test
	public void testBuildCodeContextForHelperInEvent() throws IOException {
		String dsl = readFile("ast.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 26, 8, null);
		assertTrue(analyser.parse());
		assertTrue(analyser.getNode() instanceof VariableExpression);
		VariableExpression ve = (VariableExpression) analyser.getNode();
		assertEquals("helper", ve.getName());
		DSLRegistry dslRegistry = DSLRegistryFactory.buildRegistry();
		CodeContextAnalyzer cca = new CodeContextAnalyzer(analyser, dslRegistry);
		CodeContext codeContext = cca.build();
		assertNotNull(codeContext);
		Map<String, Class<?>> variables = codeContext.getDeclaredVariables();
		assertNotNull(variables);
		assertTrue(variables.containsKey("router"));
		assertTrue(variables.containsKey("blubber"));
		assertEquals(RouteProcessor.class, codeContext.currentClassContext() );
		assertTrue(variables.containsKey("helper"));
		assertEquals(variables.get("helper"), NSFHelper.class);
	}
	
	@Test
	public void testBuildCodeContextForStrategy() throws IOException {
		String dsl = readFile("router.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 73, 2, null);
		assertTrue(analyser.parse());
		assertTrue(analyser.getNode() instanceof ClosureExpression);
		DSLRegistry dslRegistry = DSLRegistryFactory.buildRegistry();
		CodeContextAnalyzer cca = new CodeContextAnalyzer(analyser, dslRegistry);
		CodeContext codeContext = cca.build();
		assertNotNull(codeContext);
		Map<String, Class<?>> variables = codeContext.getDeclaredVariables();
		assertNotNull(variables);
		assertTrue(variables.containsKey("router"));
		assertTrue(variables.containsKey("blubber"));
		assertEquals(GetByKey.class ,codeContext.currentClassContext());
	}
}
