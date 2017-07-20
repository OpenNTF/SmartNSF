package org.openntf.xrest.designer.codeassist;

import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;

public class ASTAnalyser {
	private final String dsl;
	private DSLAndGroovyVisitor visitor;
	private MultipleCompilationErrorsException exception;
	private BlockStatement base;

	public ASTAnalyser(String dsl, int line, int column) {
		this.dsl = dsl;
		this.visitor = new DSLAndGroovyVisitor(line, column);
		
	}

	public boolean parse() {
		AstBuilder astBuilder = new AstBuilder();
		List<ASTNode> allNodes = Collections.emptyList();
		try {
			allNodes = astBuilder.buildFromString(CompilePhase.CANONICALIZATION, false, this.dsl);
		} catch (MultipleCompilationErrorsException e) {
			exception = e;
			return false;
		}
		if (allNodes.isEmpty()) {
			return false;
		}
		BlockStatement bs = (BlockStatement) allNodes.get(0);
		this.base = bs;
		bs.visit(this.visitor);
		return true;

	}

	public ASTNode getNode() {
		return visitor.getNode();
	}

	public List<ASTNode> getHierarchie() {
		return visitor.getHierarchie();
	}

	public MultipleCompilationErrorsException getException() {
		return exception;
	}

	public BlockStatement getBase() {
		return base;
	}

}
