package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.DocumentListPaged2JsonConverter;
import org.openntf.xrest.xsp.exec.datacontainer.DocumentListPaginationDataContainer;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Database;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;

public class AllByKeyPaged extends AbstractAllByKeyViewDatabaseStrategy implements
		StrategyModel<DocumentListPaginationDataContainer, JsonObject> {

	@Override
	public DocumentListPaginationDataContainer buildDataContainer(final Context context) throws ExecutorException {
		try {
			Database dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), context
					.getSession());
			View viewAccess = dbAccess.getView(getViewNameValue(context));
			viewAccess.setAutoUpdate(false);
			String varValue = getKeyValue(context);

			DocumentCollection dcl = viewAccess.getAllDocumentsByKey(varValue, isExact(context));

			int total = dcl.getCount();
			int start = getParamIntValue(context.getRequest().getParameter("start"), DEFAULT_START);
			int count = getParamIntValue(context.getRequest().getParameter("count"), DEFAULT_COUNT);
			return new DocumentListPaginationDataContainer(getPagedListFromDocCollection(dcl, start, count), start, total, viewAccess,
					dbAccess);
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
