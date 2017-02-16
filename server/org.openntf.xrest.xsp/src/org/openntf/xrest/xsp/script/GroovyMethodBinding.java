package org.openntf.xrest.xsp.script;

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodNotFoundException;

import com.ibm.xsp.binding.MethodBindingEx;

public class GroovyMethodBinding extends MethodBindingEx {

	private String formula;
	private Class[] params;
	
	public GroovyMethodBinding(String cleanFormula, Class[] params) {
		this.formula = cleanFormula;
		this.params = params;
	}

	@Override
	public Class getType(FacesContext arg0) throws MethodNotFoundException {
		return Object.class;
	}

	@Override
	public Object invoke(FacesContext arg0, Object[] arg1) throws EvaluationException, MethodNotFoundException {
		//TODO: DSL Execution
		//TODO: Inject current session and so
		return null;
	}
	
	

}
