package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.CustomRestHandler;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.NotesException;

public class Custom implements StrategyModel<DataContainer<Object>, JsonObject> {

	private String javaClassValue;

	public void javaClass(String value) {
		javaClassValue = value;
	}

	@Override
	public JsonObject buildResponse(Context context, RouteProcessor rp, DataContainer<?> dc) throws NotesException {
		throw new UnsupportedOperationException("buildReponse is for CUSTOMSTRATEGY not implemented");
	}

	@Override
	public DataContainer<Object> buildDataContainer(Context context) throws ExecutorException {
		throw new UnsupportedOperationException("buildReponse is for CUSTOMSTRATEGY not implemented");
	}

	public CustomRestHandler getCustomRestHandler() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (StringUtil.isEmpty(javaClassValue)) {
			throw new NullPointerException("javaClass should not be null!");
		}
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Class<?> cl = classLoader.loadClass(javaClassValue);
		if (checkClassHasCustomerRestHanlderInterface(cl)) {
			Object executor = cl.newInstance();
			return (CustomRestHandler) executor;
		}
		throw new ClassCastException(javaClassValue + " has to implement " + CustomRestHandler.class.getName());
	}

	private boolean checkClassHasCustomerRestHanlderInterface(Class<?> cl) {
		for (Class<?> checkInterface : cl.getInterfaces()) {
			if (checkInterface.equals(CustomRestHandler.class)) {
				return true;
			}
		}
		Class<?> superClass = cl.getSuperclass();
		if (superClass != null) {
			return checkClassHasCustomerRestHanlderInterface(superClass);
		}
		return false;
	}
}
