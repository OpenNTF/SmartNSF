package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.Document2JsonConverter;
import org.openntf.xrest.xsp.exec.datacontainer.DocumentDataContainer;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonObject;

import groovy.lang.Closure;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;

public class GetByKey extends AbstractKeyViewDatabaseStrategy implements StrategyModel<DocumentDataContainer, JsonObject> {

	private String formValue;
	private Closure<?> formCl;

	public void form(String name) {
		this.formValue = name;
	}

	public void form(Closure<?> formCl) {
		this.formCl = formCl;
	}

	public String getFormValue(Context context) {
		if (formCl != null) {
			return (String) DSLBuilder.callClosure(formCl, context);
		} else {
			return formValue;
		}
	}

	@Override
	public DocumentDataContainer buildDataContainer(Context context) throws ExecutorException {
		try {
			Database dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), context.getSession());
			View viewAccess = dbAccess.getView(getViewNameValue(context));

			String key = context.getRouterVariables().get(getKeyVariableValue(context));
			if (key.equalsIgnoreCase("@new")) {
				Document doc = dbAccess.createDocument();
				String form = getFormValue(context);
				if (!StringUtil.isEmpty(form)) {
					doc.replaceItemValue("Form", form);
				}
				return new DocumentDataContainer(doc, viewAccess, dbAccess);
			}
			return new DocumentDataContainer(viewAccess.getDocumentByKey(key, true), viewAccess, dbAccess);
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}

	}

	@Override
	public JsonObject buildResponse(Context context, RouteProcessor routeProcessor, DataContainer<?> dc) throws NotesException {
		Document2JsonConverter d2j = new Document2JsonConverter(((DocumentDataContainer) dc).getData(), routeProcessor, context);
		return d2j.buildJsonFromDocument();
	}

}
