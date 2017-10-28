package org.openntf.xrest.designer.codeassist;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.openntf.xrest.designer.XRestUIActivator;
import org.openntf.xrest.designer.codeassist.analytics.ASTAnalyser;
import org.openntf.xrest.designer.codeassist.analytics.CodeContextAnalyzer;
import org.openntf.xrest.designer.codeassist.proposals.CEProposal;
import org.openntf.xrest.designer.codeassist.proposals.CoEProposal;
import org.openntf.xrest.designer.codeassist.proposals.VEProposal;
import org.openntf.xrest.designer.dsl.DSLRegistry;

public class ProposalFactory {

	public CodeProposal getCodeProposal(ASTAnalyser analyser) {
		DSLRegistry dslRegistry =XRestUIActivator.getDefault().getDSLRegistry();
		CodeContextAnalyzer cca = new CodeContextAnalyzer(analyser, dslRegistry);
		CodeContext codeContext = cca.build();

		if (analyser.getNode() instanceof VariableExpression) {
			return new VEProposal(buildParameter(analyser, codeContext, VariableExpression.class));
		}
		if (analyser.getNode() instanceof ClosureExpression) {
			return new CEProposal(buildParameter(analyser, codeContext, ClosureExpression.class));
		}
		if (analyser.getNode() instanceof ConstantExpression) {
			return new CoEProposal(buildParameter(analyser, codeContext, ConstantExpression.class));
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	private <T extends ASTNode> ProposalParameter<T> buildParameter(ASTAnalyser analyser, CodeContext codeContext, Class<T> classX) {
		ProposalParameter<T> pp = new ProposalParameter<T>();
		pp.add((T)analyser.getNode());
		pp.add(analyser.getHierarchie());
		pp.add(codeContext);
		pp.add(XRestUIActivator.getDefault().getDSLRegistry());
		pp.add(XRestUIActivator.getDefault().getImageRegistry());
		return pp;
	}
}
