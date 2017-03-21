package org.openntf.xrest.xsp.exec.convertor.datatypes;

import java.util.List;

import org.openntf.xrest.xsp.exec.Context;

import lotus.domino.NotesException;
import lotus.domino.ViewColumn;

public class ColumnInfo {
	private final String itemName;
	private final int columnValuesIndex;
	private final Object constantValue;

	public ColumnInfo(final ViewColumn column, final Context context) throws NotesException {
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
	 * This constructor allows direct setting of fields without actual
	 * ViewColumn. Used now to add special "@unid" column
	 * 
	 * @param name
	 * @param index
	 * @param value
	 */
	public ColumnInfo(final String name, final int index, final Object value) {
		this.itemName = name;
		this.columnValuesIndex = index;
		this.constantValue = value;
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
		return "ColumnInfo [" + (itemName != null ? "itemName=" + itemName + ", " : "") + "columnValuesIndex=" + columnValuesIndex + ", "
				+ (constantValue != null ? "constantValue=" + constantValue : "") + "]";
	}
}