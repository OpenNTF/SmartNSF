package org.openntf.xrest.xsp.exec.datacontainer;

import java.util.List;

import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import lotus.domino.Document;

public class DocumentListDataContainer implements DataContainer<List<Document>> {

	private final List<Document> documents;

	public DocumentListDataContainer(List<Document> docs) {
		documents = docs;
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

}
