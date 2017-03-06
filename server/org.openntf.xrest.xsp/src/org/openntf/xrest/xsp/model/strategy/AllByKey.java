package org.openntf.xrest.xsp.model.strategy;

import java.util.ArrayList;
import java.util.List;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.DocumentList2JsonConverter;
import org.openntf.xrest.xsp.exec.datacontainer.DocumentListDataContainer;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaArray;

import groovy.lang.Closure;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;

public class AllByKey extends AbstractKeyViewDatabaseStrategy implements StrategyModel<DocumentListDataContainer, JsonJavaArray> {

	private String modeValue;
	private Closure<?> modeMatchCl;

	public void mode(final String mode) {
		this.modeValue = mode;
	}

	public void mode(final Closure<?> modeCl) {
		this.modeMatchCl = modeCl;
	}

	public String getModeValue(final Context context) {
		if (this.modeMatchCl != null) {
			return (String) DSLBuilder.callClosure(modeMatchCl, context);
		} else {
			return modeValue;
		}
	}

	@Override
	public DocumentListDataContainer buildDataContainer(final Context context) throws ExecutorException {
		try {
			Database dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), context
					.getSession());
			View viewAccess = dbAccess.getView(getViewNameValue(context));
			viewAccess.setAutoUpdate(false);
			List<Document> docs = new ArrayList<Document>();
			String varValue = context.getRouterVariables().get(getKeyVariableValue(context));

			boolean exact = false;
			String mode = getModeValue(context);
			if (null != mode && mode.equalsIgnoreCase("exact")) {
				exact = true;
			}

			DocumentCollection dcl = viewAccess.getAllDocumentsByKey(varValue, exact);

			Document docNext = dcl.getFirstDocument();
			while (docNext != null) {
				Document docProcess = docNext;
				docNext = dcl.getNextDocument();
				docs.add(docProcess);
			}
			dcl.recycle();
			return new DocumentListDataContainer(docs, viewAccess, dbAccess);
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}
	}

	@Override
	public JsonJavaArray buildResponse(final Context context, final RouteProcessor routeProcessor, final DataContainer<?> dc)
			throws NotesException {
		DocumentListDataContainer docListDC = (DocumentListDataContainer) dc;
		DocumentList2JsonConverter d2jc = new DocumentList2JsonConverter(docListDC, routeProcessor, context);
		return d2jc.buildJsonFromDocument();
	}
}
