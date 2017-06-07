package org.openntf.xrest.designer.codeassist;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.openntf.xrest.designer.dsl.MethodContainer;

import groovy.lang.Closure;

public class VEProposal implements CodeProposal {
	private final ProposalParameter parameter;

	public VEProposal(ProposalParameter pp) {
		this.parameter = pp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.xrest.designer.codeassist.CodeProposal#suggestions(int)
	 */
	@Override
	public List<ICompletionProposal> suggestions(int offset) {
		VariableExpression expression = (VariableExpression) parameter.getNode();
		if (parameter.getRegistry().isBaseAlias(expression.getName())) {
			Class<?> cl = parameter.getRegistry().getBaseClass();
			return buildListFromClass(cl, offset);
		} else {
			MethodCallExpression me = findCurrentMethodContext();
			if (me != null) {
				VariableExpression recivier = (VariableExpression) me.getReceiver();
				Class<?> cl = parameter.getRegistry().searchMethodClass(recivier.getName(), me.getMethodAsString());
				if (parameter.getRegistry().isMethodConditioned(cl, expression.getName())) {
					List<MethodContainer> mc = parameter.getRegistry().getMethodContainers(cl, expression.getName());
					return buildConditionedMethodContainerProposals(mc, offset);
				}
			}
		}
		return Collections.emptyList();
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
				sb.append(cl.getSimpleName());
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
				break;
			}
		}
		return me;
	}

	private List<ICompletionProposal> buildListFromClass(Class<?> cl, int offset) {
		List<ICompletionProposal> props = new ArrayList<ICompletionProposal>();
		for (Method m : cl.getMethods()) {
			String value = buildName(m);
			String info = buildInfo(m);
			CompletionProposal cp = new CompletionProposal(value, offset, 0, value.length(), parameter.getImageRegistry().get("bullet_green.png"), info, null, null);
			props.add(cp);
		}
		return props;
	}

	private String buildInfo(Method m) {
		StringBuilder sb = new StringBuilder(m.getName());
		sb.append("(");
		boolean isSec = false;
		for (java.lang.reflect.Parameter p : m.getParameters()) {
			if (isSec) {
				sb.append(", ");
			} else {
				isSec = true;
			}
			sb.append(p.getType().getSimpleName());
			sb.append(" ");
			sb.append(p.getName());
		}
		sb.append("): ");
		sb.append(m.getReturnType().getSimpleName());
		return sb.toString();
	}

	private String buildName(Method m) {
		StringBuilder sb = new StringBuilder(m.getName());
		sb.append("(");
		boolean isSec = false;
		boolean appendClosure = false;
		for (java.lang.reflect.Parameter p : m.getParameters()) {
			if (isSec) {
				sb.append(", ");
			} else {
				isSec = true;
			}
			if (p.getType().equals(String.class)) {
				sb.append("'" + p.getName() + "'");
			} else if (p.getType().equals(Closure.class)) {
				appendClosure = true;
			} else {
				sb.append(p.getName());
			}

		}
		sb.append(")");
		if (appendClosure) {
			sb.append(" {\n}");
		}
		return sb.toString();
	}

}
