package org.openntf.xrest.xsp.model.strategy;

import java.util.ArrayList;
import java.util.List;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.ViewEntryListPaged2JsonConverter;
import org.openntf.xrest.xsp.exec.datacontainer.ViewEntryListPaginationDataContainer;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;

public class ViewEntriesByCategoryPaged extends AbstractAllByKeyViewDatabaseStrategy implements
		StrategyModel<ViewEntryListPaginationDataContainer, JsonObject> {

	private Database dbAccess;
	private View viewAccess;

	@SuppressWarnings("unchecked")
	@Override
	public ViewEntryListPaginationDataContainer buildDataContainer(final Context context) throws ExecutorException {
		try {
			dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), context.getSession());
			viewAccess = dbAccess.getView(getViewNameValue(context));
			viewAccess.setAutoUpdate(false);

			int start = getParamIntValue(context.getRequest().getParameter("start"), DEFAULT_START);
			int count = getParamIntValue(context.getRequest().getParameter("count"), DEFAULT_COUNT);
			String varValue = getKeyValue(context);
			List<List<Object>> entries = new ArrayList<List<Object>>();

			ViewNavigator vnav = viewAccess.createViewNavFromCategory(varValue);
			vnav.setCacheGuidance(count, ViewNavigator.VN_CACHEGUIDANCE_READSELECTIVE);
			vnav.setEntryOptions(ViewNavigator.VN_ENTRYOPT_NOCOUNTDATA);

			int total = -1;
			if (isReturnTotals(context)) {
				total = vnav.getCount();
			}

			int skippedEntries = 0;
			// skip only when not starting at 1
			if (start > 1) {
				// skip counts from 0, so for start==2 we should skip(1)
				skippedEntries = vnav.skip(start - 1);
			}

			if (skippedEntries == start - 1) {
				int i = 0;
				ViewEntry entCurrent = vnav.getCurrent();
				while (entCurrent != null && entCurrent.isValid() && !entCurrent.isCategory() && !entCurrent.isConflict() && i < count) {
					List<Object> columnValues = new ArrayList<Object>();
					columnValues.addAll(entCurrent.getColumnValues());
					// add UNID to the list of values for this row
					columnValues.add(entCurrent.getUniversalID());
					entries.add(columnValues);
					i++;
					ViewEntry nextEntry = vnav.getNext();
					// recycle!
					entCurrent.recycle();
					entCurrent = nextEntry;
				}
			}
			vnav.recycle();
			return new ViewEntryListPaginationDataContainer(entries, start, total, viewAccess, dbAccess);
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}
	}

	@Override
	public JsonObject buildResponse(final Context context, final RouteProcessor routeProcessor, final DataContainer<?> dc)
			throws NotesException {
		// at this point we should already have dbAccess and viewAccess, but
		// just to be on the safe side
		if (null == dbAccess) {
			dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), context.getSession());
			viewAccess = dbAccess.getView(getViewNameValue(context));
		}
		ViewEntryListPaginationDataContainer veldc = (ViewEntryListPaginationDataContainer) dc;
		ViewEntryListPaged2JsonConverter d2jc = new ViewEntryListPaged2JsonConverter(veldc, routeProcessor, viewAccess, context);
		return d2jc.buildJsonFromDocument();
	}
}
