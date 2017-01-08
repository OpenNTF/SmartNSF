package org.openntf.xrest.xsp.exec.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.model.EventException;

import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.domino.xsp.module.nsf.NotesContext;

import lotus.domino.Database;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.notes.addins.DominoServer;

public class ContextImpl implements Context {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private NotesContext notesContext;
	private Session session;
	private Database database;
	private String userName;
	private List<String> groups;
	private List<String> roles;
	private JsonObject jsonPayload;
	private Map<String,String> routerVariables;
	
	public ContextImpl() {
		
	}
	
	public ContextImpl addRequest(HttpServletRequest request) {
		this.request = request;
		return this;
	}
	
	public Context addResponse(HttpServletResponse response) {
		this.response = response;
		return this;
	}
	public ContextImpl addNotesContext(NotesContext context) throws NotesException {
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
	public Context addRouterVariables(Map<String,String> rv) {
		this.routerVariables = rv;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getRequest()
	 */
	@Override
	public HttpServletRequest getRequest() {
		return request;
	}
	/* (non-Javadoc)
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getResponse()
	 */
	@Override
	public HttpServletResponse getResponse() {
		return response;
	}
	/* (non-Javadoc)
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getSession()
	 */
	@Override
	public Session getSession() {
		return session;
	}
	/* (non-Javadoc)
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getDatabase()
	 */
	@Override
	public Database getDatabase() {
		return database;
	}
	/* (non-Javadoc)
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getUserName()
	 */
	@Override
	public String getUserName() {
		return userName;
	}
	/* (non-Javadoc)
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getGroups()
	 */
	@Override
	public List<String> getGroups() {
		return groups;
	}
	/* (non-Javadoc)
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getRoles()
	 */
	@Override
	public List<String> getRoles() {
		return roles;
	}
	/* (non-Javadoc)
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getJsonPayload()
	 */
	@Override
	public JsonObject getJsonPayload() {
		return jsonPayload;
	}
	
	/* (non-Javadoc)
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getNotesContext()
	 */

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

	/* (non-Javadoc)
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getRouterVariables()
	 */
	@Override
	public Map<String,String> getRouterVariables() {
		return routerVariables;
	}

	@Override
	public boolean throwException(String message) {
		throw new EventException(message);
		
	}

	@Override
	public boolean throwException(String message, Throwable e) {
		throw new EventException(message,e);
		
	}
	
}
