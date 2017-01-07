package org.openntf.xrest.xsp.exec.impl;

import org.openntf.xrest.xsp.exec.DataModel;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.model.RouteProcessor;

public class POSTRouteProcessorExecutor extends AbstractRouteProcessorExecutor {

	public POSTRouteProcessorExecutor(Context context, RouteProcessor routerProcessor, String path) {
		super(context, routerProcessor, path);
	}

	@Override
	protected void executeMethodeSpecific(Context context, DataModel<?> model) {
		// TODO Auto-generated method stub

	}

}
