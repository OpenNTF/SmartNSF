package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.Document2JsonConverter;
import org.openntf.xrest.xsp.exec.datacontainer.DocumentListDataContainer;
import org.openntf.xrest.xsp.exec.impl.ContextImpl;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;

public class GetByFT extends AbstractFTDatabaseStrategy
		implements StrategyModel<DocumentListDataContainer, JsonJavaArray> {

	@Override
	public DocumentListDataContainer buildDataContainer(final Context context) throws ExecutorException {
		try {
			Database dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context),
					context.getDatabase(), getSessionFromContext(context));
			((ContextImpl) context).addDatabaseFromStrategy(dbAccess);
			return new DocumentListDataContainer(null, dbAccess);
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}
	}

	@Override
	public JsonJavaArray buildResponse(final Context context, final RouteProcessor routeProcessor,
			final DataContainer<?> dc) throws NotesException {
		Document2JsonConverter d2jc = new Document2JsonConverter(routeProcessor, context);
		JsonJavaArray jsa = new JsonJavaArray();
		String search = "";
		if (context.getRequest().getMethod().equals("POST")) {
			search = (String) context.getJsonPayload().getJsonProperty("search");
		} else if (context.getRequest().getMethod().equals("GET")) {
			search = context.getRequest().getParameter("search");
		}
		String searchKeyValue = getFtQueryValue(context);
		if (!StringUtil.isEmpty(searchKeyValue)) {
			if (context.getRouterVariables().containsKey(searchKeyValue)) {
				search = context.getRouterVariables().get(searchKeyValue);
			} else {
				search = context.getQueryStringVariables().get(searchKeyValue);
			}
		}
		DocumentCollection dcl = ((DocumentListDataContainer) dc).getDatabase().FTSearch(search);

		Document docNext = dcl.getFirstDocument();
		while (docNext != null) {
			Document docProcess = docNext;
			docNext = dcl.getNextDocument();
			JsonObject jso = d2jc.buildJsonFromDocument(docProcess);
			jsa.add(jso);
			NotesObjectRecycler.recycle(docProcess);
		}
		dcl.recycle();
		return jsa;

	}
}
