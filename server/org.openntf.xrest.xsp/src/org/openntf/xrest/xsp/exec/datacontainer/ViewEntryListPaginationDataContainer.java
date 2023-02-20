package org.openntf.xrest.xsp.exec.datacontainer;

import java.util.List;

import lotus.domino.Database;
import lotus.domino.View;

public class ViewEntryListPaginationDataContainer extends AbstractDataContainer<List<List<Object>>> {

	private final List<List<Object>> entries;
	private final int start;
	private final int count;
	private final long max;

	public ViewEntryListPaginationDataContainer(final List<List<Object>> viewEntries, final int start, final long max,
			final View view, final Database db) {
		super(view, db);
		entries = viewEntries;
		this.start = start;
		this.count = viewEntries.size();
		this.max = max;
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

	public int getStart() {
		return start;
	}

	public int getCount() {
		return count;
	}

	public long getMax() {
		return max;
	}

	@Override
	protected void executeCleanUp() {

	}
}
