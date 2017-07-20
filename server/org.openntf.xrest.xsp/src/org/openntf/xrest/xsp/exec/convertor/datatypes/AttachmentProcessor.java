package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.io.File;
import java.util.Vector;

import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.Item;
import lotus.domino.MIMEEntity;
import lotus.domino.MIMEHeader;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.Session;

public class AttachmentProcessor extends MimeMapJsonTypeProcessor {

	private static final AttachmentProcessor processor = new AttachmentProcessor();

	public static final AttachmentProcessor getInstance() {
		return processor;
	}

	public void addAttachment(final Document doc, final String fieldName, final String filePath, final String fileName)
			throws NotesException {
		Item notesItem = doc.getFirstItem(fieldName);

		if (notesItem != null && notesItem.getType() == Item.RICHTEXT) {
			addFileToRT((RichTextItem) notesItem, filePath, fileName);
		} else {
			MIMEEntity entity = doc.getMIMEEntity(fieldName);
			if (entity == null) {
				doc.removeItem(fieldName);
				entity = doc.createMIMEEntity(fieldName);
			} else {
				MIMEHeader mimeHeader = entity.getNthHeader(CONTENT_TYPE);
				if (mimeHeader == null || !MULTIPART_MIXED.equals(mimeHeader.getHeaderVal())) {
					entity = entity.createParentEntity();
				}
			}
			checkMulitPartMixedHeaders(entity);
			Session ses = doc.getParentDatabase().getParent();
			addFileToMime(entity, ses, filePath, fileName);
		}
	}

	private void addFileToMime(final MIMEEntity entity, final Session ses, final String filePath, final String fileName)
			throws NotesException {
		processSingleAttachment2Mime(entity, ses, fileName, filePath);
	}

	private void addFileToRT(final RichTextItem notesItem, final String serverFilePath, final String fileName) throws NotesException {
		File serverFile = new File(serverFilePath);
		File fileNew = new File(serverFile.getParentFile().getAbsolutePath() + File.separator + fileName);
		try {
			serverFile.renameTo(fileNew);
			notesItem.embedObject(EmbeddedObject.EMBED_ATTACHMENT, null, fileNew.getAbsolutePath(), fileName);
		} finally {
			fileNew.renameTo(serverFile);
		}
	}

	public void deleteAttachment(final Document doc, final String fieldName, final String fileName) {

	}

	public boolean isMime(final Document doc, final String fieldName) throws NotesException {
		Item notesItem = doc.getFirstItem(fieldName);
		try {
			if (notesItem != null && notesItem.getType() == Item.RICHTEXT) {
				return false;
			} else {
				return true;
			}
		} finally {
			NotesObjectRecycler.recycle(notesItem);
		}

	}

	public MIMEEntity getMimeAttachment(final Document doc, final String fieldName, final String fileName) throws NotesException {
		MIMEEntity entity = doc.getMIMEEntity(fieldName);
		if (entity == null) {
			return null;
		}
		return findAttachment(entity, fileName);
	}

	public EmbeddedObject getEmbeddedObjectAttachment(final Document doc, final String fieldName, final String fileName)
			throws NotesException {
		EmbeddedObject embo = null;
		Item notesItem = doc.getFirstItem(fieldName);
		try {
			@SuppressWarnings("unchecked")
			Vector<EmbeddedObject> allEmbeddedObject = ((RichTextItem) notesItem).getEmbeddedObjects();
			for (EmbeddedObject emb : allEmbeddedObject) {
				String name = emb.getName();
				if (name.equals(fileName)) {
					embo = emb;
				} else {
					emb.recycle();
				}
			}
		} finally {
			NotesObjectRecycler.recycle(notesItem);
		}
		return embo;
	}
}
