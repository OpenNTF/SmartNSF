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

public class GetByUNID extends AbstractDatabaseStrategy implements StrategyModel<DocumentDataContainer, JsonObject> {

	private String keyVariableValue;
	private Closure<?> keyVariableCl;
	private String formValue;
	private Closure<?> formCl;

	public void keyVariableName(String name) {
		this.keyVariableValue = name;
	}

	public void keyVariableName(Closure<?> keyVariableCl) {
		this.keyVariableCl = keyVariableCl;
	}

	public void form(String name) {
		this.formValue = name;
	}

	public void form(Closure<?> formCl) {
		this.formCl = formCl;
	}

	public String getKeyVariableValue(Context context) {
		if (keyVariableCl != null) {
			return (String) DSLBuilder.callClosure(keyVariableCl, context);
		} else {
			return keyVariableValue;
		}
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
			String unid = context.getRouterVariables().get(keyVariableValue);
			if (unid.equalsIgnoreCase("@new")) {
				Document doc = dbAccess.createDocument();
				String form = getFormValue(context);
				if (!StringUtil.isEmpty(form)) {
					doc.replaceItemValue("Form", form);
				}
				return new DocumentDataContainer(doc, null, dbAccess);
			}
			return new DocumentDataContainer(dbAccess.getDocumentByUNID(unid), null, dbAccess);
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
