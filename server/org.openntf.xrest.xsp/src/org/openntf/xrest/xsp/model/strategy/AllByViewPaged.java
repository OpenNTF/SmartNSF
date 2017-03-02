package org.openntf.xrest.xsp.model.strategy;

import java.util.ArrayList;
import java.util.List;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;

public class AllByViewPaged extends AbstractViewDatabaseStrategy implements StrategyModel<List<Document>> {
	private Database dbAccess;
	private View viewAccess;
	private ViewNavigator vnav;
	private ViewEntry entCurrent;

	private static final int DEFAULT_START = 1;
	private static final int DEFAULT_COUNT = 10;

	@Override
	public List<Document> getModel(final Context context) throws ExecutorException {
		try {
			dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(),
					context.getSession());
			viewAccess = dbAccess.getView(getViewNameValue(context));
			vnav = viewAccess.createViewNav();
			// TODO: should probably check if dbAccess, viewAccess and vnav are
			// not null before proceeding

			int start = getParamIntValue(context.getRequest().getParameter("start"), DEFAULT_START);
			int count = getParamIntValue(context.getRequest().getParameter("count"), DEFAULT_COUNT);
			List<Document> docs = new ArrayList<Document>();

			int skippedEntries = vnav.skip(start);
			if (skippedEntries == start) {
				entCurrent = vnav.getCurrent();
				int i = 0;
				while (entCurrent != null && entCurrent.isValid() && i < count) {
					docs.add(dbAccess.getDocumentByUNID(entCurrent.getUniversalID()));
					i++;
					ViewEntry nextEntry = vnav.getNext();
					// recycle!
					entCurrent.recycle();
					entCurrent = nextEntry;
				}
			} else {
				// TODO: handle the situation when we did not skipped to desired
				// start: is it because we are already at the end of view?
			}
			return docs;
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}
	}

	/**
	 * Convert given param to int, assing defaultVal when it is empty or less
	 * than 1
	 * 
	 * @param param
	 * @param defaultVal
	 * @return int value of param or defaulVal
	 */
	private int getParamIntValue(final String param, final int defaultVal) {
		int count = defaultVal;
		try {
			if (null != param) {
				count = Integer.parseInt(param);
				if (count < 1) {
					count = defaultVal;
				}
			}
		} catch (NumberFormatException e) {
		}
		return count;
	}

	@Override
	public void cleanUp() {
		try {
			if (null != vnav) {
				vnav.recycle();
			}
			if (null != viewAccess) {
				viewAccess.recycle();
			}
			if (null != dbAccess) {
				dbAccess.recycle();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
