package org.openntf.xrest.xsp.exec;

import org.openntf.xrest.xsp.model.EventException;

public class ExecutorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String path;
	private final String phase;
	private final int httpStatusCode;

	public ExecutorException(int httpStatusCode, Exception ex, String path, String phase) {
		super(ex);
		this.path = path;
		this.phase = phase;
		this.httpStatusCode = httpStatusCode;
	}

	public ExecutorException(int httpStatusCode, String message, String path, String phase) {
		super(message);
		this.path = path;
		this.phase = phase;
		this.httpStatusCode = httpStatusCode;
	}

	public ExecutorException(int httpStatusCode, String message, Exception ex, String path, String phase) {
		super(message, ex);
		this.path = path;
		this.phase = phase;
		this.httpStatusCode = httpStatusCode;
	}

	public ExecutorException(EventException ex, String path, String phase) {
		super(ex.getMessage(), ex.getCause());
		this.path = path;
		this.phase = phase;
		this.httpStatusCode = ex.getHttpStatusCode();
	}

	public String getPath() {
		return path;
	}

	public String getPhase() {
		return phase;
	}

	public int getHttpErrorNr() {
		return httpStatusCode;
	}
}
