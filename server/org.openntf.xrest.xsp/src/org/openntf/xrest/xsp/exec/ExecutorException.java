package org.openntf.xrest.xsp.exec;

public class ExecutorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String path;
	private final String phase;
	private final int httpErrorNr;

	public ExecutorException(int httpErrorNr, Exception ex, String path, String phase) {
		super(ex);
		this.path = path;
		this.phase = phase;
		this.httpErrorNr = httpErrorNr;
	}

	public ExecutorException(int httpErrorNr, String message, String path, String phase) {
		super(message);
		this.path = path;
		this.phase = phase;
		this.httpErrorNr = httpErrorNr;
	}

	public ExecutorException(int httpErrorNr, String message, Exception ex, String path, String phase) {
		super(message, ex);
		this.path = path;
		this.phase = phase;
		this.httpErrorNr = httpErrorNr;
	}

	public String getPath() {
		return path;
	}

	public String getPhase() {
		return phase;
	}

	public int getHttpErrorNr() {
		return httpErrorNr;
	}
}
