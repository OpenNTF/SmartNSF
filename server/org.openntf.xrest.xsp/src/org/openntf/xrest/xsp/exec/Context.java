package org.openntf.xrest.xsp.exec;

import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.names.IdentityMapProvider;

import com.ibm.commons.util.io.json.JsonArray;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Database;
import lotus.domino.Session;

public interface Context {

	HttpServletRequest getRequest();

	HttpServletResponse getResponse();

	Session getSession();

	Session getSessionAsSigner();

	Session getSessionAsSignerFullAdmin();

	Database getDatabase();

	Database getDatabaseFromStrategy();

	String getUserName();

	List<String> getGroups();

	List<String> getRoles();

	JsonObject getJsonPayload();

	JsonArray getJsonPayloadAsArray();

	Map<String, String> getRouterVariables();

	Map<String, String> getQueryStringVariables();

	boolean throwException(String message);

	boolean throwException(String message, Throwable e);

	boolean throwException(int httpStatus, String message);

	boolean throwException(int httpStatus, String message, Throwable e);

	NSFHelper getNSFHelper();

	void setResultPayload(Object resultPayLoad);

	Object getResultPayload();

	boolean traceEnabled();

	FacesContext getFacesContext();

	IdentityMapProvider getIdentityMapProvider();

	public void cleanUp();
}