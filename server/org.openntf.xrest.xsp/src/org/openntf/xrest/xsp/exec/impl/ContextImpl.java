package org.openntf.xrest.xsp.exec.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.NSFHelper;
import org.openntf.xrest.xsp.model.EventException;
import org.openntf.xrest.xsp.names.IdentityMapProvider;

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
	private Session session;
	private Session sessionAsSigner;
	private Session sessionAsSignerAdmin;
	private Database database;
	private Database databaseFromStrategy;
	private String userName;
	private List<String> groups;
	private List<String> roles;
	private JsonObject jsonPayload;
	private Map<String, String> routerVariables;
	private Map<String, String> queryStringVariables;
	private NSFHelper nsfHelper;
	private Object resultPayload;
	private boolean trace;
	private FacesContext facesContext;
	private IdentityMapProvider identityMapProvider;

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

	public ContextImpl addNotesContext(NotesContext notesContext) throws NotesException {
		this.session = notesContext.getCurrentSession();
		this.sessionAsSigner = notesContext.getSessionAsSigner();
		this.sessionAsSignerAdmin = notesContext.getSessionAsSignerFullAdmin();
		this.database = notesContext.getCurrentDatabase();
		calcSessionBasedValues();
		this.nsfHelper = new NSFHelperImpl(this.database);
		return this;
	}

	public Context addJsonPayload(JsonObject jsonObject) {
		this.jsonPayload = jsonObject;
		return this;
	}

	public Context addRouterVariables(Map<String, String> rv) {
		this.routerVariables = rv;
		return this;
	}
	public Context addQueryStringVariables(Map<String, String> qv) {
		this.queryStringVariables = qv;
		return this;
	}

	public Context addFacesContext(FacesContext fc) {
		this.facesContext = fc;
		return this;
	}

	public Context addIdentityMapProvider(IdentityMapProvider idmp) {
		this.identityMapProvider = idmp;
		return this;
	}
	
	public Context addDatabaseFromStrategy(Database db) {
		this.databaseFromStrategy = db;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getRequest()
	 */
	@Override
	public HttpServletRequest getRequest() {
		return request;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getResponse()
	 */
	@Override
	public HttpServletResponse getResponse() {
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getSession()
	 */
	@Override
	public Session getSession() {
		return session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getDatabase()
	 */
	@Override
	public Database getDatabase() {
		return database;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getUserName()
	 */
	@Override
	public String getUserName() {
		return userName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getGroups()
	 */
	@Override
	public List<String> getGroups() {
		return groups;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getRoles()
	 */
	@Override
	public List<String> getRoles() {
		return roles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getJsonPayload()
	 */
	@Override
	public JsonObject getJsonPayload() {
		return jsonPayload;
	}

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.xrest.xsp.exec.impl.IContext#getRouterVariables()
	 */
	@Override
	public Map<String, String> getRouterVariables() {
		return routerVariables;
	}

	@Override
	public boolean throwException(String message) {
		throw new EventException(message);

	}

	@Override
	public boolean throwException(String message, Throwable e) {
		throw new EventException(message, e);

	}

	@Override
	public boolean throwException(int httpStatus, String message) {
		throw new EventException(httpStatus, message);
	}

	@Override
	public boolean throwException(int httpStatus, String message, Throwable e) {
		throw new EventException(httpStatus, message, e);
	}

	@Override
	public NSFHelper getNSFHelper() {
		return nsfHelper;
	}

	@Override
	public void setResultPayload(Object resultPayLoad) {
		this.resultPayload = resultPayLoad;

	}

	@Override
	public Object getResultPayload() {
		return resultPayload;
	}

	public boolean traceEnabled() {
		return trace;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	@Override
	public FacesContext getFacesContext() {
		return facesContext;
	}

	@Override
	public Session getSessionAsSigner() {
		return this.sessionAsSigner;
	}

	@Override
	public Session getSessionAsSignerFullAdmin() {
		return this.sessionAsSignerAdmin;
	}

	@Override
	public IdentityMapProvider getIdentityMapProvider() {
		return identityMapProvider;
	}

	@Override
	public Database getDatabaseFromStrategy() {
		return this.databaseFromStrategy;
	}

	@Override
	public Map<String, String> getQueryStringVariables() {
		return this.queryStringVariables;
	}

}
