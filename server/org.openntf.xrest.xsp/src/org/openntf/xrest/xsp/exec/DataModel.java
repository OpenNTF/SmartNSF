package org.openntf.xrest.xsp.exec;

import java.util.List;

import lotus.domino.Base;

public class DataModel<T> {

	private final T data;

	public DataModel(T data) {
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public boolean isList() {
		return data instanceof List;
	}

	public void cleanUp() {
		if (isList()) {
			@SuppressWarnings("unchecked")
			List<Object> dataList = (List<Object>) data;
			for (Object dataValue : dataList) {
				recyclyData(dataValue);
			}
		} else {
			recyclyData(data);
		}
	}

	private void recyclyData(Object dataValue) {
		if (dataValue instanceof Base) {
			try {
				((Base) dataValue).recycle();
			} catch (Exception ex) {
				// SILENCE
			}
		}
	}
}
