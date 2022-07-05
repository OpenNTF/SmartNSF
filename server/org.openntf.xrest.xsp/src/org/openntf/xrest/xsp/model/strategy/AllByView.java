package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.Document2JsonConverter;
import org.openntf.xrest.xsp.exec.datacontainer.DocumentListDataContainer;
import org.openntf.xrest.xsp.exec.impl.ContextImpl;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import com.ibm.commons.util.io.json.JsonArray;
import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;

public class AllByView extends AbstractViewDatabaseStrategy
		implements StrategyModel<DocumentListDataContainer, JsonArray> {

	@Override
	public DocumentListDataContainer buildDataContainer(final Context context) throws ExecutorException {
		try {
			Database dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context),
					context.getDatabase(), getSessionFromContext(context));
			((ContextImpl) context).addDatabaseFromStrategy(dbAccess);
			View viewAccess = dbAccess.getView(getViewNameValue(context));
			return new DocumentListDataContainer(viewAccess, dbAccess);
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}
	}

	@Override
	public JsonArray buildResponse(final Context context, final RouteProcessor routeProcessor,
			final DataContainer<?> dc) throws NotesException {
		Document2JsonConverter d2jc = new Document2JsonConverter(routeProcessor, context);
		JsonJavaArray jsa = new JsonJavaArray();
		View viewCurrent = ((DocumentListDataContainer)dc).getView();
		viewCurrent.setAutoUpdate(false);
		//ViewNavigator vnav = ((DocumentListDataContainer)dc).getView().createViewNav();
		//vnav.setEntryOptions(ViewNavigator.VN_ENTRYOPT_NOCOLUMNVALUES + ViewNavigator.VN_ENTRYOPT_NOCOUNTDATA);
		//ViewEntry nextEntry = vnav.getFirst();
		//vnav.setCacheGuidance(Integer.MAX_VALUE, ViewNavigator.VN_CACHEGUIDANCE_READSELECTIVE);
		Document docNext = viewCurrent.getFirstDocument();
		while (docNext != null) {
			Document docProcess = docNext;
			docNext = viewCurrent.getNextDocument(docProcess);
			if (docProcess.isValid() && !docProcess.isDeleted()) {
				JsonObject jso =  d2jc.buildJsonFromDocument(docProcess);
				jsa.add(jso);
				
			}
			NotesObjectRecycler.recycle(docProcess);

			/*
			ViewEntry entryCurrent = nextEntry;
			nextEntry = vnav.getNext(entryCurrent);
			if (entryCurrent.isValid()) {
				Document doc = entryCurrent.getDocument();
				JsonObject jso =  d2jc.buildJsonFromDocument(doc);
				jsa.add(jso);
				NotesObjectRecycler.recycle(doc);
			}
			
			NotesObjectRecycler.recycle(entryCurrent);
			*/
		}
		//NotesObjectRecycler.recycle(vnav);
		return jsa;
	}
}
