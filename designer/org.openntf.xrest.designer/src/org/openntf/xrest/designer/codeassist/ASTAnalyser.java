package org.openntf.xrest.designer.codeassist;

import java.security.CodeSource;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.openntf.xrest.designer.utils.ReflectionUtils;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;

public class ASTAnalyser {
	private final String dsl;
	private final ClassLoader classLoader;
	private DSLAndGroovyVisitor visitor;
	private MultipleCompilationErrorsException exception;
	private BlockStatement base;

	public ASTAnalyser(String dsl, int line, int column, ClassLoader cl) {
		this.dsl = dsl;
		this.visitor = new DSLAndGroovyVisitor(line, column);
		this.classLoader = cl;
	}

	public boolean parse() {
		//AstBuilder astBuilder = new AstBuilder();
		List<ASTNode> allNodes = Collections.emptyList();
		try {
			allNodes = compile(this.dsl, CompilePhase.CANONICALIZATION, false);
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
	
	private List<ASTNode> compile(String script, CompilePhase compilePhase, boolean statementsOnly) {
        String scriptClassName = "script" + System.currentTimeMillis();
        
        GroovyClassLoader classLoader = getClassLoader();
        GroovyCodeSource codeSource = new GroovyCodeSource(script, scriptClassName + ".groovy", "/groovy/script");
        CodeSource cs = (CodeSource)ReflectionUtils.getPrivateField(GroovyCodeSource.class, "codeSource", codeSource);
        CompilationUnit cu = new CompilationUnit(CompilerConfiguration.DEFAULT, cs, classLoader);
        cu.addSource(codeSource.getName(), script);
        cu.compile(compilePhase.getPhaseNumber());
        List<ASTNode> acc = new LinkedList<ASTNode>();
        for (ModuleNode node: cu.getAST().getModules()) {
        	if (node.getStatementBlock() != null) {
        		acc.add(node.getStatementBlock());
        	}
        	for (Iterator<ClassNode> it = node.getClasses().iterator(); it.hasNext();) {
        		ClassNode classNode = it.next();
        		if (!classNode.getName().equals(scriptClassName) && statementsOnly) {
        			acc.add(classNode);
        		}
        	}
        }
        return acc;
        // collect all the ASTNodes into the result, possibly ignoring the script body if desired
        //return cu.ast.modules.inject([]) {List acc, ModuleNode node ->
        //    if (node.statementBlock) acc.add(node.statementBlock)
        //    node.classes?.each {
        //        if (!(it.name == scriptClassName && statementsOnly)) {
        //            acc << it
        //        }
        //    }
        //    acc
        //}
}

	private GroovyClassLoader getClassLoader() {
		if (this.classLoader == null) {
			return new GroovyClassLoader();
		}
		return new GroovyClassLoader(this.classLoader);
	}

}
