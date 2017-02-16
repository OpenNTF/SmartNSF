package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.Context;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.View;

public class GetByKey extends AbstractKeyViewDatabaseStrategy implements StrategyModel<Document> {

	private Database dbAccess;
	private View viewAccess;

	@Override
	public Document getModel(Context context) throws ExecutorException {
		try {
			dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), context.getSession());
			viewAccess = dbAccess.getView(getViewNameValue(context));

			String key = context.getRouterVariables().get(getKeyVariableValue(context));
			if (key.equalsIgnoreCase("@new")) {
				return dbAccess.createDocument();
			}
			return viewAccess.getDocumentByKey(key, true);
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
