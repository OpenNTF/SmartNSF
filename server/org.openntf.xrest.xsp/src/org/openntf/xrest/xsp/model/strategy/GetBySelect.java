package org.openntf.xrest.xsp.model.strategy;

import java.util.Map.Entry;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.Document2JsonConverter;
import org.openntf.xrest.xsp.exec.datacontainer.DocumentListDataContainer;
import org.openntf.xrest.xsp.exec.impl.ContextImpl;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonObject;

import groovy.lang.Closure;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;

public class GetBySelect extends AbstractDatabaseStrategy
		implements StrategyModel<DocumentListDataContainer, JsonJavaArray> {

	private String selectQueryValue;
	private Closure<?> selectQueryCl;

	public void selectQuery(final String name) {
		this.selectQueryValue = name;
	}

	public void selectQuery(final Closure<?> queryCl) {
		this.selectQueryCl = queryCl;
	}

	public String getSelectQueryValue(final Context context) {
		if (selectQueryCl != null) {
			return (String) DSLBuilder.callClosure(selectQueryCl, context);
		} else {
			return selectQueryValue;
		}
	}

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

	private String buildSelectString(final Context context) {
		String rc = getSelectQueryValue(context);
		for (Entry<String, String> routeEntry : context.getRouterVariables().entrySet()) {
			rc = rc.replace("{" + routeEntry.getKey() + "}", routeEntry.getValue());
		}
		for (Entry<String, String> routeEntry : context.getQueryStringVariables().entrySet()) {
			rc = rc.replace("{" + routeEntry.getKey() + "}", routeEntry.getValue());
		}
		return rc;
	}

	@Override
	public JsonJavaArray buildResponse(final Context context, final RouteProcessor routeProcessor,
			final DataContainer<?> dc) throws NotesException {
		Document2JsonConverter d2jc = new Document2JsonConverter(routeProcessor, context);
		JsonJavaArray jsa = new JsonJavaArray();

		String search = buildSelectString(context);
		DocumentCollection dcl = ((DocumentListDataContainer) dc).getDatabase().search(search);

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
