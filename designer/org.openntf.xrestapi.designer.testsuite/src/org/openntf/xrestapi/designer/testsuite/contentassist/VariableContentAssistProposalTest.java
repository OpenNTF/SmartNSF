package org.openntf.xrestapi.designer.testsuite.contentassist;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.ast.expr.VariableExpression;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.junit.Test;
import org.openntf.xrest.designer.codeassist.ASTAnalyser;
import org.openntf.xrest.designer.codeassist.VEProposal;
import org.openntf.xrest.xsp.model.Router;

public class VariableContentAssistProposalTest extends AbstractGroovyParserTest {

	@Test
	public void testRouterVariable() throws IOException {
		String dsl = readFile("router.groovy");
		ASTAnalyser analyser = new ASTAnalyser(dsl, 8, 7);
		assertTrue(analyser.parse());
		assertTrue(analyser.getNode() instanceof VariableExpression);
		VariableExpression ve = (VariableExpression) analyser.getNode();
		assertTrue("router".equals(ve.getName()));
		Map<String, Class<?>> predefindeObject = new HashMap<String, Class<?>>();
		predefindeObject.put("router", Router.class);
		VEProposal veproposal = new VEProposal(ve, analyser.getHierarchie(), predefindeObject);
		List<ICompletionProposal> proposals = veproposal.suggestions(0);
		assertNotNull(proposals);
	}
}
