package org.openntf.xrest.xsp.exec.convertor;

import java.util.Map;
import java.util.Vector;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.model.MappingField;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;
import com.ibm.xsp.model.domino.wrapped.DominoRichTextItem;
import com.ibm.xsp.util.HtmlUtil;

import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.MIMEEntity;
import lotus.domino.MIMEHeader;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.Session;
import lotus.domino.Stream;

public class Document2JsonConverter {
	
	private final Context context;
	private final RouteProcessor routeProcessor;
	private final Document doc;
	
	public Document2JsonConverter(Document doc, RouteProcessor routeProcessor, Context context) {
		this.context = context;
		this.routeProcessor = routeProcessor;
		this.doc = doc;
	}
	
	public JsonObject buildJsonFromDocument() throws NotesException {
		JsonObject jo = new JsonJavaObject();
		@SuppressWarnings("unchecked")
		Vector<Item> documentItems = doc.getItems();
		Map<String, MappingField> fieldDefinition = routeProcessor.getMappingFields();
		for (Item item : documentItems) {
			if (fieldDefinition.containsKey(item.getName().toLowerCase())) {
				processItem(jo, item, fieldDefinition.get(item.getName().toLowerCase()));
			}
		}
		for (MappingField field : routeProcessor.getFormulaFields()) {
			processFormulaToJson(jo, field, doc);
		}
		return jo;
	}

	private void processFormulaToJson(JsonObject jo, MappingField field, Document doc) throws NotesException {
		Vector<?> result = context.getSession().evaluate(field.getFormula(), doc);
		if (result.size() == 1) {
			jo.putJsonProperty(field.getJsonName(), result.get(0));
		} else {
			jo.putJsonProperty(field.getJsonName(), result);
		}
	}

	private void processItem(JsonObject jo, Item item, MappingField mappingField) throws NotesException {
		switch (item.getType()) {
		case Item.OTHEROBJECT:
		case Item.ATTACHMENT:
		case Item.NOTELINKS:
		case Item.SIGNATURE:
			break;
		case Item.MIME_PART:
		case Item.RICHTEXT:
			handleRTMimeItem(jo, item, mappingField);
			break;
		default:
			handleMainTypeItem(jo, item, mappingField);
		}
	}

	private void handleMainTypeItem(JsonObject jo, Item item, MappingField mappingField) throws NotesException {
		if (item.getValues().size() == 1) {
			jo.putJsonProperty(mappingField.getJsonName(), item.getValues().get(0));
		} else {
			jo.putJsonProperty(mappingField.getJsonName(), item.getValues());
		}
	}

	private void handleRTMimeItem(JsonObject jo, Item item, MappingField mappingField) {
		try {
			String fieldName = item.getName();
			Document doc = item.getParent();
			MIMEEntity entity = doc.getMIMEEntity(fieldName);
			if (entity != null) {
				String content = getContentFromMime(entity, context.getSession());
				jo.putJsonProperty(mappingField.getJsonName(), content);
				entity.recycle();
			} else {
				if (item.getType() != Item.RICHTEXT) {
					jo.putJsonProperty(mappingField.getJsonName(), item.getValueString());
				} else {
					RichTextItem rti = (RichTextItem) item;
					DominoDocument dd = new DominoDocument();
					dd.setDocument(doc);
					DominoRichTextItem drtCurrent = new DominoRichTextItem(dd, rti);
					String value = drtCurrent.getHTML();
					jo.putJsonProperty(mappingField.getJsonName(), value);
				}
			}
		} catch (Exception e) {
		} finally {
		}

	}

	private String getContentFromMime(MIMEEntity entity, Session parent) throws NotesException {
		String content;
		content = extractMimeText(entity, "text/html", parent);
		if (content == null) {
			content = extractMimeText(entity, "text/plain", parent);
			content = HtmlUtil.toHTMLContentString(content, true, HtmlUtil.useHTML);
		}
		if (content == null) {
			content = extractMimeText(entity, null, parent);
		}
		return content;
	}

	private String extractMimeText(MIMEEntity entity, String mimeType, Session sesCurrent) throws NotesException {
		String content = null;
		MIMEHeader mimeContentType = entity.getNthHeader("Content-Type");
		MIMEHeader mimeDispostion = entity.getNthHeader("Content-Disposition");
		if ((mimeContentType != null) && (mimeDispostion == null)) {
			String headerValue = mimeContentType.getHeaderVal();
			if (headerValue.startsWith("multipart")) {
				MIMEEntity childNext = entity.getFirstChildEntity();
				while ((childNext != null) && (content == null)) {
					MIMEEntity child = childNext;
					childNext = child.getNextSibling();
					content = extractMimeText(child, mimeType, sesCurrent);
					child.recycle();
				}
			} else if ((mimeType != null) && (headerValue.startsWith(mimeType))) {
				content = getContentsAsText(entity, sesCurrent);
			}
			mimeContentType.recycle();
		} else if ((mimeType == null) && (mimeDispostion == null)) {
			content = getContentsAsText(entity, sesCurrent);
		}

		return content;
	}

	private String getContentsAsText(MIMEEntity child, Session sesCurrent) throws NotesException {
		Stream stream = sesCurrent.createStream();
		child.getContentAsText(stream, true);
		stream.setPosition(0);
		String str = stream.readText();
		stream.recycle();
		return str;
	}
}
