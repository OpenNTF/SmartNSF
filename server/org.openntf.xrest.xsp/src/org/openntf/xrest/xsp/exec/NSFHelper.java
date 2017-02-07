package org.openntf.xrest.xsp.exec;

import java.util.List;

import lotus.domino.Document;
import lotus.domino.NotesException;

public interface NSFHelper {

	public void makeDocumentAsChild(String parentId, Document doc) throws NotesException;

	public void executeAgent(String agentName, Document doc) throws NotesException;

	public void executeAgent(String agentName) throws NotesException;

	public void computeWithForm(Document doc) throws NotesException;

	public void computeWithFormAndValidate(Document doc) throws NotesException;

	public List<?> executeFormula(String formula) throws NotesException;

	public List<?> executeFormula(String formula, Document doc) throws NotesException;

}
