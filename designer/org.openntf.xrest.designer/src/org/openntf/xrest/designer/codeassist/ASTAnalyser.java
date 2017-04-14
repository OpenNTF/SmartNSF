package org.openntf.xrest.designer.codeassist;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.control.CompilePhase;

public enum ASTAnalyser {
	INSTANCE;

	public ASTNode parseAndfindNode(String dsl, int line, int column) {
		AstBuilder astBuilder = new AstBuilder();
		List<ASTNode> allNodes = astBuilder.buildFromString(CompilePhase.SEMANTIC_ANALYSIS, false, dsl);
		if (allNodes.isEmpty()) {
			return null;
		}
		BlockStatement bs = (BlockStatement) allNodes.get(0);
		DSLAndGroovyVisitor visitor = new DSLAndGroovyVisitor(line, column);
		bs.visit(visitor);
		return visitor.getNode();	
	}
}
