package org.openntf.xrest.designer.codeassist;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.openntf.xrest.designer.XRestUIActivator;

import groovy.lang.Closure;

public class VEProposal {
	private final Map<String, Class<?>> predefinedObject;
	private final VariableExpression expression;
	private final List<ASTNode> callHierarchie;

	public VEProposal(VariableExpression ve, List<ASTNode> hierarchie, Map<String, Class<?>> predefinedObjects) {
		this.predefinedObject = predefinedObjects;
		this.expression = ve;
		this.callHierarchie = hierarchie;
	}

	public List<ICompletionProposal> suggestions(int offset) {
		if (predefinedObject.containsKey(expression.getName())) {
			Class<?> cl = predefinedObject.get(expression.getName());
			return buildListFromClass(cl, offset);
		} else {
			MethodCallExpression me = findCurrentMethodContext();
			if (me != null) {
				System.out.println(me.getText());
				System.out.println(me.getReceiver());
				System.out.println(me.getObjectExpression());
			}
		}
		return Collections.emptyList();
	}

	private MethodCallExpression findCurrentMethodContext() {
		List<ASTNode> hierRevers = new ArrayList<ASTNode>(callHierarchie);
		Collections.reverse(hierRevers);
		MethodCallExpression me = null;
		for (ASTNode node: hierRevers) {
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
			CompletionProposal cp = new CompletionProposal(value, offset, 0, value.length(), XRestUIActivator.getDefault().getImageByKey("bullet_green.png"), info, null, null);
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
		sb.append(m.getReturnType().getName());
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
				appendClosure =true;
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
