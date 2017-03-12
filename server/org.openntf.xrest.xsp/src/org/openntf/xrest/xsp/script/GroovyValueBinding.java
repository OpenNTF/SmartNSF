package org.openntf.xrest.xsp.script;

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;

import com.ibm.xsp.binding.ValueBindingEx;
import com.ibm.xsp.exception.EvaluationExceptionEx;

public class GroovyValueBinding extends ValueBindingEx {

	private String formula;

	public GroovyValueBinding() {
	}

	public GroovyValueBinding(final String formula) {
		this.formula = formula;
	}

	@Override
	public Class getType(final FacesContext facesContext) throws EvaluationException, PropertyNotFoundException {
		return super.getExpectedType();
	}

	@Override
	public Object getValue(final FacesContext facesContext) throws EvaluationException, PropertyNotFoundException {

		// TODO Execute DSLBuilder
		// Force the type based on getExpectedType
		// Converters is used after.
		Object result = null;
		return convertToExpectedType(facesContext, result);
	}

	@Override
	public boolean isReadOnly(final FacesContext facesContext) throws EvaluationException, PropertyNotFoundException {
		return true;
	}

	@Override
	public void setValue(final FacesContext facesContext, final Object arg1) throws EvaluationException, PropertyNotFoundException {
		throw new EvaluationExceptionEx("Not implemented", this);
	}

	@Override
	public Object saveState(final FacesContext arg0) {
		Object[] state = new Object[2];
		state[0] = super.saveState(arg0);
		state[1] = formula;
		return state;
	}

	@Override
	public void restoreState(final FacesContext facesContext, final Object state) {
		Object[] stateObject = (Object[]) state;
		super.restoreState(facesContext, stateObject[0]);
		formula = (String) stateObject[1];
	}

	@Override
	public String getExpressionString() {
		return "#{groovy:" + formula + "}";
	}
}
