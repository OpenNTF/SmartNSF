package org.openntf.xrest.xsp.model;

import lotus.domino.Database;

public interface DataContainer<T> {

	public T getData();
	public boolean isList();
	public boolean isBinary();
	public void cleanUp();
	public Database getDatabase();
}
