package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import lotus.domino.NotesException;

public interface StrategyModel<T extends DataContainer<?>, R> {

	public R buildResponse(Context context, RouteProcessor rp, DataContainer<?> dc) throws NotesException;
	public T buildDataContainer(Context context) throws ExecutorException;
}
