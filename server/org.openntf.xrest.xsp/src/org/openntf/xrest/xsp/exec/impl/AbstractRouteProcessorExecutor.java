package org.openntf.xrest.xsp.exec.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DataModel;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.RouteProcessorExecutor;
import org.openntf.xrest.xsp.exec.output.ExecutorExceptionProcessor;
import org.openntf.xrest.xsp.exec.output.JsonPayloadProcessor;
import org.openntf.xrest.xsp.model.EventException;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.MappingField;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;
import com.ibm.xsp.model.domino.wrapped.DominoRichTextItem;
import com.ibm.xsp.util.HtmlUtil;

import groovy.lang.Closure;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.MIMEEntity;
import lotus.domino.MIMEHeader;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.Session;
import lotus.domino.Stream;

public abstract class AbstractRouteProcessorExecutor implements RouteProcessorExecutor {

	private final Context context;
	private final RouteProcessor routeProcessor;
	private final String path;
	private DataModel<?> model;
	private Object resultPayload;

	public AbstractRouteProcessorExecutor(Context context, RouteProcessor routerProcessor, String path) {
		super();
		this.context = context;
		this.routeProcessor = routerProcessor;
		this.path = path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.xrest.xsp.exec.RouteProcessorExecutor#execute(java.lang.
	 * String)
	 */
	@Override
	public void execute() {
		try {
			checkAccess();
			validateRequest();
			preLoadDocument();
			loadDocument();
			postNewDocument();
			postLoadDocument();
			executeMethodeSpecific(this.context, this.model);
			preSubmitValues();
			submitValues();
		} catch (ExecutorException ex) {
			try {
				ExecutorExceptionProcessor.INSTANCE.processExecutorException(ex, context.getResponse());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (JsonException ex) {
			try {
				ExecutorExceptionProcessor.INSTANCE.processGeneralException(500, ex, context.getResponse());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException ex) {
			try {
				ExecutorExceptionProcessor.INSTANCE.processGeneralException(500, ex, context.getResponse());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void checkAccess() throws ExecutorException {
		List<String> allowedUsersAndGroups = routeProcessor.getAccessGroups();
		if (allowedUsersAndGroups == null || allowedUsersAndGroups.isEmpty()) {
			return;
		}
		List<String> myGroups = new ArrayList<String>();
		myGroups.add(context.getUserName());
		myGroups.addAll(context.getGroups());
		myGroups.addAll(context.getRoles());
		for (String me : myGroups) {
			if (allowedUsersAndGroups.contains(me)) {
				return;
			}
		}
		throw new ExecutorException(403, "Access denied for user " + context.getUserName(), path, "checkAccess");
	}

	private void validateRequest() throws ExecutorException {
		try {
			Closure<?> cl = routeProcessor.getEventClosure(EventType.VALIDATE);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context);
			}
		} catch (EventException e) {
			throw new ExecutorException(400, "Validation Error: " + e.getMessage(), e, path, "validation");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, path, "validation");
		}
	}

	private void preLoadDocument() throws ExecutorException {
		try {
			Closure<?> cl = routeProcessor.getEventClosure(EventType.PRE_LOAD_DOCUMENT);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context);
			}
		} catch (EventException e) {
			throw new ExecutorException(400, "Pre Load Error: " + e.getMessage(), e, path, "preloadmodel");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, path, "preloadmodel");
		}
	}

	private void loadDocument() throws ExecutorException {
		model = routeProcessor.getDataModel(context);
	}

	private void postNewDocument() throws ExecutorException {
		if (model.isList()) {
			return;
		}
		try {
			Document doc = (Document) model.getData();
			Closure<?> cl = routeProcessor.getEventClosure(EventType.POST_NEW);
			if (cl != null && doc.isNewNote()) {
				DSLBuilder.callClosure(cl, context, model);
			}
		} catch (EventException e) {
			throw new ExecutorException(400, "Post Load Error: " + e.getMessage(), e, path, "postloadmodel");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, path, "postloadmodel");
		}
	}

	
	private void postLoadDocument() throws ExecutorException {
		try {
			Closure<?> cl = routeProcessor.getEventClosure(EventType.POST_LOAD_DOCUMENT);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context, model);
			}
		} catch (EventException e) {
			throw new ExecutorException(400, "Post Load Error: " + e.getMessage(), e, path, "postloadmodel");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, path, "postloadmodel");
		}
	}


	private void preSubmitValues() throws ExecutorException {
		try {
			Closure<?> cl = routeProcessor.getEventClosure(EventType.PRE_SUBMIT);
			if (cl != null) {
				DSLBuilder.callClosure(cl, context, model);
			}
		} catch (EventException e) {
			throw new ExecutorException(400, "Post Load Error: " + e.getMessage(), e, path, "presubmit");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " + e.getMessage(), e, path, "presubmit");
		}
		model.cleanUp();
		routeProcessor.cleanUp();

	}

	private void submitValues() throws IOException, JsonException {
		JsonPayloadProcessor.INSTANCE.processJsonPayload(resultPayload, context.getResponse());
	}

	abstract protected void executeMethodeSpecific(Context context, DataModel<?> model) throws ExecutorException;

	public void setResultPayload(Object rp) {
		resultPayload = rp;
	}

	public void setModel(DataModel<?> model) {
		this.model = model;
	}

	protected JsonObject buildJsonFromDocument(Document doc) throws NotesException {
		JsonObject jo = new JsonJavaObject();
		@SuppressWarnings("unchecked")
		Vector<Item> documentItems = doc.getItems();
		Map<String, MappingField> fieldDefinition = routeProcessor.getMappingFields();
		for (Item item : documentItems) {
			if (fieldDefinition.containsKey(item.getName().toLowerCase())) {
				processItem(jo, item, fieldDefinition.get(item.getName().toLowerCase()));
			}
		}
		return jo;
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
	
	protected RouteProcessor getRouteProcessor() {
		return routeProcessor;
	}
	
	protected String getPath() {
		return path;
	}
}