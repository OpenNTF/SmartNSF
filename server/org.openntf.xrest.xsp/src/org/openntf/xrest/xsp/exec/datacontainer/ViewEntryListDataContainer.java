package org.openntf.xrest.xsp.exec.datacontainer;

import java.util.List;

import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import lotus.domino.Database;
import lotus.domino.View;

public class ViewEntryListDataContainer extends AbstractDataContainer<List<List<Object>>> {

	private final List<List<Object>> entries;

	public ViewEntryListDataContainer(final List<List<Object>> viewEntries, final View view, final Database db) {
		super(view, db);
		entries = viewEntries;
	}

	@Override
	public List<List<Object>> getData() {
		return entries;
	}

	@Override
	public boolean isList() {
		return true;
	}

	@Override
	public boolean isBinary() {
		return false;
	}

	@Override
	protected void executeCleanUp() {
		if (entries == null) {
			return;
		}
		for(List<Object> columns: entries) {
			NotesObjectRecycler.recycleObjects(columns);
		}
	}

}
