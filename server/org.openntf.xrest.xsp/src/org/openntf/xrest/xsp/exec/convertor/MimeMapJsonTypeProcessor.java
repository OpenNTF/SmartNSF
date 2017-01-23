package org.openntf.xrest.xsp.exec.convertor;

import java.util.List;

import org.openntf.xrest.xsp.model.MapJsonTypeProcessor;

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

public class MimeMapJsonTypeProcessor implements MapJsonTypeProcessor {

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
