package org.openntf.xrest.xsp.exec;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.domino.xsp.module.nsf.NotesContext;

import lotus.domino.Database;
import lotus.domino.Session;

public interface Context {

	HttpServletRequest getRequest();

	HttpServletResponse getResponse();

	Session getSession();

	Database getDatabase();

	String getUserName();

	List<String> getGroups();

	List<String> getRoles();

	JsonObject getJsonPayload();

	NotesContext getNotesContext();

	Map<String, String> getRouterVariables();

}