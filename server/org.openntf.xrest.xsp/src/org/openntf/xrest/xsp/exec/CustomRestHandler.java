package org.openntf.xrest.xsp.exec;

public interface CustomRestHandler {

	public void processCall(Context context, String path) throws Exception;

}
