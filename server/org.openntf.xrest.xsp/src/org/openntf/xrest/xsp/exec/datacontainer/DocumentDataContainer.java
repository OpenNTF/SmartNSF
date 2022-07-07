package org.openntf.xrest.xsp.exec.datacontainer;

import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.View;

public class DocumentDataContainer extends AbstractDataContainer<Document> {

	public Document document;

	public DocumentDataContainer(Document doc, View view, Database db) {
		super(view, db);
		document = doc;
	}

	@Override
	public Document getData() {
		return document;
	}

	@Override
	public boolean isList() {
		return false;
	}

	@Override
	public boolean isBinary() {
		return false;
	}

	@Override
	protected void executeCleanUp() {
		NotesObjectRecycler.recycle(document);
		document = null;
	}

}
