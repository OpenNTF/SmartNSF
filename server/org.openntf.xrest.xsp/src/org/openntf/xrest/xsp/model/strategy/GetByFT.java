package org.openntf.xrest.xsp.model.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.DocumentList2JsonConverter;
import org.openntf.xrest.xsp.exec.datacontainer.DocumentListDataContainer;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaArray;

import groovy.lang.Closure;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;

public class GetByFT extends AbstractDatabaseStrategy implements StrategyModel<DocumentListDataContainer, JsonJavaArray> {

	private String ftQueryValue;
	private Closure<?> ftQueryValueCl;

	public String getFtQueryValue(Context context) {
		if (ftQueryValueCl != null) {
			return (String) DSLBuilder.callClosure(ftQueryValueCl, context);
		} else {
			return ftQueryValue;
		}
	}

	public void ftQueryValue(String keyVariableValue) {
		this.ftQueryValue = keyVariableValue;
	}

	public void ftQueryValue(Closure<?> keyVariableCl) {
		this.ftQueryValueCl = keyVariableCl;
	}

	@Override
	public DocumentListDataContainer buildDataContainer(Context context) throws ExecutorException {
		try {
			Database dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), context.getSession());
			List<Document> docs = new ArrayList<Document>();
			String search = buildSearchString(context);
			DocumentCollection dcl = dbAccess.FTSearch(search);
			Document docNext = dcl.getFirstDocument();
			while (docNext != null) {
				Document docProcess = docNext;
				docNext = dcl.getNextDocument();
				docs.add(docProcess);
			}
			return new DocumentListDataContainer(docs, null, dbAccess);
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}
	}

	private String buildSearchString(Context context) {
		String rc = getFtQueryValue(context);
		for (Entry<String, String> routeEntry : context.getRouterVariables().entrySet()) {
			rc = rc.replace("{" + routeEntry.getKey() + "}", routeEntry.getValue());
		}
		return rc;
	}

	@Override
	public JsonJavaArray buildResponse(Context context, RouteProcessor routeProcessor, DataContainer<?> dc) throws NotesException {
		DocumentListDataContainer docListDC = (DocumentListDataContainer) dc;
		DocumentList2JsonConverter d2jc = new DocumentList2JsonConverter(docListDC, routeProcessor, context);
		return d2jc.buildJsonFromDocument();
	}
}
