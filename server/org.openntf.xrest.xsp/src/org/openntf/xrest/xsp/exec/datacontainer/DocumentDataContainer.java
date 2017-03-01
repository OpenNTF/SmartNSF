package org.openntf.xrest.xsp.exec.datacontainer;

import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import lotus.domino.Document;

public class DocumentDataContainer implements DataContainer<Document> {

	public final Document document;

	public DocumentDataContainer(Document doc) {
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
	public void cleanUp() {
		NotesObjectRecycler.recycle(document);
	}

}
