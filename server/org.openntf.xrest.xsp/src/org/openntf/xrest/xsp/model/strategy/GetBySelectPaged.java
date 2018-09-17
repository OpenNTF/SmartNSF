package org.openntf.xrest.xsp.model.strategy;

import java.util.Map.Entry;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.DocumentListPaged2JsonConverter;
import org.openntf.xrest.xsp.exec.datacontainer.DocumentListPaginationDataContainer;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonObject;

import groovy.lang.Closure;
import lotus.domino.Database;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;

public class GetBySelectPaged extends AbstractDatabaseStrategy implements StrategyModel<DocumentListPaginationDataContainer, JsonObject> {

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
	public DocumentListPaginationDataContainer buildDataContainer(final Context context) throws ExecutorException {
		try {
			Database dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), getSessionFromContext(context));
			String search = buildSelectString(context);
			DocumentCollection dcl = dbAccess.search(search);
			int total = dcl.getCount();
			int start = getParamIntValue(context.getRequest().getParameter("start"), DEFAULT_START);
			int count = getParamIntValue(context.getRequest().getParameter("count"), DEFAULT_COUNT);
			return new DocumentListPaginationDataContainer(getPagedListFromDocCollection(dcl, start, count), start, total, null, dbAccess);
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}

	}

	private String buildSelectString(final Context context) {
		String rc = getSelectQueryValue(context);
		for (Entry<String, String> routeEntry : context.getRouterVariables().entrySet()) {
			rc = rc.replace("{" + routeEntry.getKey() + "}", routeEntry.getValue());
		}
		return rc;
	}

	@Override
	public JsonObject buildResponse(final Context context, final RouteProcessor routeProcessor, final DataContainer<?> dc)
			throws NotesException {
		DocumentListPaginationDataContainer docListDC = (DocumentListPaginationDataContainer) dc;
		DocumentListPaged2JsonConverter d2jc = new DocumentListPaged2JsonConverter(docListDC, routeProcessor, context);
		return d2jc.buildJsonFromDocument();
	}
}
