package org.openntf.xrest.xsp.library;

import javax.faces.application.Application;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.binding.BindingFactory;
import com.ibm.xsp.util.ValueBindingUtil;

public class GroovyScriptBindingFactory implements BindingFactory {

	@Override
	public MethodBinding createMethodBinding(Application arg0, String formula, Class[] params) {
		String cleanFormula = ValueBindingUtil.parseSimpleExpression(formula);		
		return new GroovyMethodBinding(cleanFormula, params);
	}

	@Override
	public ValueBinding createValueBinding(Application arg0, String formula) {
		String cleanFormula = ValueBindingUtil.parseSimpleExpression(formula);
		return new GroovyValueBinding(cleanFormula);
	}

	@Override
	public String getPrefix() {
		return "groovy";
	}

}
