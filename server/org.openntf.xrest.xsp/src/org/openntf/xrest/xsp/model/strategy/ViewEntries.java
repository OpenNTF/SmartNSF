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

public class ViewEntries extends AbstractViewDatabaseStrategy implements StrategyModel<ViewEntryListDataContainer, JsonJavaArray> {

	private Database dbAccess;
	private View viewAccess;

	@SuppressWarnings("unchecked")
	@Override
	public ViewEntryListDataContainer buildDataContainer(final Context context) throws ExecutorException {
		try {
			dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), getSessionFromContext(context));
			viewAccess = dbAccess.getView(getViewNameValue(context));
			viewAccess.setAutoUpdate(false);
			List<List<Object>> entries = new ArrayList<List<Object>>();

			ViewNavigator vnav = viewAccess.createViewNav();
			ViewEntry entCurrent = vnav.getFirst();
			while (entCurrent != null && entCurrent.isValid() && !entCurrent.isCategory() && !entCurrent.isConflict()) {
				List<Object> columnValues = new ArrayList<Object>();
				columnValues.addAll(entCurrent.getColumnValues());
				// add UNID to the list of values for this row
				columnValues.add(entCurrent.getUniversalID());
				entries.add(columnValues);
				ViewEntry nextEntry = vnav.getNext();
				// recycle!
				entCurrent.recycle();
				entCurrent = nextEntry;
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
		ViewEntryList2JsonConverter d2jc = new ViewEntryList2JsonConverter(veldc, routeProcessor, viewAccess, context);
		return d2jc.buildJsonFromDocument();
	}
}
