package org.openntf.xrest.xsp.model.strategy;

import java.util.ArrayList;
import java.util.List;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.DocumentList2JsonConverter;
import org.openntf.xrest.xsp.exec.datacontainer.DocumentListDataContainer;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import com.ibm.commons.util.io.json.JsonJavaArray;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;

public class AllByKey extends AbstractKeyViewDatabaseStrategy implements StrategyModel<DocumentListDataContainer, JsonJavaArray> {

	private Database dbAccess;
	private View viewAccess;

	@Override
	public DocumentListDataContainer buildDataContainer(Context context) throws ExecutorException {
		try {
			dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), context.getSession());
			viewAccess = dbAccess.getView(getViewNameValue(context));
			List<Document> docs = new ArrayList<Document>();
			String varValue = context.getRouterVariables().get(getKeyVariableValue(context));
			DocumentCollection dcl = viewAccess.getAllDocumentsByKey(varValue);

			Document docNext = dcl.getFirstDocument();
			while (docNext != null) {
				Document docProcess = docNext;
				docNext = dcl.getNextDocument();
				docs.add(docProcess);
			}
			dcl.recycle();
			return new DocumentListDataContainer(docs);
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}
	}

	@Override
	public void cleanUp() {
		NotesObjectRecycler.recycle(viewAccess, dbAccess);
	}

	@Override
	public JsonJavaArray buildResponse(Context context, RouteProcessor routeProcessor, DataContainer<?> dc) throws NotesException {
		DocumentListDataContainer docListDC = (DocumentListDataContainer) dc;
		DocumentList2JsonConverter d2jc = new DocumentList2JsonConverter(docListDC, routeProcessor, context);
		return d2jc.buildJsonFromDocument();
	}
}
