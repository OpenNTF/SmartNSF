package org.openntf.xrestapi.designer.testsuite.contentassist;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.junit.Test;
import org.openntf.xrest.designer.codeassist.CodeContext;
import org.openntf.xrest.designer.codeassist.analytics.ASTAnalyser;
import org.openntf.xrest.designer.codeassist.analytics.CodeContextAnalyzer;
import org.openntf.xrest.designer.dsl.DSLRegistry;
import org.openntf.xrest.designer.dsl.DSLRegistryFactory;

public class JavaClassImportTest extends AbstractGroovyParserTest {

	@Test
	public void testImportWithoutContextClassLoader() throws IOException {
		String dsl = readFile("import.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 8, 7, null);
		assertFalse(analyser.parse());
	}
	
	@Test
	public void testImportWithURLClassLoader() throws IOException {
		String dsl = readFile("import.groovy");
		URL resourceURL = getClass().getResource("../mock/markdown4j-2.2.jar");
		System.out.println("URL" +resourceURL);
		ClassLoader cl = new URLClassLoader(new URL[]{resourceURL,});
		
		ASTAnalyser analyser = new ASTAnalyser(dsl, 8, 7, cl);
		assertTrue(analyser.parse());		
	}
	
	@Test
	public void testClassOfImportedProcessor() throws IOException {
		String dsl = readFile("import.groovy");
		URL resourceURL = getClass().getResource("../mock/markdown4j-2.2.jar");
		System.out.println("URL" +resourceURL);
		ClassLoader cl = new URLClassLoader(new URL[]{resourceURL,});
		
		ASTAnalyser analyser = new ASTAnalyser(dsl, 8, 9, cl);
		assertTrue(analyser.parse());
		System.out.println(analyser.getNode());
		assertTrue(analyser.getNode() instanceof VariableExpression);
		DSLRegistry dslRegistry = DSLRegistryFactory.buildRegistry();
		CodeContextAnalyzer cca = new CodeContextAnalyzer(analyser, dslRegistry);
		CodeContext codeContext = cca.build();
		assertNotNull(codeContext);
		Map<String, Class<?>> variables = codeContext.getDeclaredVariables();
		assertNotNull(variables);
		assertTrue(variables.containsKey("processor"));
		assertEquals("org.markdown4j.Markdown4jProcessor", variables.get("processor").getName());

	}
}