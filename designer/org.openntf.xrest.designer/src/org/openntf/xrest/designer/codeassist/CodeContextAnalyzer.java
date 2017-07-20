package org.openntf.xrest.designer.codeassist;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.openntf.xrest.designer.dsl.DSLRegistry;
import org.openntf.xrest.designer.dsl.MapContainer;

public class CodeContextAnalyzer {
	private final ASTAnalyser analyser;
	private final DSLRegistry dslRegistry;
	
	public CodeContextAnalyzer(ASTAnalyser analyser, DSLRegistry dslRegistry) {
		this.analyser = analyser;
		this.dslRegistry = dslRegistry;
	}
	
	public CodeContext build() {
		Map<String, Class<?>> declaredVariables = new HashMap<String, Class<?>>();
		declaredVariables.put(dslRegistry.getBaseAlias(), dslRegistry.getBaseClass());
		findDeclarations(declaredVariables);
		Class<?> currentClassContext = findClassContext(declaredVariables);
		return new CodeContext(declaredVariables, currentClassContext);
	}

	private void findDeclarations( Map<String, Class<?>> declaredVariables) {
		BlockStatement bs = analyser.getBase();
		for (Entry<String, Variable> entry : bs.getVariableScope().getDeclaredVariables().entrySet()) {
			declaredVariables.put(entry.getKey(), entry.getValue().getType().getTypeClass());
		}
	}

	private Class<?> findClassContext( Map<String, Class<?>> declaredVariables) {
		ClassContextAnalyzerParameter ccap =new ClassContextAnalyzerParameter(dslRegistry, declaredVariables);
		for (ASTNode node : analyser.getHierarchie()) {

			if (node instanceof MethodCallExpression) {
				MethodCallExpression me = (MethodCallExpression) node;
				processMethodCallExpression(ccap, me);
			}
			if (node instanceof MapEntryExpression) {
				MapEntryExpression mee = (MapEntryExpression) node;
				processMapEntryExpression(ccap, mee);
			}
			if (node instanceof ClosureExpression) {
				ccap.currentClass = processClosureExpression(ccap, (ClosureExpression)node);
			}
			if (node instanceof BlockStatement) {
				BlockStatement bs = (BlockStatement)node;
				processBlockStatement(ccap, bs);
			}
			if (node instanceof ArgumentListExpression) {
				ArgumentListExpression ale = (ArgumentListExpression)node;
				processArgumentListExpression(ccap, ale);
			}
		}
		return ccap.currentClass;
	}

	private void processArgumentListExpression(ClassContextAnalyzerParameter ccap, ArgumentListExpression ale) {
		if (!ale.getExpressions().isEmpty()) {
			Expression firstExp = ale.getExpression(0);
			if (firstExp instanceof VariableExpression) {
				VariableExpression ve = (VariableExpression)firstExp;
				ccap.currentFirstArgument = ve.getName();
			}
		}
	}

	private void processBlockStatement(ClassContextAnalyzerParameter ccap, BlockStatement bs) {
		Set<String> variableNames = new HashSet<String>();
		VariableScope vs = bs.getVariableScope();
		for (Entry<String, Variable> variableEntry :vs.getDeclaredVariables().entrySet()) {
			if (variableEntry.getValue() instanceof VariableExpression) {
				VariableExpression exp = (VariableExpression)variableEntry.getValue();
				variableNames.add(exp.getName());
			}
		}
		for (Statement stmt: bs.getStatements() ) {
			if (stmt instanceof ExpressionStatement) {
				ExpressionStatement es = (ExpressionStatement)stmt;
				if(es.getExpression() instanceof DeclarationExpression) {
					DeclarationExpression des = (DeclarationExpression)es.getExpression();
					if (des.getLeftExpression() instanceof VariableExpression) {
						VariableExpression exp = (VariableExpression) des.getLeftExpression();
						if (variableNames.contains(exp.getName())) {
							Class<?> cl = findClassFromExpression(des.getRightExpression(), ccap.declaredVariables);
							ccap.declaredVariables.put(exp.getName(), cl);
						}
					}
				}
			}
		}
	}

	private void processMapEntryExpression(ClassContextAnalyzerParameter ccap, MapEntryExpression mee) {
		if (mee.getKeyExpression() instanceof ConstantExpression) {
			ConstantExpression ce = (ConstantExpression) mee.getKeyExpression();
			ccap.isMapKeyBased = true;
			ccap.currentKey = ce.getValue()+"";
		}
	}

	private void processMethodCallExpression(ClassContextAnalyzerParameter ccap, MethodCallExpression me) {
		ccap.currentMethodName = me.getMethodAsString();
		ccap.isMapKeyBased = false;
		if (me.getReceiver() instanceof VariableExpression) {
			VariableExpression recivier = (VariableExpression) me.getReceiver();
			if ("this".equals(recivier.getName()) && ccap.currentClass != null) {
				ccap.currentVariable = ccap.currentClass.getCanonicalName();
				ccap.currentVariableClass = ccap.currentClass;
			} else {
				ccap.currentVariable = recivier.getName();
			}
		}
	}

	private Class<?> findClassFromExpression(Expression rightExpression, Map<String,Class<?>> declaredVariables) {
		if (rightExpression instanceof MethodCallExpression) {
			MethodCallExpression me = (MethodCallExpression)rightExpression;
			if (me.getReceiver() instanceof VariableExpression) {
				VariableExpression ve = (VariableExpression) me.getReceiver();
				if (declaredVariables.containsKey(ve.getName())) {
					Class<?> cl = declaredVariables.get(ve.getName());
					return getReturnTypeOf(cl, me.getMethod());
				}
			}
		}
		return Object.class;
	}

	private Class<?> getReturnTypeOf(Class<?> cl, Expression method) {
		String methodName = method.getText();
		for (Method me : cl.getMethods()) {
			if (me.getName().equals(methodName)) {
				return me.getReturnType();
			}
		}
		return Object.class;
	}

	private Class<?> processClosureExpression(ClassContextAnalyzerParameter classContextAnalyzerParameter, ClosureExpression ce) {
		Class<?> currentClass = classContextAnalyzerParameter.currentClass;
		if (classContextAnalyzerParameter.isMapKeyBased) {
			currentClass = processMapKeyBasedClosuresExpression(classContextAnalyzerParameter, ce, currentClass);
		} else {
			if (classContextAnalyzerParameter.dslRegistry.isBaseAlias(classContextAnalyzerParameter.currentVariable)) {
				currentClass = classContextAnalyzerParameter.dslRegistry.getObjectForClosureInMethod(classContextAnalyzerParameter.currentMethodName);
			} else {
				if (classContextAnalyzerParameter.dslRegistry.isMethodConditioned(classContextAnalyzerParameter.currentVariableClass, classContextAnalyzerParameter.currentMethodName)) {
					currentClass = classContextAnalyzerParameter.dslRegistry.getObjectForClosureInMethodByCondition(classContextAnalyzerParameter.currentVariableClass, classContextAnalyzerParameter.currentMethodName, classContextAnalyzerParameter.currentFirstArgument);
				} else {
					currentClass = classContextAnalyzerParameter.dslRegistry.getObjectForClosureInMethod(classContextAnalyzerParameter.currentVariable, classContextAnalyzerParameter.currentMethodName);
				}
			}
		}
		return currentClass;
	}

	private Class<?> processMapKeyBasedClosuresExpression(ClassContextAnalyzerParameter classContextAnalyzerParameter, ClosureExpression ce, final Class<?> currentClass) {
		Class<?> returningClass = currentClass;
		List<MapContainer> mcs = classContextAnalyzerParameter.dslRegistry.getMapContainers(classContextAnalyzerParameter.currentVariableClass, classContextAnalyzerParameter.currentMethodName);
		for (MapContainer mc : mcs) {
			if (mc.getKey().equals(classContextAnalyzerParameter.currentKey)) {
				returningClass = mc.getContainerClass();
				if (ce.getParameters() != null) {
					int counter = 0;
					List<Class<?>> cParameter = mc.getClosureParameters();
					for (Parameter param : ce.getParameters()) {
						String name = param.getName();
						if (counter < cParameter.size()) {
							classContextAnalyzerParameter.declaredVariables.put(name, cParameter.get(counter));
						}
						counter++;
					}
				}
			}
		}
		return returningClass;
	}
}
