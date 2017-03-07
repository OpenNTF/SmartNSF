package org.openntf.xrest.xsp.exec.datacontainer;

import java.util.List;

import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.View;

public class DocumentListPaginationDataContainer extends AbstractDataContainer<List<Document>> {

	private final List<Document> documents;
	private final int start;
	private final int count;
	private final long max;

	public DocumentListPaginationDataContainer(final List<Document> docs, final int start, final long max, View view, Database db) {
		super(view, db);
		documents = docs;
		this.start = start;
		this.count = docs.size();
		this.max = max;
	}

	@Override
	public List<Document> getData() {
		return documents;
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
		NotesObjectRecycler.recycle(documents.toArray(new Document[documents.size()]));
	}

}
