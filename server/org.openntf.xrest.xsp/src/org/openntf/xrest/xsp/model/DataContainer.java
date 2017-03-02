package org.openntf.xrest.xsp.model;

public interface DataContainer<T> {

	public T getData();
	public boolean isList();
	public boolean isBinary();
	public void cleanUp();
}
