package org.openntf.xrest.xsp.exec.convertor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.datacontainer.ViewEntryListDataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;

public class ViewEntryList2JsonConverter {

	private final ViewEntryListDataContainer container;
	private final RouteProcessor routeProcessor;
	private final View view;
	private final Context context;
	private List<ColumnInfo> columnInfo;
	private Map<String, ColumnInfo> columnInfoMap;

	public ViewEntryList2JsonConverter(final ViewEntryListDataContainer velContainer, final RouteProcessor routeProcessor, final View view,
			final Context context) {
		this.container = velContainer;
		this.routeProcessor = routeProcessor;
		this.view = view;
		this.context = context;
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
				result.add(new ColumnInfo(col));
			}
			columnInfo = result;
		}
		return columnInfo;
	}

	class ColumnInfo {
		private final String itemName;
		private final int columnValuesIndex;
		private final Object constantValue;

		public ColumnInfo(final ViewColumn column) throws NotesException {
			itemName = column.getItemName();
			columnValuesIndex = column.getColumnValuesIndex();
			if (columnValuesIndex == 65535) {
				List<?> v = context.getNSFHelper().executeFormula(column.getFormula());
				constantValue = v.get(0);
			} else {
				constantValue = null;
			}
		}

		/**
		 * Gets the programmatic name of the column
		 *
		 * @return String programmatic column name
		 */
		public String getItemName() {
			return itemName;
		}

		/**
		 * Gets the index for the column in the view, beginning at 0
		 *
		 * @return int index of the column
		 */
		public int getColumnValuesIndex() {
			return columnValuesIndex;
		}

		/**
		 * Gets constant (stored as result of evaluated column formula)
		 * 
		 * @return Object constant or null if column doesn't contain constant
		 */
		public Object getConstantValue() {
			return constantValue;
		}

		@Override
		public String toString() {
			return "ColumnInfo [" + (itemName != null ? "itemName=" + itemName + ", " : "") + "columnValuesIndex=" + columnValuesIndex
					+ ", " + (constantValue != null ? "constantValue=" + constantValue : "") + "]";
		}
	}
}
