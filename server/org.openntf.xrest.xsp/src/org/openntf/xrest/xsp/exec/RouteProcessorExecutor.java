package org.openntf.xrest.xsp.exec;

import org.openntf.xrest.xsp.model.RouteProcessor;

public interface RouteProcessorExecutor {

	void execute(Context context, RouteProcessor rp);

}