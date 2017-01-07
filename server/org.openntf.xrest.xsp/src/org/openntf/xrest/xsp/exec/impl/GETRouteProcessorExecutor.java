package org.openntf.xrest.xsp.exec.impl;

import java.util.List;

import org.openntf.xrest.xsp.exec.DataModel;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonArray;
import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Document;

public class GETRouteProcessorExecutor extends AbstractRouteProcessorExecutor {

	public GETRouteProcessorExecutor(Context context, RouteProcessor routerProcessor, String path) {
		super(context, routerProcessor, path);
	}

	@Override
	protected void executeMethodeSpecific(Context context, DataModel<?> model) {
		try {
			if (model.isList()) {
				JsonArray jsa = new JsonJavaArray();
				@SuppressWarnings("unchecked")
				List<Document> documents = (List<Document>) model.getData();
				for (Document doc : documents) {
					JsonObject jo = buildJsonFromDocument(doc);
					jsa.add(jo);
				}
				setResultPayload(jsa);
			} else {
				Document doc = (Document) model.getData();
				JsonObject jo = buildJsonFromDocument(doc);
				setResultPayload(jo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
