package org.openntf.xrest.xsp.model.strategy;

import java.util.ArrayList;
import java.util.List;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.ViewEntryList2JsonConverter;
import org.openntf.xrest.xsp.exec.datacontainer.ViewEntryListDataContainer;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaArray;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;

public class ViewEntriesPaged extends AbstractViewDatabaseStrategy implements StrategyModel<ViewEntryListDataContainer, JsonJavaArray> {

	private Database dbAccess;
	private View viewAccess;

	@SuppressWarnings("unchecked")
	@Override
	public ViewEntryListDataContainer buildDataContainer(final Context context) throws ExecutorException {
		try {
			dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), context.getSession());
			viewAccess = dbAccess.getView(getViewNameValue(context));
			viewAccess.setAutoUpdate(false);
			int total = -1;
			String totals = context.getRequest().getParameter("totals");
			// skip counting only if we have parameter totals=off
			// if parameter is omitted, we assume it is on
			totals = null == totals ? "on" : totals;
			if (!totals.equals("off")) {
				total = viewAccess.getEntryCount();
			}
			int start = getParamIntValue(context.getRequest().getParameter("start"), DEFAULT_START);
			int count = getParamIntValue(context.getRequest().getParameter("count"), DEFAULT_COUNT);

			List<List<Object>> entries = new ArrayList<List<Object>>();

			ViewNavigator vnav = viewAccess.createViewNav();
			vnav.setCacheGuidance(count, ViewNavigator.VN_CACHEGUIDANCE_READSELECTIVE);
			vnav.setEntryOptions(ViewNavigator.VN_ENTRYOPT_NOCOUNTDATA);
			int skippedEntries = 0;
			// skip only when not starting at 1
			if (start > 1) {
				// skip counts from 0, so for start==2 we should skip(1)
				skippedEntries = vnav.skip(start - 1);
			}

			if (skippedEntries == start - 1) {
				int i = 0;
				ViewEntry entCurrent = vnav.getFirst();
				while (entCurrent != null && entCurrent.isValid() && i < count) {
					List<Object> columnValues = new ArrayList<Object>();
					columnValues.addAll(entCurrent.getColumnValues());
					entries.add(columnValues);
					i++;
					ViewEntry nextEntry = vnav.getNext();
					// recycle!
					entCurrent.recycle();
					entCurrent = nextEntry;
				}
			}
			vnav.recycle();
			return new ViewEntryListDataContainer(entries, viewAccess, dbAccess);
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}
	}

	@Override
	public JsonJavaArray buildResponse(final Context context, final RouteProcessor routeProcessor, final DataContainer<?> dc)
			throws NotesException {
		// at this point we should already have dbAccess and viewAccess, but
		// just to be on the safe side
		if (null == dbAccess) {
			dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), context.getSession());
			viewAccess = dbAccess.getView(getViewNameValue(context));
		}
		ViewEntryListDataContainer veldc = (ViewEntryListDataContainer) dc;
		ViewEntryList2JsonConverter d2jc = new ViewEntryList2JsonConverter(veldc, routeProcessor, viewAccess);
		return d2jc.buildJsonFromDocument();
	}
}
