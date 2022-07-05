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

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;

public class AllByKey extends AbstractAllByKeyViewDatabaseStrategy
		implements StrategyModel<DocumentListDataContainer, JsonJavaArray> {

	@Override
	public DocumentListDataContainer buildDataContainer(final Context context) throws ExecutorException {
		try {
			Database dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context),
					context.getDatabase(), getSessionFromContext(context));
			((ContextImpl) context).addDatabaseFromStrategy(dbAccess);
			View viewAccess = dbAccess.getView(getViewNameValue(context));
			String varValue = getKeyValue(context);
			if (StringUtil.isEmpty(varValue)) {
				throw new ExecutorException(400, "KeyValue for strategy should not be blank", "", "getmodel");
			}

			return new DocumentListDataContainer(viewAccess, dbAccess);
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}
	}

	@Override
	public JsonJavaArray buildResponse(final Context context, final RouteProcessor routeProcessor,
			final DataContainer<?> dc) throws NotesException {
		Document2JsonConverter d2jc = new Document2JsonConverter(routeProcessor, context);
		JsonJavaArray jsa = new JsonJavaArray();
		((DocumentListDataContainer) dc).getView().setAutoUpdate(false);
		String varValue = getKeyValue(context);
		DocumentCollection dcl = ((DocumentListDataContainer) dc).getView().getAllDocumentsByKey(varValue,
				isExact(context));
		Document docNext = dcl.getFirstDocument();
		while (docNext != null) {
			Document docProcess = docNext;
			docNext = dcl.getNextDocument();
			JsonObject jso = d2jc.buildJsonFromDocument(docProcess);
			jsa.add(jso);
			NotesObjectRecycler.recycle(docProcess);
		}
		dcl.recycle();
		return jsa;
	}
}
