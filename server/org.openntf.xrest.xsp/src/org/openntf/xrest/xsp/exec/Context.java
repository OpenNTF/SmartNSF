package org.openntf.xrest.xsp.exec;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.domino.xsp.module.nsf.NotesContext;

import lotus.domino.Database;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.notes.addins.DominoServer;

public class Context {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private NotesContext notesContext;
	private Session session;
	private Database database;
	private String userName;
	private List<String> groups;
	private List<String> roles;
	private JsonObject jsonPayload;
	
	public Context() {
		
	}
	
	public Context addRequest(HttpServletRequest request) {
		this.request = request;
		return this;
	}
	
	public Context addResponse(HttpServletResponse response) {
		this.response = response;
		return this;
	}
	public Context addNotesContext(NotesContext context) throws NotesException {
		this.notesContext = context;
		this.session = notesContext.getCurrentSession();
		this.database = notesContext.getCurrentDatabase();
		calcSessionBasedValues();
		return this;
	}
	public Context addJsonPayload(JsonObject jsonObject) {
		this.jsonPayload = jsonObject;
		return this;
	}

	public HttpServletRequest getRequest() {
		return request;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public Session getSession() {
		return session;
	}
	public Database getDatabase() {
		return database;
	}
	public String getUserName() {
		return userName;
	}
	public List<String> getGroups() {
		return groups;
	}
	public List<String> getRoles() {
		return roles;
	}
	public JsonObject getJsonPayload() {
		return jsonPayload;
	}
	
	public NotesContext getNotesContext() {
		return notesContext;
	}

	@SuppressWarnings("unchecked")
	private void calcSessionBasedValues() throws NotesException {
		String userName = session.getEffectiveUserName();
		Name userNotesName = session.createName(userName);
		DominoServer ds = new lotus.notes.addins.DominoServer(database.getServer());
		this.groups = new ArrayList<String>(ds.getNamesList(userNotesName.getCanonical()));
		this.roles = new ArrayList<String>(database.queryAccessRoles(userNotesName.getAbbreviated()));
		this.userName = userName;
		userNotesName.recycle();
		
	}
	
}
