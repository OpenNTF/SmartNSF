package org.openntf.xrest.xsp.exec.convertor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.convertor.datatypes.ColumnInfo;
import org.openntf.xrest.xsp.exec.datacontainer.ViewEntryListPaginationDataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;

public class ViewEntryListPaged2JsonConverter {

	private final ViewEntryListPaginationDataContainer container;
	private final RouteProcessor routeProcessor;
	private final View view;
	private final Context context;
	private List<ColumnInfo> columnInfo;
	private Map<String, ColumnInfo> columnInfoMap;

	public ViewEntryListPaged2JsonConverter(final ViewEntryListPaginationDataContainer velContainer, final RouteProcessor routeProcessor,
			final View view, final Context context) {
		this.container = velContainer;
		this.routeProcessor = routeProcessor;
		this.view = view;
		this.context = context;
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

	private Map<String, ColumnInfo> getColumnInfoMap() throws NotesException {
		if (columnInfoMap == null) {
			columnInfoMap = new LinkedHashMap<String, ColumnInfo>();
			for (ColumnInfo columnInfo : getColumnInfos()) {
				columnInfoMap.put(columnInfo.getItemName(), columnInfo);
			}
		}
		return columnInfoMap;
	}

	private List<ColumnInfo> getColumnInfos() throws NotesException {
		if (columnInfo == null) {
			@SuppressWarnings("unchecked")
			Vector<ViewColumn> columns = view.getColumns();
			List<ColumnInfo> result = new ArrayList<ColumnInfo>(columns.size());
			for (ViewColumn col : columns) {
				result.add(new ColumnInfo(col, context));
			}
			columnInfo = result;
		}
		return columnInfo;
	}

}
