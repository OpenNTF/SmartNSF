package org.openntf.xrest.designer.codeassist;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import groovy.lang.Closure;

public class AbstractProposalFactory {

	private final ImageRegistry imageRegistry;
	
	public AbstractProposalFactory(ImageRegistry imageRegistry) {
		this.imageRegistry = imageRegistry;
	}

	protected List<ICompletionProposal> buildListFromClass(Class<?> cl, int offset) {
		List<ICompletionProposal> props = new ArrayList<ICompletionProposal>();
		for (Method m : cl.getMethods()) {
			String value = buildName(m);
			String info = buildInfo(m);
			CompletionProposal cp = new CompletionProposal(value, offset, 0, value.length(), imageRegistry.get("bullet_green.png"), info, null, null);
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