package org.openntf.xrest.xsp.exec.impl;

import java.util.List;

import org.openntf.xrest.xsp.exec.NSFHelper;
import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;

import lotus.domino.Agent;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;

public class NSFHelperImpl implements NSFHelper {
	private final Database database;
	private Database databaseFromStrategy;

	public NSFHelperImpl(Database database) {
		super();
		this.database = database;
	}

	public void setDatabaseFromStrategy(Database dbFromStrategy) {
		this.databaseFromStrategy = dbFromStrategy;
	}
	@Override
	public void makeDocumentAsChild(String parentId, Document doc) throws NotesException {
		Document parentDoc = database.getDocumentByUNID(parentId);
		doc.makeResponse(parentDoc);
		NotesObjectRecycler.recycle(parentDoc);
	}

	@Override
	public void executeAgentInCurrentDatabase(String agentName) throws NotesException {
		Agent agt = database.getAgent(agentName);
		agt.runOnServer();
		NotesObjectRecycler.recycle(agt);
	}

	@Override
	public void executeAgent(String agentName, Document doc) throws NotesException {
		Agent agt =getAgent(agentName);
		if (doc != null) {
			agt.runOnServer(doc.getNoteID());
		} else {
			agt.runOnServer();
		}
		NotesObjectRecycler.recycle(agt);
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
	
	private Agent getAgent(String agentName) throws NotesException {
		return database.equals(databaseFromStrategy) || this.databaseFromStrategy == null ? database.getAgent(agentName) : databaseFromStrategy.getAgent(agentName);
	}

}
