package org.openntf.xrest.xsp.model.strategy;

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

public class GetByFTPaged extends AbstractDatabaseStrategy implements StrategyModel<DocumentListPaginationDataContainer, JsonObject> {

	private String ftQueryValue;
	private Closure<?> ftQueryValueCl;

	public String getFtQueryValue(final Context context) {
		if (ftQueryValueCl != null) {
			return (String) DSLBuilder.callClosure(ftQueryValueCl, context);
		} else {
			return ftQueryValue;
		}
	}

	public void ftQueryValue(final String keyVariableValue) {
		this.ftQueryValue = keyVariableValue;
	}

	public void ftQueryValue(final Closure<?> keyVariableCl) {
		this.ftQueryValueCl = keyVariableCl;
	}

	@Override
	public DocumentListPaginationDataContainer buildDataContainer(final Context context) throws ExecutorException {
		try {
			Database dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), context
					.getSession());
			int start = DEFAULT_START;
			int count = DEFAULT_COUNT;
			String search = "";
			if (context.getRequest().getMethod().equals("POST")) {
				Double st = (Double) context.getJsonPayload().getJsonProperty("start");
				Double cnt = (Double) context.getJsonPayload().getJsonProperty("count");
				start = null != st ? st.intValue() : DEFAULT_START;
				count = null != cnt ? cnt.intValue() : DEFAULT_COUNT;
				search = (String) context.getJsonPayload().getJsonProperty("search");
			} else if (context.getRequest().getMethod().equals("GET")) {
				start = getParamIntValue(context.getRequest().getParameter("start"), DEFAULT_START);
				count = getParamIntValue(context.getRequest().getParameter("count"), DEFAULT_COUNT);
				search = context.getRequest().getParameter("search");
			}
			DocumentCollection dcl = dbAccess.FTSearch(search);
			int total = dcl.getCount();
			if (total == 0) {
				throw new ExecutorException(404, "Not found", "", "getmodel");
			}
			return new DocumentListPaginationDataContainer(getPagedListFromDocCollection(dcl, start, count), start, total, null, dbAccess);
		} catch (ExecutorException exe) {
			throw exe;
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}
	}

	@Override
	public JsonObject buildResponse(final Context context, final RouteProcessor routeProcessor, final DataContainer<?> dc)
			throws NotesException {
		DocumentListPaginationDataContainer docListDC = (DocumentListPaginationDataContainer) dc;
		DocumentListPaged2JsonConverter d2jc = new DocumentListPaged2JsonConverter(docListDC, routeProcessor, context);
		return d2jc.buildJsonFromDocument();
	}
}
