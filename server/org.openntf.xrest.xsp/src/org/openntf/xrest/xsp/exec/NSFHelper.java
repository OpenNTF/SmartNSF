package org.openntf.xrest.xsp.exec;

import java.text.ParseException;
import java.util.List;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;

import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.NotesException;

public interface NSFHelper {

	public void makeDocumentAsChild(String parentId, Document doc) throws NotesException;

	public void executeAgent(String agentName, Document doc) throws NotesException;

	public void executeAgentInCurrentDatabase(String agentName) throws NotesException;

	public void executeAgent(String agentName) throws NotesException;

	public void computeWithForm(Document doc) throws NotesException;

	public void computeWithFormAndValidate(Document doc) throws NotesException;

	public List<?> executeFormula(String formula) throws NotesException;

	public List<?> executeFormula(String formula, Document doc) throws NotesException;

	public JsonJavaObject createJsonObject();
	
	public JsonJavaArray createJsonArray();
	
	public String buildJsonDateStringFromDocument(Document doc, String fieldName) throws NotesException;
	
	public String buildJsonDateTimeStringFromDocument(Document doc, String fieldName) throws NotesException;
	
	public DateTime buildDateTimeFromJsonDateString(String jsonDateTimeString) throws ParseException, NotesException;
}
