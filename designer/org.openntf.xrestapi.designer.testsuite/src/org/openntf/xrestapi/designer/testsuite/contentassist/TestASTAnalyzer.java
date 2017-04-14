package org.openntf.xrestapi.designer.testsuite.contentassist;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.junit.Test;
import org.openntf.xrest.designer.codeassist.ASTAnalyser;


public class TestASTAnalyzer {

	@Test
	public void testFindAst() throws IOException {
		String dsl = readFile("router.groovy");
		ASTNode node = ASTAnalyser.INSTANCE.parseAndfindNode(dsl, 8,2);
		assertNotNull(node);
	}
	
	private String readFile(String filename) throws IOException {
		InputStream is = getClass().getResourceAsStream(filename);
		return IOUtils.toString(is, "utf-8");
	}

}
