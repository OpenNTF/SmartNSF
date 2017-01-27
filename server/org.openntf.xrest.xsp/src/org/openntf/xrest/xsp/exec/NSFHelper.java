package org.openntf.xrest.xsp.exec;

import lotus.domino.Document;
import lotus.domino.NotesException;

public interface NSFHelper {

	public boolean makeDocumentAsChild(String parentId, Document doc) throws NotesException;

	public boolean executeAgent(String agentName, Document doc) throws NotesException;

	public boolean executeAgent(String agentName) throws NotesException;
	
	public boolean computeWithForm(Document doc) throws NotesException;

	public boolean computeWithFormAndValidate(Document doc) throws NotesException;

}
