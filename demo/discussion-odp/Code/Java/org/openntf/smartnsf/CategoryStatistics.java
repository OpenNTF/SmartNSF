package org.openntf.smartnsf;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;
public class CategoryStatistics {

	private final List<JsonJavaObject> payload;
	public CategoryStatistics(List<JsonJavaObject> payload) {
		this.payload = payload;
	}
	
	public JsonObject count() {
		Map<String, Integer> countStore = new HashMap<String, Integer>();
		for (JsonJavaObject jso: payload) {
			JsonJavaArray cats = jso.getAsArray("categories");
			for (int counter = 0; counter < cats.size(); counter ++) {
				String catValue = cats.getAsString(counter);
				addCatValueToMap(countStore, catValue);
			}
		}
		JsonJavaObject jsoResult = new JsonJavaObject();
		for (Entry<String, Integer> entry : countStore.entrySet()) {
			jsoResult.put(entry.getKey(), entry.getValue());
		}
		return jsoResult;
	}

	private void addCatValueToMap(Map<String, Integer> countStore, String catValue) {
		if (countStore.containsKey(catValue)) {
			Integer val = countStore.get(catValue);
			int newVal = val + 1;
			countStore.put(catValue, newVal);
		} else {
			countStore.put(catValue, 1);
		}		
	}
}
