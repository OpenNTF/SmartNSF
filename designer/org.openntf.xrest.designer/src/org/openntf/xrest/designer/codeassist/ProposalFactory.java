package org.openntf.xrest.designer.codeassist;

import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.openntf.xrest.designer.XRestUIActivator;
import org.openntf.xrest.designer.dsl.DSLRegistry;

public class ProposalFactory {

	public CodeProposal getCodeProposal(ASTAnalyser analyser) {
		DSLRegistry dslRegistry =XRestUIActivator.getDefault().getDSLRegistry();
		CodeContextAnalyzer cca = new CodeContextAnalyzer(analyser, dslRegistry);
		CodeContext codeContext = cca.build();

		if (analyser.getNode() instanceof VariableExpression) {
			return new VEProposal(buildParameter(analyser, codeContext));
		}
		if (analyser.getNode() instanceof ClosureExpression) {
			return new CEProposal(buildParameter(analyser, codeContext));
		}

		return null;
	}
	
	private ProposalParameter buildParameter(ASTAnalyser analyser, CodeContext codeContext) {
		ProposalParameter pp = new ProposalParameter();
		pp.add(analyser.getNode());
		pp.add(analyser.getHierarchie());
		pp.add(codeContext);
		pp.add(XRestUIActivator.getDefault().getDSLRegistry());
		pp.add(XRestUIActivator.getDefault().getImageRegistry());
		return pp;
	}
}
