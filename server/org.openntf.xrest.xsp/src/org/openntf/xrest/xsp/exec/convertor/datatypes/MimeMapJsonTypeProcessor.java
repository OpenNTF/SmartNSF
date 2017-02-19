package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.mail.internet.MimeUtility;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.openntf.xrest.xsp.model.MappingField;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.StreamUtil;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.designer.runtime.domino.adapter.mime.MIME;
import com.ibm.xsp.model.domino.DominoUtils;

import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.Item;
import lotus.domino.MIMEEntity;
import lotus.domino.MIMEHeader;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.Session;
import lotus.domino.Stream;

public class MimeMapJsonTypeProcessor extends AbstractMapJsonTypeProcessor {

	private static final String ATTACHMENT_HEADER_VALUE = "attachment";
	private static final String BINARY_HEADER_VALUE = "binary";
	private static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String MULTIPART_MIXED = "multipart/mixed";
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
				String value = getContentFormRT(doc, item.getName());
				jo.putJsonProperty(jsonPropertyName, value);
			}
		}
	}

	private String getContentFormRT(Document doc, String fieldName) throws NotesException {
		DominoUtils.HtmlConverterWrapper converter = null;
		String htmlContent;
		try {
			converter = new DominoUtils.HtmlConverterWrapper();
			converter.convertItem(doc, fieldName);
			htmlContent = converter.getConverterText();
			Vector<String> attachments = converter.getReferneceUrls();
			for (String att : attachments) {
				System.out.println("Attachment: " + att);
			}
		} finally {
			if (converter != null) {
				converter.recycle();
			}
		}
		return htmlContent;
	}

	@Override
	public void processValuesToJsonObject(List<?> values, JsonObject jo, String jsonProperty) throws NotesException {
		// TODO Auto-generated method stub

	}

	private String getContentFromMime(MIMEEntity entity, Session parent) throws NotesException {
		String content = "";

		MIMEEntity contentEntity = findContent(entity, "text/html");
		if (contentEntity != null) {
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
		if (notesItem != null && notesItem.getType() == Item.RICHTEXT) {
			updateAndConvertRTItemToMime((RichTextItem) notesItem, stream);
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
			addContentToHTMLEntity(stream, htmlEntity);
			doc.closeMIMEEntities(true, fieldName);
		}
	}

	private void addContentToHTMLEntity(Stream stream, MIMEEntity htmlEntity) throws NotesException {
		stream.setPosition(0);
		htmlEntity.setContentFromText(stream, TEXT_HTML_CHARSET_UTF_8, MIMEEntity.ENC_NONE);
		stream.close();
	}

	private void updateAndConvertRTItemToMime(RichTextItem notesItem, Stream stream) throws NotesException {
		File tempDir = buildTempDir();
		Document doc = notesItem.getParent();
		Session session = doc.getParentDatabase().getParent();
		String fieldName = notesItem.getName();
		try {
			List<String> fileNames = extractAllAttachments(notesItem, tempDir.getAbsolutePath());
			doc.removeItem(fieldName);
			MIMEEntity baseEntity = doc.createMIMEEntity(fieldName);
			MIMEEntity htmlEntity;

			if (fileNames.isEmpty()) {
				htmlEntity = baseEntity;
			} else {
				checkMulitPartMixedHeaders(baseEntity);
				htmlEntity = baseEntity.createChildEntity();
			}
			checkHTMLEntityHeaders(htmlEntity);
			addContentToHTMLEntity(stream, htmlEntity);
			processAttachments2Mime(fileNames, baseEntity, tempDir.getAbsolutePath(), session);
			doc.closeMIMEEntities(true, fieldName);
		} finally {
			try {
				FileUtils.deleteDirectory(tempDir);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void processAttachments2Mime(List<String> fileNames, MIMEEntity baseEntity, String absolutePath, Session ses) throws NotesException {
		for (String fileName : fileNames) {
			String mimeType = mimeTypeOfFile(fileName);
			Stream stream = ses.createStream();
			stream.open(absolutePath + File.separator + fileName, BINARY_HEADER_VALUE);
			MIMEEntity mimeAttachment = baseEntity.createChildEntity();
			mimeAttachment.setContentFromBytes(stream, mimeType, MIMEEntity.ENC_IDENTITY_BINARY);
			MIMEHeader mimeHeader = applyMimeHeaderTypeAndValue(mimeAttachment, CONTENT_TYPE, mimeType);
			String attachmentValue = null;
			try {
				attachmentValue = MimeUtility.encodeText(fileName, "utf-8", "B");
			} catch (UnsupportedEncodingException ex) {
				attachmentValue = fileName;
			}
			mimeHeader.setParamVal("name", attachmentValue);

			MIMEHeader contentDispHeader = applyMimeHeaderTypeAndValue(mimeAttachment, CONTENT_TYPE, ATTACHMENT_HEADER_VALUE);
			contentDispHeader.setParamVal("filename", attachmentValue);

			applyMimeHeaderTypeAndValue(mimeAttachment, CONTENT_TRANSFER_ENCODING, BINARY_HEADER_VALUE);
			stream.close();
			stream.recycle();
		}
	}

	private File buildTempDir() {
		String uuid = UUID.randomUUID().toString();
		String tempFolder = System.getProperty("java.io.tmpdir");
		File dir = new File(tempFolder + "/xrest/" + uuid);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		return dir;
	}

	private String mimeTypeOfFile(String fileName) {
		String extension = FilenameUtils.getExtension(fileName);
		if (extension != null) {
			String mimeType = MIME.getMIMETypeFromExtension(extension);
			if (StringUtil.isNotEmpty(mimeType)) {
				return mimeType;
			}
		}
		return "application/octet-stream";
	}

	@SuppressWarnings("unchecked")
	private List<String> extractAllAttachments(RichTextItem notesItem, String tempDir) throws NotesException {
		Vector<Object> embObjects = notesItem.getEmbeddedObjects();
		if (embObjects == null || embObjects.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> attachmentNames = new ArrayList<String>();
		for (Object emb : embObjects) {
			EmbeddedObject embObject = (EmbeddedObject) emb;
			if (embObject.getType() == EmbeddedObject.EMBED_ATTACHMENT) {
				InputStream attachmentStream = embObject.getInputStream();
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(tempDir + File.separator + embObject.getName());
					StreamUtil.copyStream(attachmentStream, fos);
					attachmentNames.add(embObject.getName());
					embObject.recycle();
				} catch (Exception ex) {

				} finally {
					StreamUtil.close(fos);
					StreamUtil.close(attachmentStream);
				}
			}
		}
		return attachmentNames;
	}

	private void checkHTMLEntityHeaders(MIMEEntity htmlEntity) throws NotesException {

		MIMEHeader mimeHeader = htmlEntity.getNthHeader(CONTENT_TYPE);
		if (mimeHeader == null) {
			mimeHeader = htmlEntity.createHeader(CONTENT_TYPE);
			mimeHeader.setHeaderValAndParams(TEXT_HTML_CHARSET_UTF_8);
		} else {
			mimeHeader.setParamVal("charset", CHARSET_UTF_8);
		}
	}

	private void checkMulitPartMixedHeaders(MIMEEntity baseEntity) throws NotesException {
		MIMEHeader mimeHeader = baseEntity.getNthHeader(CONTENT_TYPE);
		if (mimeHeader == null) {
			mimeHeader = baseEntity.createHeader(CONTENT_TYPE);
		}
		mimeHeader.setHeaderVal(MULTIPART_MIXED);

	}

	private MIMEHeader applyMimeHeaderTypeAndValue(MIMEEntity entity, String type, String value) throws NotesException {
		MIMEHeader mimeHeader = entity.getNthHeader(type);
		if (mimeHeader == null) {
			mimeHeader = entity.createHeader(type);
		}
		mimeHeader.setHeaderVal(value);
		return mimeHeader;

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
