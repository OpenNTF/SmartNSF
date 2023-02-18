package org.openntf.xrest.xsp.exec.convertor;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.datacontainer.DocumentListDataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Document;
import lotus.domino.NotesException;

public class DocumentList2JsonConverter {

	private final DocumentListDataContainer container;
	private final RouteProcessor routeProcessor;
	private final Context context;
	public DocumentList2JsonConverter(DocumentListDataContainer dlContainer , RouteProcessor routeProcessor, Context context) {
		this.container = dlContainer;
		this.routeProcessor =routeProcessor;
		this.context = context;
	}

	public JsonJavaArray buildJsonFromDocument() throws NotesException {
		JsonJavaArray jsa = new JsonJavaArray();
		Document2JsonConverter d2jc = new Document2JsonConverter(routeProcessor, context);
		
		for (Document doc : container.getData()) {
			JsonObject jso =  d2jc.buildJsonFromDocument(doc);
			jsa.add(jso);
		}
		return jsa;
	}

}
