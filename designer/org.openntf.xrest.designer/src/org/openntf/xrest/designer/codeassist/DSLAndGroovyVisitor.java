package org.openntf.xrest.designer.codeassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.MethodPointerExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.SpreadMapExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.UnaryMinusExpression;
import org.codehaus.groovy.ast.expr.UnaryPlusExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.classgen.BytecodeExpression;

public class DSLAndGroovyVisitor implements GroovyCodeVisitor {

	private enum ScanOperation {
		DEEPER, STRIKE, EXIT
	}

	private final int line;
	private final int column;
	private ASTNode resultNode;
	private ScanOperation operation = ScanOperation.DEEPER;
	private List<ASTNode> nodeHierarchie = new LinkedList<ASTNode>();

	public DSLAndGroovyVisitor(int line, int column) {
		this.line = line;
		this.column = column;
	}

	@Override
	public void visitArgumentlistExpression(ArgumentListExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);
	}

	@Override
	public void visitArrayExpression(ArrayExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);
	}

	@Override
	public void visitAssertStatement(AssertStatement arg0) {
		ScanOperation ops = scanDeeper(arg0);
	}

	@Override
	public void visitAttributeExpression(AttributeExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitBinaryExpression(BinaryExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);
	}

	@Override
	public void visitBitwiseNegationExpression(BitwiseNegationExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitBlockStatement(BlockStatement arg0) {
		ScanOperation ops = scanDeeper(arg0);
		if (ops == ScanOperation.EXIT) {
			return;
		}
		walkChildren(arg0.getStatements());
	}

	@Override
	public void visitBooleanExpression(BooleanExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitBreakStatement(BreakStatement arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitBytecodeExpression(BytecodeExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitCaseStatement(CaseStatement arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitCastExpression(CastExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitCatchStatement(CatchStatement arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitClassExpression(ClassExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitClosureExpression(ClosureExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitClosureListExpression(ClosureListExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitConstantExpression(ConstantExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitConstructorCallExpression(ConstructorCallExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitContinueStatement(ContinueStatement arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitDeclarationExpression(DeclarationExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitDoWhileLoop(DoWhileStatement arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitExpressionStatement(ExpressionStatement arg0) {
		ScanOperation ops = scanDeeper(arg0);
		if (ops == ScanOperation.EXIT) {
			return;
		}
		Expression ex = arg0.getExpression();
		registerNodeToHierarchie(ex);
		ex.visit(this);
		dergisterNodeFromHierarchie(ex);
	}

	@Override
	public void visitFieldExpression(FieldExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitForLoop(ForStatement arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitGStringExpression(GStringExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitIfElse(IfStatement arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitListExpression(ListExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitMapEntryExpression(MapEntryExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitMapExpression(MapExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitMethodCallExpression(MethodCallExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);
		if (ops == ScanOperation.EXIT) {
			return;
		}
		List<ASTNode> elements = new ArrayList<ASTNode>();
		elements.add(arg0.getReceiver());
		elements.add(arg0.getArguments());
		elements.add(arg0.getDeclaringClass());
		elements.add(arg0.getMethod());
		elements.add(arg0.getObjectExpression());
		if (arg0.getGenericsTypes() != null) {
			elements.addAll(Arrays.asList(arg0.getGenericsTypes()));
		}
		walkChildren(elements);
	}

	@Override
	public void visitMethodPointerExpression(MethodPointerExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitNotExpression(NotExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitPostfixExpression(PostfixExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitPrefixExpression(PrefixExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitPropertyExpression(PropertyExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitRangeExpression(RangeExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitReturnStatement(ReturnStatement arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitShortTernaryExpression(ElvisOperatorExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitSpreadExpression(SpreadExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitSpreadMapExpression(SpreadMapExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitStaticMethodCallExpression(StaticMethodCallExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitSwitch(SwitchStatement arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitSynchronizedStatement(SynchronizedStatement arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitTernaryExpression(TernaryExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitThrowStatement(ThrowStatement arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitTryCatchFinally(TryCatchStatement arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitTupleExpression(TupleExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitUnaryMinusExpression(UnaryMinusExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitUnaryPlusExpression(UnaryPlusExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	@Override
	public void visitVariableExpression(VariableExpression arg0) {
		ScanOperation ops = scanDeeper(arg0);
		if (ops == ScanOperation.STRIKE) {
			resultNode = arg0;
		}
	}

	@Override
	public void visitWhileLoop(WhileStatement arg0) {
		ScanOperation ops = scanDeeper(arg0);

	}

	public ASTNode getNode() {
		return resultNode;
	}

	public List<ASTNode> getHierarchie() {
		return nodeHierarchie;
	}

	private void walkChildren(List<? extends ASTNode> children) {
		for (ASTNode child : children) {
			if (child != null) {
				System.out.println("CHILD TO TEST:" + child.getText());
				if (operation != ScanOperation.STRIKE) {
					nodeHierarchie.add(child);
					System.out.println("CHILD ADDED: " + nodeHierarchie.size());
					child.visit(this);
					System.out.println("VISIT DONE");
					if (operation != ScanOperation.STRIKE) {
						System.out.println("REMOVE CHILD: " + nodeHierarchie.size());
						nodeHierarchie.remove(child);
						System.out.println("CHILD REMOVED: " + nodeHierarchie.size());

					}
				}
			}
		}

	}

	private ScanOperation scanDeeper(ASTNode arg0) {
		if (arg0.getLineNumber() == -1) {
			return ScanOperation.DEEPER;
		}
		if (arg0.getLineNumber() <= line && arg0.getLastLineNumber() >= line) {
			if (arg0.getLineNumber() == line && arg0.getLastLineNumber() == line) {
				if (arg0.getColumnNumber() <= column && arg0.getLastColumnNumber() >= column) {
					operation = ScanOperation.STRIKE;
					return ScanOperation.STRIKE;
				}
			}
			return ScanOperation.DEEPER;
		}
		return ScanOperation.EXIT;
	}

	private void registerNodeToHierarchie(ASTNode node) {
		nodeHierarchie.add(node);
	}

	private void dergisterNodeFromHierarchie(ASTNode node) {
		if (operation != ScanOperation.STRIKE) {
			nodeHierarchie.remove(node);
		}
	}
}
