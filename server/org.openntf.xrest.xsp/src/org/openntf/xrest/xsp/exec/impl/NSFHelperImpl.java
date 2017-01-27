package org.openntf.xrest.xsp.exec.impl;

import org.openntf.xrest.xsp.exec.NSFHelper;

import lotus.domino.Agent;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;

public class NSFHelperImpl implements NSFHelper {
	private final Database database;

	
	public NSFHelperImpl(Database database) {
		super();
		this.database = database;
	}

	@Override
	public boolean makeDocumentAsChild(String parentId, Document doc) throws NotesException {
		Document parentDoc = database.getDocumentByUNID(parentId);
		doc.makeResponse(parentDoc);
		return true;
	}

	@Override
	public boolean executeAgent(String agentName, Document doc) throws NotesException {
		Agent agt = database.getAgent(agentName);
		if (doc != null) {
			agt.runOnServer(doc.getNoteID());
		} else {
			agt.runOnServer();
		}
		return true;
	}

	@Override
	public boolean executeAgent(String agentName) throws NotesException {
		return executeAgent(agentName, null);
	}

	@Override
	public boolean computeWithForm(Document doc ) throws NotesException {
		boolean rc = doc.computeWithForm(false, false);
		if (rc) {
			doc.save(true,false,true);
		}
		return rc;
	}

	@Override
	public boolean computeWithFormAndValidate(Document doc) throws NotesException {
		boolean rc= doc.computeWithForm(false, true);
		if (rc) {
			doc.save(true,false,true);
		}
		return rc;
	}

	
}
