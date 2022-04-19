package org.openntf.xrest.xsp.exec.datacontainer;

import java.util.List;

import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.View;

public class DocumentListDataContainer extends AbstractDataContainer<List<Document>> {

	private final List<Document> documents;

	public DocumentListDataContainer(List<Document> docs, View view, Database db) {
		super(view,db);
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
	protected void executeCleanUp() {
		NotesObjectRecycler.recycleList(documents);
		documents.clear();
	}

}
