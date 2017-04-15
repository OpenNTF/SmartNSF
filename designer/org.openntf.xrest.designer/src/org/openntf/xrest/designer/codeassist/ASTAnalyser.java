package org.openntf.xrest.designer.codeassist;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.control.CompilePhase;

public class ASTAnalyser {
	private final String dsl;
	private DSLAndGroovyVisitor visitor;

	public ASTAnalyser(String dsl, int line, int column) {
		this.dsl = dsl;
		this.visitor = new DSLAndGroovyVisitor(line, column);
	}

	public boolean parse() {
		AstBuilder astBuilder = new AstBuilder();
		List<ASTNode> allNodes = astBuilder.buildFromString(CompilePhase.SEMANTIC_ANALYSIS, false, this.dsl);
		if (allNodes.isEmpty()) {
			return false;
		}
		BlockStatement bs = (BlockStatement) allNodes.get(0);
		bs.visit(this.visitor);
		return true;

	}

	public ASTNode getNode() {
		return visitor.getNode();
	}

	public List<ASTNode> getHierarchie() {
		return visitor.getHierarchie();
	}
}
