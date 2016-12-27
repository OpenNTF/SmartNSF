package org.openntf.xrest.xsp.exec;

import java.util.List;

public class DataModel<T> {

	private final T data;

	public DataModel(T data) {
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public boolean isList() {
		return data instanceof List;
	}
}
