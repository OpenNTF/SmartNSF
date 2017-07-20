package org.openntf.xrest.designer.codeassist;

import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class CEProposal extends AbstractProposalFactory implements CodeProposal {

	private final ProposalParameter parameter;
	
	public CEProposal(ProposalParameter parameter) {
		super(parameter.getImageRegistry());
		this.parameter =parameter;
	}

	@Override
	public List<ICompletionProposal> suggestions(int offset) {
		CodeContext context = this.parameter.getCodeContext();
		Class<?> currentClassContext = context.currentClassContext();
		return buildListFromClass(currentClassContext, offset);
	}
}
