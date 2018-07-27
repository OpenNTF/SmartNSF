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

	public void form(final String name) {
		this.formValue = name;
	}

	public void form(final Closure<?> formCl) {
		this.formCl = formCl;
	}

	public String getFormValue(final Context context) {
		if (formCl != null) {
			return (String) DSLBuilder.callClosure(formCl, context);
		} else {
			return formValue;
		}
	}

	@Override
	public DocumentDataContainer buildDataContainer(final Context context) throws ExecutorException {
		try {
			Database dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), getSessionFromContext(context));
			View viewAccess = dbAccess.getView(getViewNameValue(context));
			String key = getKeyValue(context);

			if (key.equalsIgnoreCase("@new")) {
				Document doc = dbAccess.createDocument();
				String form = getFormValue(context);
				if (!StringUtil.isEmpty(form)) {
					doc.replaceItemValue("Form", form);
				}
				return new DocumentDataContainer(doc, viewAccess, dbAccess);
			}
			Document doc = viewAccess.getDocumentByKey(key, true);
			if (null == doc) {
				throw new ExecutorException(404, "Not found", "", "getmodel");
			}
			return new DocumentDataContainer(doc, viewAccess, dbAccess);
		} catch (ExecutorException exe) {
			throw exe;
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}

	}

	@Override
	public JsonObject buildResponse(final Context context, final RouteProcessor routeProcessor, final DataContainer<?> dc)
			throws NotesException {
		Document2JsonConverter d2j = new Document2JsonConverter(((DocumentDataContainer) dc).getData(), routeProcessor, context);
		return d2j.buildJsonFromDocument();
	}

}
