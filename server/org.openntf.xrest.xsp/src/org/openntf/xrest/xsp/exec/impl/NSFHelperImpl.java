package org.openntf.xrest.xsp.exec.impl;

import java.util.List;

import org.openntf.xrest.xsp.exec.NSFHelper;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;

import lotus.domino.Agent;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;

public class NSFHelperImpl implements NSFHelper {
	private final Database database;

	public NSFHelperImpl(Database database) {
		super();
		this.database = database;
	}

	@Override
	public void makeDocumentAsChild(String parentId, Document doc) throws NotesException {
		Document parentDoc = database.getDocumentByUNID(parentId);
		doc.makeResponse(parentDoc);
	}

	@Override
	public void executeAgent(String agentName, Document doc) throws NotesException {
		Agent agt = database.getAgent(agentName);
		if (doc != null) {
			agt.runOnServer(doc.getNoteID());
		} else {
			agt.runOnServer();
		}
	}

	@Override
	public void executeAgent(String agentName) throws NotesException {
		executeAgent(agentName, null);
	}

	@Override
	public void computeWithForm(Document doc) throws NotesException {
		doc.computeWithForm(false, false);
	}

	@Override
	public void computeWithFormAndValidate(Document doc) throws NotesException {
		doc.computeWithForm(false, true);
	}

	@Override
	public List<?> executeFormula(String formula) throws NotesException {
		Session session = database.getParent();
		return session.evaluate(formula);
	}

	@Override
	public List<?> executeFormula(String formula, Document doc) throws NotesException {
		Session session = database.getParent();
		return session.evaluate(formula, doc);

	}

	@Override
	public JsonJavaObject createJsonObject() {
		return new JsonJavaObject();
	}

	@Override
	public JsonJavaArray createJsonArray() {
		return new JsonJavaArray();
	}

}
