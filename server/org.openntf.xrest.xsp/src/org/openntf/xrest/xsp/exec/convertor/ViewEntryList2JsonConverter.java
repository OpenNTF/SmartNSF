package org.openntf.xrest.xsp.exec.convertor;

import java.util.List;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.datacontainer.ViewEntryListDataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.NotesException;
import lotus.domino.View;

public class ViewEntryList2JsonConverter extends ViewEntryList2JsonConverterBase {

	private final ViewEntryListDataContainer container;

	public ViewEntryList2JsonConverter(final ViewEntryListDataContainer velContainer, final RouteProcessor routeProcessor, final View view,
			final Context context) {
		super(routeProcessor, view, context);
		this.container = velContainer;
	}

	public JsonJavaArray buildJsonFromDocument() throws NotesException {
		JsonJavaArray jsa = new JsonJavaArray();
		for (List<Object> entry : container.getData()) {
			ViewEntry2JsonConverter d2jc = new ViewEntry2JsonConverter(entry, routeProcessor, getColumnInfoMap());
			JsonObject jso = d2jc.buildJsonFromEntry();
			jsa.add(jso);
		}
		return jsa;
	}

}
