package org.openntf.xrest.xsp.names;

import java.util.List;

import org.openntf.xrest.xsp.exec.Context;

import lotus.domino.NotesException;

public interface TypeAHeadResolver {

	List<UserInformation> findUsers(String searchFor, UserSearchScope scope, Context context) throws NotesException;	
}
