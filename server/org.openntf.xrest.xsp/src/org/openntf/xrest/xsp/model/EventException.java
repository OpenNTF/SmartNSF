package org.openntf.xrest.xsp.model;

public class EventException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final int httpStatusCode;

	public static EventException error(String message) {
		return new EventException(message);
	}

	public static EventException error(String message, Throwable cause) {
		return new EventException(message, cause);
	}

	public static EventException error(Throwable cause) {
		return new EventException(cause);
	}

	public EventException() {
		httpStatusCode = 400;
	}

	public EventException(String message) {
		super(message);
		httpStatusCode = 400;
	}

	public EventException(Throwable cause) {
		super(cause);
		httpStatusCode = 400;
	}

	public EventException(String message, Throwable cause) {
		super(message, cause);
		httpStatusCode = 400;
	}

	public EventException(int httpStatus, String message) {
		super(message);
		httpStatusCode = httpStatus;
	}

	public EventException(int httpStatus, String message, Throwable cause) {
		super(message, cause);
		httpStatusCode = 400;
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}
}
