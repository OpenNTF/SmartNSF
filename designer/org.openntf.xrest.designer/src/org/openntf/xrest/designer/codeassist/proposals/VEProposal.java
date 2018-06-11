package org.openntf.xrest.designer.codeassist.proposals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.openntf.xrest.designer.codeassist.CodeContext;
import org.openntf.xrest.designer.codeassist.CodeProposal;
import org.openntf.xrest.designer.codeassist.ProposalParameter;
import org.openntf.xrest.designer.dsl.MapContainer;
import org.openntf.xrest.designer.dsl.MethodContainer;

public class VEProposal extends AbstractProposalFactory implements CodeProposal {
	final ProposalParameter<VariableExpression> parameter;

	public VEProposal(ProposalParameter<VariableExpression> pp) {
		super(pp.getImageRegistry());
		this.parameter = pp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.xrest.designer.codeassist.CodeProposal#suggestions(int)
	 */
	@Override
	public List<ICompletionProposal> suggestions(int offset) {
		VariableExpression expression = parameter.getNode();
		String variableName = expression.getName();
		CodeContext context = this.parameter.getCodeContext();
		if (context.getDeclaredVariables().containsKey(variableName)) {
			Class<?> cl = context.getDeclaredVariables().get(variableName);
			return buildListFromClass(cl, offset,0);
		} else {
			MethodCallExpression me = findCurrentMethodContext();
			if (me != null) {
				VariableExpression recivier = (VariableExpression) me.getReceiver();
				Class<?> cl = parameter.getRegistry().searchMethodClass(recivier.getName(), me.getMethodAsString());
				if (parameter.getRegistry().isMethodConditioned(cl, expression.getName())) {
					List<MethodContainer> mc = parameter.getRegistry().getMethodContainers(cl, expression.getName());
					return buildConditionedMethodContainerProposals(mc, offset);
				} else {
					List<MapContainer> mapContainer = parameter.getRegistry().getMapContainers(cl, expression.getName());
					return buildConditionedMapContainerProposal(mapContainer,offset);
				}
			}
		}
		return Collections.emptyList();
	}

	private List<ICompletionProposal> buildConditionedMapContainerProposal(List<MapContainer> mapContainer, int offset) {
		List<ICompletionProposal> cps = new ArrayList<ICompletionProposal>();
		for (MapContainer container : mapContainer) {
			String value = buildNameFromMapContainer(container);
			String info = buildInfoFromMapContainer(container);
			CompletionProposal cp = new CompletionProposal(value, offset, 0, value.length(), parameter.getImageRegistry().get("bullet_green.png"), info, null, null);
			cps.add(cp);
		}
		return cps;
	}

	private String buildInfoFromMapContainer(MapContainer container) {
		StringBuilder sb = new StringBuilder(" "+container.getKey());
		sb.append(" (");
		sb.append(container.getValueClass().getSimpleName());
		sb.append(")");
		return sb.toString();
	}

	private String buildNameFromMapContainer(MapContainer container) {
		StringBuilder sb = new StringBuilder(container.getKey());
		sb.append(" {");
		boolean hasParam = false;
		if (container.getClosureParameters() != null) {
			for (Class<?> cl : container.getClosureParameters()) {
				if (!hasParam) {
					hasParam = true;
				} else {
					sb.append(", ");
				}
				sb.append(cl.getSimpleName().toLowerCase());
			}
			if (hasParam) {
				sb.append(" -> ");
			}
		}
		sb.append("\n}");

		return sb.toString();
	}

	private List<ICompletionProposal> buildConditionedMethodContainerProposals(List<MethodContainer> mc, int offset) {
		List<ICompletionProposal> cps = new ArrayList<ICompletionProposal>();
		for (MethodContainer container : mc) {
			String value = buildNameFromMethodContainer(container);
			String info = buildInfoFromMethodContainer(container);
			CompletionProposal cp = new CompletionProposal(value, offset, 0, value.length(), parameter.getImageRegistry().get("bullet_green.png"), info, null, null);
			cps.add(cp);
		}
		return cps;
	}

	private String buildInfoFromMethodContainer(MethodContainer container) {
		StringBuilder sb = new StringBuilder(" "+container.getCondition());
		sb.append(" (");
		sb.append(container.getClosureClass().getSimpleName());
		sb.append(")");
		return sb.toString();
	}

	private String buildNameFromMethodContainer(MethodContainer container) {
		StringBuilder sb = new StringBuilder(container.getCondition());
		sb.append(" {");
		boolean hasParam = false;
		if (container.getClosureParameters() != null) {
			for (Class<?> cl : container.getClosureParameters()) {
				if (!hasParam) {
					hasParam = true;
				} else {
					sb.append(", ");
				}
				sb.append(cl.getSimpleName().toLowerCase());
			}
			if (hasParam) {
				sb.append(" -> ");
			}
		}
		sb.append("\n}");

		return sb.toString();
	}

	private MethodCallExpression findCurrentMethodContext() {
		List<ASTNode> hierRevers = new ArrayList<ASTNode>(parameter.getHierarchie());
		Collections.reverse(hierRevers);
		MethodCallExpression me = null;
		for (ASTNode node : hierRevers) {
			if (node instanceof MethodCallExpression) {
				me = (MethodCallExpression) node;
			}
		}
		return me;
	}

}
