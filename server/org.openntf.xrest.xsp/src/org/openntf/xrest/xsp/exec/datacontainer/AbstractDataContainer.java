package org.openntf.xrest.xsp.exec.datacontainer;

import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import lotus.domino.Database;
import lotus.domino.View;

public abstract class AbstractDataContainer<T> implements DataContainer<T> {

	private Database db;
	private View view;

	public AbstractDataContainer(View view, Database db) {
		this.db = db;
		this.view = view;
	}

	public Database getDatabase() {
		return db;
	}

	public View getView() {
		return view;
	}

	@Override
	public void cleanUp() {
		executeCleanUp();
		NotesObjectRecycler.recycle(view, db);
		view = null;
		db = null;
	}

	protected abstract void executeCleanUp();
}