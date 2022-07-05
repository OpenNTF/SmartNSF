package org.openntf.xrest.xsp.exec.convertor;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.datacontainer.DocumentListPaginationDataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Document;
import lotus.domino.NotesException;

public class DocumentListPaged2JsonConverter {
	private final DocumentListPaginationDataContainer container;
	private final RouteProcessor routeProcessor;
	private final Context context;

	public DocumentListPaged2JsonConverter(DocumentListPaginationDataContainer docListDC, RouteProcessor routeProcessor, Context context) {
		this.container = docListDC;
		this.routeProcessor = routeProcessor;
		this.context = context;
	}

	public JsonObject buildJsonFromDocument() throws NotesException {
		JsonObject jso = new JsonJavaObject();
		jso.putJsonProperty("start", container.getStart());
		jso.putJsonProperty("count", container.getCount());
		jso.putJsonProperty("total", container.getMax());
		JsonJavaArray jsa = new JsonJavaArray();
		Document2JsonConverter d2jc = new Document2JsonConverter( routeProcessor, context);
		for (Document doc : container.getData()) {
			JsonObject jsoEntrie =  d2jc.buildJsonFromDocument(doc);
			jsa.add(jsoEntrie);
		}
		jso.putJsonProperty("entries", jsa);
		return jso;
	}

}
