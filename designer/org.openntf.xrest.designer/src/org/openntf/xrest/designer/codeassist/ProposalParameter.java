package org.openntf.xrest.designer.codeassist;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.eclipse.jface.resource.ImageRegistry;
import org.openntf.xrest.designer.dsl.DSLRegistry;

public class ProposalParameter<T extends ASTNode> {

	private T node;
	private List<ASTNode> hierarchie;
	private DSLRegistry registry;
	private ImageRegistry imageRegistry;
	private CodeContext codeContext;

	public ProposalParameter<T> add(T node) {
		this.node = node;
		return this;
	}

	public ProposalParameter<T> add(List<ASTNode> hierarchie) {
		this.hierarchie = hierarchie;
		return this;
	}

	public ProposalParameter<T> add(DSLRegistry registry) {
		this.registry = registry;
		return this;
	}

	public ProposalParameter<T> add(ImageRegistry registry) {
		this.imageRegistry = registry;
		return this;
	}
	
	public ProposalParameter<T> add(CodeContext context) {
		this.codeContext = context;
		return this;
	}

	public T getNode() {
		return node;
	}

	public List<ASTNode> getHierarchie() {
		return hierarchie;
	}

	public DSLRegistry getRegistry() {
		return registry;
	}

	public ImageRegistry getImageRegistry() {
		return imageRegistry;
	}
	
	public CodeContext getCodeContext() {
		return codeContext;
	}

}
