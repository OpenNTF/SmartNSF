package org.openntf.xrest.xsp.model;

public class EventException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
		// TODO Auto-generated constructor stub
	}

	public EventException(String message) {
		super(message);
	}

	public EventException(Throwable cause) {
		super(cause);
	}

	public EventException(String message, Throwable cause) {
		super(message, cause);
	}

}
