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
	private ViewEntry entNext;

	private static final int DEFAULT_START = 1;
	private static final int DEFAULT_COUNT = 10;

	@Override
	public List<Document> getModel(final Context context) throws ExecutorException {
		try {
			dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(),
					context.getSession());
			viewAccess = dbAccess.getView(getViewNameValue(context));
			vnav = viewAccess.createViewNav();
			String paramStart = context.getRequest().getParameter("start");
			String paramCount = context.getRequest().getParameter("count");
			int start = DEFAULT_START;
			try {
				if (null != paramStart) {
					start = Integer.valueOf(paramStart);
					if (start < 1) {
						start = DEFAULT_START;
					}
					// we may need to check if start is not greater than
					// vnav.getCount() and issue an error message? the code
					// works without it, but returns an empty list
				}
			} catch (NumberFormatException e) {
			}
			int count = DEFAULT_COUNT;
			try {
				if (null != paramCount) {
					count = Integer.valueOf(paramCount);
					if (count < 1) {
						count = DEFAULT_COUNT;
					}
				}
			} catch (NumberFormatException e) {
			}
			List<Document> docs = new ArrayList<Document>();
			entNext = vnav.getNth(start);
			int i = 0;
			while (entNext != null && i < count) {
				ViewEntry entProcess = entNext;
				entNext = vnav.getNext(entNext);
				docs.add(dbAccess.getDocumentByUNID(entProcess.getUniversalID()));
				i++;
			}
			return docs;
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}
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
			if (null != dbAccess] {
				dbAccess.recycle();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
