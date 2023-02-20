package org.openntf.xrestapi.designer.testsuite.contentassist;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.expr.MethodCallExpression;

public class AbstractGroovyParserTest {

	public AbstractGroovyParserTest() {
		super();
	}

	protected String readFile(String filename) throws IOException {
		InputStream is = getClass().getResourceAsStream(filename);
		return IOUtils.toString(is, "utf-8");
	}

	protected void printOutHierarchie(List<ASTNode> hierarchie) {
		for (ASTNode n: hierarchie) {
			System.out.println(n.getText() +" -> "+ n.getClass());
			if (n instanceof MethodCallExpression) {
				MethodCallExpression me = (MethodCallExpression)n;
				System.out.println(me.getMethodAsString() + " --> "+ me.getMethod() +" Target: "+ me.getMethodTarget());
				System.out.println(me.getGenericsTypes());
				System.out.println(me.getReceiver());
				System.out.println(me.getType());
			}
		}
	}
	protected ClassLoader buildClassLoader() {
		URL resourceURL = getClass().getResource("../mock/markdown4j-2.2.jar");
		System.out.println("URL" +resourceURL);
		return new URLClassLoader(new URL[]{resourceURL,},getClass().getClassLoader());
	}

}