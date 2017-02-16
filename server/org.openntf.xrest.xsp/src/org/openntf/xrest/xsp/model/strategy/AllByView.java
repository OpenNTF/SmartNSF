package org.openntf.xrest.xsp.model.strategy;

import java.util.ArrayList;
import java.util.List;

import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.Context;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.View;

public class AllByView extends AbstractViewDatabaseStrategy implements StrategyModel<List<Document>> {

	private Database dbAccess;
	private View viewAccess;

	@Override
	public List<Document> getModel(Context context) throws ExecutorException {
		try {
			dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), context.getSession());
			viewAccess = dbAccess.getView(getViewNameValue(context));
			List<Document> docs = new ArrayList<Document>();
			Document docNext = viewAccess.getFirstDocument();
			while (docNext != null) {
				Document docProcess = docNext;
				docNext = viewAccess.getNextDocument(docNext);
				docs.add(docProcess);
			}
			return docs;
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}
	}

	@Override
	public void cleanUp() {
		try {
			viewAccess.recycle();
			dbAccess.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
