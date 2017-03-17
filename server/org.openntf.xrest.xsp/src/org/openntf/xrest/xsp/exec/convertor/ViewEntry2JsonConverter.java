package org.openntf.xrest.xsp.exec.convertor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.openntf.xrest.xsp.model.MapJsonType;
import org.openntf.xrest.xsp.model.MappingField;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;

public class ViewEntry2JsonConverter {

	private final RouteProcessor routeProcessor;
	private final Vector<ViewColumn> columns;
	private List<ColumnInfo> columnInfo;
	private Map<String, ColumnInfo> columnInfoMap;
	private List<Object> columnValues;

	@SuppressWarnings("unchecked")
	public ViewEntry2JsonConverter(final List<Object> viewEntry, final RouteProcessor routeProcessor, final View view)
			throws NotesException {
		this.routeProcessor = routeProcessor;
		this.columnValues = viewEntry;
		this.columns = view.getColumns();
	}

	public JsonObject buildJsonFromEntry() throws NotesException {
		JsonObject jo = new JsonJavaObject();
		if (null == columns) {
			return jo;
		}
		Set<String> itemsProcessed = new HashSet<String>();
		Map<String, MappingField> fieldDefinitions = routeProcessor.getMappingFields();

		for (String fieldDefinition : fieldDefinitions.keySet()) {
			MappingField mf = fieldDefinitions.get(fieldDefinition);
			Object columnValue = columnValues.get(getColumnInfoMap().get(mf.getNotesFieldName()).getColumnValuesIndex());
			if (!mf.isWriteOnly()) {
				processColumn(jo, columnValue, mf);
				itemsProcessed.add(mf.getNotesFieldName());
			}
		}
		return jo;
	}

	private void processColumn(final JsonObject jo, final Object clmnValue, final MappingField mappingField) throws NotesException {
		MapJsonType mjType = mappingField.getType();
		// System.out.println("DEBUG: processColumn=" + clmnValue);
		// System.out.println("DEBUG: mappingField=" + mappingField);
		mjType.processColumnValueToJsonObject(clmnValue, jo, mappingField.getJsonName());
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

		public ColumnInfo(final ViewColumn column) throws NotesException {
			itemName = column.getItemName();
			columnValuesIndex = column.getColumnValuesIndex();
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

		@Override
		public String toString() {
			return "ColumnInfo [" + (itemName != null ? "itemName=" + itemName + ", " : "") + "columnValuesIndex=" + columnValuesIndex
					+ "]";
		}

	}

}
