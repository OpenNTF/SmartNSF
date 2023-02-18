package org.openntf.xrest.xsp.utils;

import java.lang.reflect.Field;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.impl.ContextImpl;

import com.ibm.domino.xsp.module.nsf.NotesContext;

import lotus.domino.NotesException;

public class NotesContextFactory {

	public static NotesContext buildModifiedNotesContext() {
		NotesContext c = NotesContext.getCurrentUnchecked();
		try {
			Field checkedSigners = NotesContext.class.getDeclaredField("checkedSigners");
			checkedSigners.setAccessible(true);
			HashSet<?> signers = (HashSet<?>) checkedSigners.get(c);
			signers.clear();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		c.setSignerSessionRights("WEB-INF/routes.groovy");
		return c;
	}
	public static Context createSimpleContext(HttpServletRequest request, HttpServletResponse response) throws NotesException {
		NotesContext c = buildModifiedNotesContext();
		ContextImpl context = new ContextImpl();
		context.addNotesContext(c).addRequest(request).addResponse(response);
		return context;
	}
}
