package org.openntf.xrest.designer.codeassist.proposals;

import java.util.List;

import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.openntf.xrest.designer.codeassist.CodeContext;
import org.openntf.xrest.designer.codeassist.CodeProposal;
import org.openntf.xrest.designer.codeassist.ProposalParameter;

public class CEProposal extends AbstractProposalFactory implements CodeProposal {

	private final ProposalParameter<ClosureExpression> parameter;
	
	public CEProposal(ProposalParameter<ClosureExpression> parameter) {
		super(parameter.getImageRegistry());
		this.parameter =parameter;
	}

	@Override
	public List<ICompletionProposal> suggestions(int offset) {
		CodeContext context = this.parameter.getCodeContext();
		Class<?> currentClassContext = context.currentClassContext();
		return buildListFromClass(currentClassContext, offset,0);
	}
}
