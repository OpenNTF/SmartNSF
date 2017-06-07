package org.openntf.xrest.designer.codeassist;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.openntf.xrest.designer.XRestUIActivator;

public class ProposalFactory {

	public CodeProposal getCodeProposa(ASTNode node, List<ASTNode> callhierarchy) {
		if (node instanceof VariableExpression) {
			VariableExpression ve = (VariableExpression) node;
			return new VEProposal(buildParameter(ve, callhierarchy));
		}

		return null;
	}
	
	private ProposalParameter buildParameter(ASTNode node, List<ASTNode> callhierarchy) {
		ProposalParameter pp = new ProposalParameter();
		pp.add(node);
		pp.add(callhierarchy);
		pp.add(XRestUIActivator.getDefault().getDSLRegistry());
		pp.add(XRestUIActivator.getDefault().getImageRegistry());
		return pp;
	}
}
