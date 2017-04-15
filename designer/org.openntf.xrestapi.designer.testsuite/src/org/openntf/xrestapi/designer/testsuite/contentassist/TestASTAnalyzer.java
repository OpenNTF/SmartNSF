package org.openntf.xrestapi.designer.testsuite.contentassist;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.junit.Test;
import org.openntf.xrest.designer.codeassist.ASTAnalyser;

public class TestASTAnalyzer {

	@Test
	public void testFindAst() throws IOException {
		String dsl = readFile("router.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 8,2);
		assertTrue(analyser.parse());
		ASTNode node = analyser.getNode();
		List<ASTNode> hierarchie = analyser.getHierarchie();
		assertNotNull(node);
		assertNotNull(hierarchie);
		System.out.println("LAST ND: "+ node.getText());
		for (ASTNode hierNode: hierarchie) {
			System.out.println(node.getText());
		}
	}

	private String readFile(String filename) throws IOException {
		InputStream is = getClass().getResourceAsStream(filename);
		return IOUtils.toString(is, "utf-8");
	}

}
