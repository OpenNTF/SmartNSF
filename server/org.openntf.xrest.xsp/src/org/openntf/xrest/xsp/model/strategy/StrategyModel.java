package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.ExecutorException;

public interface StrategyModel<T> {

	public T getModel(Context context) throws ExecutorException;
}
