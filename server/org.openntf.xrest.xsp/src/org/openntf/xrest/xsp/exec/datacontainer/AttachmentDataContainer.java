package org.openntf.xrest.xsp.exec.datacontainer;

import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import lotus.domino.Base;
import lotus.domino.Database;
import lotus.domino.Item;
import lotus.domino.MIMEEntity;

public class AttachmentDataContainer<T extends Base> implements DataContainer<T> {
	private final DocumentDataContainer documentDataContainer;

	private final T attachmentMime;
	private final String fieldName;
	private final String fileName;
	private final Item notesItem;

	public AttachmentDataContainer(DocumentDataContainer documentDataContainer, T attachmentObject, String fieldName, String fileName, Item item) {
		this.documentDataContainer = documentDataContainer;
		this.attachmentMime = attachmentObject;
		this.fieldName = fieldName;
		this.fileName = fileName;
		this.notesItem = item;
	}

	@Override
	public T getData() {
		return attachmentMime;
	}

	@Override
	public boolean isList() {
		return false;
	}

	@Override
	public boolean isBinary() {
		return true;
	}

	@Override
	public void cleanUp() {
		NotesObjectRecycler.recycle(attachmentMime, notesItem);
		documentDataContainer.cleanUp();
	}

	public boolean isMime() {
		return attachmentMime instanceof MIMEEntity;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFieldName() {
		return fieldName;
	}
	public DocumentDataContainer getDocumentDataContainer() {
		return documentDataContainer;
	}

	@Override
	public Database getDatabase() {
		return documentDataContainer.getDatabase();
	}
}
