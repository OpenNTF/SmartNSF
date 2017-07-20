package org.openntf.xrest.xsp.exec.convertor;

import java.util.List;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.datacontainer.ViewEntryListPaginationDataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.NotesException;
import lotus.domino.View;

public class ViewEntryListPaged2JsonConverter extends ViewEntryList2JsonConverterBase {

	private final ViewEntryListPaginationDataContainer container;

	public ViewEntryListPaged2JsonConverter(final ViewEntryListPaginationDataContainer velContainer, final RouteProcessor routeProcessor,
			final View view, final Context context) {
		super(routeProcessor, view, context);
		this.container = velContainer;
	}

	public JsonObject buildJsonFromDocument() throws NotesException {
		JsonObject jso = new JsonJavaObject();
		jso.putJsonProperty("start", container.getStart());
		jso.putJsonProperty("count", container.getCount());
		jso.putJsonProperty("total", container.getMax());
		JsonJavaArray jsa = new JsonJavaArray();
		for (List<Object> entry : container.getData()) {
			ViewEntry2JsonConverter d2jc = new ViewEntry2JsonConverter(entry, routeProcessor, getColumnInfoMap());
			JsonObject jsobj = d2jc.buildJsonFromEntry();
			jsa.add(jsobj);
		}
		jso.putJsonProperty("entries", jsa);
		return jso;
	}

}
