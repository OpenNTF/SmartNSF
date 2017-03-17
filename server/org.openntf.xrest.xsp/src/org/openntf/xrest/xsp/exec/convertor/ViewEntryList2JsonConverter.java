package org.openntf.xrest.xsp.exec.convertor;

import java.util.List;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.datacontainer.ViewEntryListDataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.NotesException;

public class ViewEntryList2JsonConverter {

	private final ViewEntryListDataContainer container;
	private final RouteProcessor routeProcessor;
	private final Context context;

	public ViewEntryList2JsonConverter(final ViewEntryListDataContainer velContainer, final RouteProcessor routeProcessor,
			final Context context) {
		this.container = velContainer;
		this.routeProcessor = routeProcessor;
		this.context = context;
	}

	public JsonJavaArray buildJsonFromDocument() throws NotesException {
		JsonJavaArray jsa = new JsonJavaArray();
		for (List<Object> entry : container.getData()) {
			ViewEntry2JsonConverter d2jc = new ViewEntry2JsonConverter(entry, routeProcessor, container.getView());
			JsonObject jso = d2jc.buildJsonFromEntry();
			jsa.add(jso);
		}
		return jsa;
	}

}
