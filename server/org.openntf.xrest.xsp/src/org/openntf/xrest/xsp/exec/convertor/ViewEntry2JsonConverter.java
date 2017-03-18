package org.openntf.xrest.xsp.exec.convertor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openntf.xrest.xsp.exec.convertor.datatypes.ColumnInfo;
import org.openntf.xrest.xsp.model.MapJsonType;
import org.openntf.xrest.xsp.model.MappingField;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.NotesException;

public class ViewEntry2JsonConverter {

	private final RouteProcessor routeProcessor;
	private final List<Object> columnValues;
	private final Map<String, ColumnInfo> columnInfoMap;

	public ViewEntry2JsonConverter(final List<Object> viewEntry, final RouteProcessor routeProcessor,
			final Map<String, ColumnInfo> columnInfoMap) throws NotesException {
		this.routeProcessor = routeProcessor;
		this.columnValues = viewEntry;
		this.columnInfoMap = columnInfoMap;
	}

	public JsonObject buildJsonFromEntry() throws NotesException {
		JsonObject jo = new JsonJavaObject();

		Set<String> itemsProcessed = new HashSet<String>();
		Map<String, MappingField> fieldDefinitions = routeProcessor.getMappingFields();

		for (String fieldDefinition : fieldDefinitions.keySet()) {
			MappingField mf = fieldDefinitions.get(fieldDefinition);
			if (!mf.isWriteOnly() && !mf.isFormula() && columnInfoMap.containsKey(mf.getNotesFieldName())) {
				ColumnInfo ci = columnInfoMap.get(mf.getNotesFieldName());
				int idx = ci.getColumnValuesIndex();
				processColumn(jo, idx == 65535 ? ci.getConstantValue() : columnValues.get(idx), mf);
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

}
