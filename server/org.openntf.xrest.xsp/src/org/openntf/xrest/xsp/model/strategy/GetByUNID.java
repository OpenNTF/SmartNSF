package org.openntf.xrest.xsp.model.strategy;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.Document2JsonConverter;
import org.openntf.xrest.xsp.exec.datacontainer.DocumentDataContainer;
import org.openntf.xrest.xsp.exec.impl.ContextImpl;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonObject;

import groovy.lang.Closure;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;

public class GetByUNID extends AbstractDatabaseStrategy implements StrategyModel<DocumentDataContainer, JsonObject> {

	private String keyVariableValue;
	private Closure<?> keyVariableCl;
	private String formValue;
	private Closure<?> formCl;

	public void keyVariableName(final String name) {
		this.keyVariableValue = name;
	}

	public void keyVariableName(final Closure<?> keyVariableCl) {
		this.keyVariableCl = keyVariableCl;
	}

	public void form(final String name) {
		this.formValue = name;
	}

	public void form(final Closure<?> formCl) {
		this.formCl = formCl;
	}

	public String getKeyVariableValue(final Context context) {
		if (keyVariableCl != null) {
			return (String) DSLBuilder.callClosure(keyVariableCl, context);
		} else {
			return keyVariableValue;
		}
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
			((ContextImpl)context).addDatabaseFromStrategy(dbAccess);
			String unid = getUNID(context);
			if (unid.equalsIgnoreCase("@new")) {
				Document doc = dbAccess.createDocument();
				String form = getFormValue(context);
				if (!StringUtil.isEmpty(form)) {
					doc.replaceItemValue("Form", form);
				}
				return new DocumentDataContainer(doc, null, dbAccess);
			}
			Document doc = null;
			try {
				doc = dbAccess.getDocumentByUNID(unid);
			} catch (NotesException ne) {
				throw new ExecutorException(404, "Not found", "", "getmodel");
			}
			return new DocumentDataContainer(doc, null, dbAccess);
		} catch (ExecutorException exe) {
			throw exe;
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}
	}

	private String getUNID(final Context context) {
		String keyVariableValue = this.getKeyVariableValue(context);
		if (context.getRouterVariables().containsKey(keyVariableValue)) {
			return context.getRouterVariables().get(keyVariableValue);
		} else {
			return context.getQueryStringVariables().get(keyVariableValue);
		}
	}

	@Override
	public JsonObject buildResponse(final Context context, final RouteProcessor routeProcessor, final DataContainer<?> dc)
			throws NotesException {
		Document2JsonConverter d2j = new Document2JsonConverter( routeProcessor, context);
		return d2j.buildJsonFromDocument(((DocumentDataContainer) dc).getData());
	}

}
