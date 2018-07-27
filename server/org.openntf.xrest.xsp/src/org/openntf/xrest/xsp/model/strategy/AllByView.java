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

import com.ibm.commons.util.io.json.JsonJavaArray;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;

public class AllByView extends AbstractViewDatabaseStrategy implements StrategyModel<DocumentListDataContainer, JsonJavaArray> {

	@Override
	public DocumentListDataContainer buildDataContainer(final Context context) throws ExecutorException {
		try {
			Database dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), getSessionFromContext(context));
			View viewAccess = dbAccess.getView(getViewNameValue(context));
			viewAccess.setAutoUpdate(false);
			List<Document> docs = new ArrayList<Document>();
			ViewNavigator vnav = viewAccess.createViewNav();
			vnav.setEntryOptions(ViewNavigator.VN_ENTRYOPT_NOCOLUMNVALUES + ViewNavigator.VN_ENTRYOPT_NOCOUNTDATA);
			vnav.setCacheGuidance(Integer.MAX_VALUE, ViewNavigator.VN_CACHEGUIDANCE_READSELECTIVE);
			ViewEntry entCurrent = vnav.getFirst();
			while (entCurrent != null && entCurrent.isValid()) {
				docs.add(entCurrent.getDocument());
				ViewEntry nextEntry = vnav.getNext();
				// recycle!
				entCurrent.recycle();
				entCurrent = nextEntry;
			}
			vnav.recycle();
			return new DocumentListDataContainer(docs, viewAccess, dbAccess);
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}
	}

	@Override
	public JsonJavaArray buildResponse(final Context context, final RouteProcessor routeProcessor, final DataContainer<?> dc) throws NotesException {
		DocumentListDataContainer docListDC = (DocumentListDataContainer) dc;
		DocumentList2JsonConverter d2jc = new DocumentList2JsonConverter(docListDC, routeProcessor, context);
		return d2jc.buildJsonFromDocument();
	}
}
