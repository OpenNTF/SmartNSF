package org.openntf.xrest.xsp.names;

import java.util.List;

import org.openntf.xrest.xsp.exec.Context;

import lotus.domino.NotesException;

public interface UserInformationResolver {
	UserInformation findUserByUserName(String userName, UserSearchScope scope, Context context) throws NotesException;
	UserInformation findUserByEMail(String email, UserSearchScope scope, Context context) throws NotesException;
	List<UserInformation> allUser(UserSearchScope scope, Context context) throws NotesException;

	
}
