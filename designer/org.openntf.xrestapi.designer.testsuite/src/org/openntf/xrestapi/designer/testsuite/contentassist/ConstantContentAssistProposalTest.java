package org.openntf.xrestapi.designer.testsuite.contentassist;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.junit.Test;
import org.openntf.xrest.designer.codeassist.CodeContext;
import org.openntf.xrest.designer.codeassist.ProposalParameter;
import org.openntf.xrest.designer.codeassist.analytics.ASTAnalyser;
import org.openntf.xrest.designer.codeassist.analytics.CodeContextAnalyzer;
import org.openntf.xrest.designer.codeassist.proposals.CoEProposal;
import org.openntf.xrest.designer.dsl.DSLRegistry;
import org.openntf.xrest.designer.dsl.DSLRegistryFactory;

public class ConstantContentAssistProposalTest extends AbstractContentAssistProposalTest {

	@Test
	public void testImportMockClass() throws IOException {
		String dsl = readFile("import.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 10, 7, getClassLoader());
		assertTrue(analyser.parse());
		assertTrue(analyser.getNode() instanceof ConstantExpression);
		ConstantExpression ce = (ConstantExpression) analyser.getNode();
		assertTrue("g".equals(ce.getValue()));
		DSLRegistry dslRegistry = DSLRegistryFactory.buildRegistry();
		CodeContextAnalyzer cca = new CodeContextAnalyzer(analyser, dslRegistry);
		CodeContext codeContext = cca.build();
		ImageRegistry imgRegistry = mockImageRegistry();
		ProposalParameter<ConstantExpression> pp = new ProposalParameter<ConstantExpression>();
		pp.add(ce);
		pp.add(analyser.getHierarchie());
		pp.add(dslRegistry);
		pp.add(imgRegistry);
		pp.add(codeContext);
		System.out.println(codeContext.getDeclaredVariables());
		System.out.println(codeContext.currentClassContext());
		CoEProposal coe = new CoEProposal(pp);
		List<ICompletionProposal> proposals = coe.suggestions(0);
		assertEquals(3, proposals.size());
	}
	
	
	private ClassLoader getClassLoader() {
		URL resourceURL = getClass().getResource("../mock/markdown4j-2.2.jar");
		return new URLClassLoader(new URL[]{resourceURL,});

	}
}
