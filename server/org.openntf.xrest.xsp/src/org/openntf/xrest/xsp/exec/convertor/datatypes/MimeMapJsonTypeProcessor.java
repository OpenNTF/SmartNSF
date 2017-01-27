package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.util.List;

import org.openntf.xrest.xsp.model.MappingField;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;
import com.ibm.xsp.model.domino.wrapped.DominoRichTextItem;

import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.MIMEEntity;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.Session;
import lotus.domino.Stream;
import lotus.domino.MIMEHeader;

public class MimeMapJsonTypeProcessor extends AbstractMapJsonTypeProcessor {

	private static final String CONTENT_TYPE = "Content-Type";
	private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=UTF-8";
	private static final String CHARSET_UTF_8 = "charset=UTF-8";

	@Override
	public void processItemToJsonObject(Item item, JsonObject jo, String jsonPropertyName) throws NotesException {
		Document doc = item.getParent();
		Session session = doc.getParentDatabase().getParent();
		String fieldName = item.getName();
		MIMEEntity entity = doc.getMIMEEntity(fieldName);
		if (entity != null) {
			String content = getContentFromMime(entity, session);
			jo.putJsonProperty(jsonPropertyName, content);
			entity.recycle();
		} else {
			if (item.getType() != Item.RICHTEXT) {
				jo.putJsonProperty(jsonPropertyName, item.getValueString());
			} else {
				RichTextItem rti = (RichTextItem) item;
				DominoDocument dd = new DominoDocument();
				dd.setDocument(doc);
				DominoRichTextItem drtCurrent = new DominoRichTextItem(dd, rti);
				String value = drtCurrent.getHTML();
				jo.putJsonProperty(jsonPropertyName, value);
			}
		}
	}

	@Override
	public void processValuesToJsonObject(List<?> values, JsonObject jo, String jsonProperty) throws NotesException {
		// TODO Auto-generated method stub

	}

	private String getContentFromMime(MIMEEntity entity, Session parent) throws NotesException {
		String content = "";

		MIMEEntity contentEntity = findContent(entity, "text/html");
		if (contentEntity != null) {
			System.out.println("HTML FOUND!");
			content = extractContentsAsText(contentEntity, parent);
		} else {
			contentEntity = findContent(entity, "text/plain");
			if (contentEntity != null) {
				content = extractContentsAsText(contentEntity, parent);
			}
		}
		return content;
	}

	private String extractContentsAsText(MIMEEntity child, Session sesCurrent) throws NotesException {
		Stream stream = sesCurrent.createStream();
		child.getContentAsText(stream, true);
		stream.setPosition(0);
		String str = stream.readText();
		stream.recycle();
		return str;
	}

	@Override
	public void processJsonValueToDocument(JsonJavaObject jso, Document doc, MappingField mfField) throws NotesException {
		String fieldName = mfField.getNotesFieldName();
		String value = jso.getAsString(mfField.getJsonName());
		Stream stream = doc.getParentDatabase().getParent().createStream();
		stream.writeText(value);
		Item notesItem = doc.getFirstItem(fieldName);
		if (notesItem.getType() == Item.RICHTEXT) {
			System.out.println("NOOOOO ITS RICHTEXT!!!!!");
		} else {
			MIMEEntity entity = doc.getMIMEEntity(fieldName);
			MIMEEntity htmlEntity;
			if (entity == null) {
				doc.removeItem(fieldName);
				entity = doc.createMIMEEntity(fieldName);
				htmlEntity = entity;
			} else {
				htmlEntity = findContent(entity, "text/html");
				if (htmlEntity == null) {
					htmlEntity = entity.createChildEntity();
				}
			}
			checkHTMLEntityHeaders(htmlEntity);
			stream.setPosition(0);
			htmlEntity.setContentFromText(stream, TEXT_HTML_CHARSET_UTF_8, MIMEEntity.ENC_NONE);
			stream.close();
		}
	}

	private void checkHTMLEntityHeaders(MIMEEntity htmlEntity) throws NotesException {

		MIMEHeader localMIMEHeader = htmlEntity.getNthHeader(CONTENT_TYPE);
		if (localMIMEHeader == null) {
			localMIMEHeader = htmlEntity.createHeader(CONTENT_TYPE);
			localMIMEHeader.setHeaderValAndParams(TEXT_HTML_CHARSET_UTF_8);
		} else {
			localMIMEHeader.setParamVal("charset", CHARSET_UTF_8);
		}
	}

	private MIMEEntity findContent(MIMEEntity entity, String mimeType) throws NotesException {
		String contentType = getContentHeaderValue(entity);
		if (contentType.startsWith(mimeType)) {
			return entity;
		}
		if (contentType.startsWith("multipart")) {
			MIMEEntity childNext = entity.getFirstChildEntity();
			while (childNext != null) {
				MIMEEntity childCurrent = childNext;
				childNext = childNext.getNextSibling();
				MIMEEntity matcher = findContent(childCurrent, mimeType);
				if (matcher != null) {
					childNext.recycle();
					return matcher;
				}
				childCurrent.recycle();
			}
		}
		return null;
	}

	private String getContentHeaderValue(MIMEEntity entity) throws NotesException {
		MIMEHeader mimeHeader = entity.getNthHeader(CONTENT_TYPE);
		if (mimeHeader != null) {
			String contenType = mimeHeader.getHeaderVal();
			mimeHeader.recycle();
			return contenType;
		}
		return "";

	}
}
