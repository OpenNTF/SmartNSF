package org.openntf.xrest.xsp.exec.datacontainer;

import java.util.List;

import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import lotus.domino.Document;

public class DocumentListPaginationDataContainer implements DataContainer<List<Document>> {

	private final List<Document> documents;
	private final int start;
	private final int count;
	private final long max;

	public DocumentListPaginationDataContainer(final List<Document> docs, final int start, final long max) {
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

	@Override
	public void cleanUp() {
		NotesObjectRecycler.recycle(documents.toArray(new Document[documents.size()]));
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

}
