package org.openntf.xrest.designer.codeassist;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.eclipse.jface.resource.ImageRegistry;
import org.openntf.xrest.designer.dsl.DSLRegistry;

public class ProposalParameter {

	private ASTNode node;
	private List<ASTNode> hierarchie;
	private DSLRegistry registry;
	private ImageRegistry imageRegistry;

	public ProposalParameter add(ASTNode node) {
		this.node = node;
		return this;
	}

	public ProposalParameter add(List<ASTNode> hierarchie) {
		this.hierarchie = hierarchie;
		return this;
	}

	public ProposalParameter add(DSLRegistry registry) {
		this.registry = registry;
		return this;
	}

	public ProposalParameter add(ImageRegistry registry) {
		this.imageRegistry = registry;
		return this;
	}

	public ASTNode getNode() {
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

}
