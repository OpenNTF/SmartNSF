package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.Context;

public interface StrategyModel<T> {

	public T getModel(Context context) throws ExecutorException;
	public void cleanUp();
}
