package org.openntf.xrest.designer.codeassist.proposals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.openntf.xrest.designer.codeassist.ProposalParameter;

public class CoEProposal extends AbstractProposalFactory {

	private final ProposalParameter<ConstantExpression> parameter;
	
	public CoEProposal(ProposalParameter<ConstantExpression> parameter) {
		super(parameter.getImageRegistry());
		this.parameter =parameter;
	}

	@Override
	public List<ICompletionProposal> suggestions(int offset) {
		String searchValue = this.parameter.getNode().getValue() +"";
		ASTNode previousNode = parameter.getHierarchie().get(parameter.getHierarchie().size() -2);
		if (previousNode instanceof PropertyExpression) {
			PropertyExpression pe = (PropertyExpression)previousNode;
			if (pe.getObjectExpression() instanceof VariableExpression) {
				VariableExpression ve = (VariableExpression)pe.getObjectExpression();
				List<ICompletionProposal> proposals = buildListFromClass(ve.getType().getTypeClass(), offset - searchValue.length(), searchValue.length());
				return filteredList(proposals, searchValue);
			}
		}
		return Collections.emptyList();
	}

	private List<ICompletionProposal> filteredList(List<ICompletionProposal> proposals, String searchValue) {
		List<ICompletionProposal> filtered = new ArrayList<ICompletionProposal>();
		for (ICompletionProposal prop: proposals) {
			if (prop.getDisplayString().toLowerCase().startsWith(searchValue)) {
				filtered.add(prop);
			}
		}
		return filtered;
	}
	
	
}
