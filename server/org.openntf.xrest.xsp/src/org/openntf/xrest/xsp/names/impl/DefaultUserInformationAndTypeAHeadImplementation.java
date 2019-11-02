package org.openntf.xrest.xsp.names.impl;

import java.util.ArrayList;
import java.util.List;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.names.TypeAHeadResolver;
import org.openntf.xrest.xsp.names.UserInformation;
import org.openntf.xrest.xsp.names.UserInformationResolver;
import org.openntf.xrest.xsp.names.UserSearchScope;

import lotus.domino.NotesException;

public class DefaultUserInformationAndTypeAHeadImplementation implements UserInformationResolver, TypeAHeadResolver {

	private UserInformationContainer userInformationContainer = new UserInformationContainer();
	@Override
	public List<UserInformation> findUsers(String searchFor, UserSearchScope scope, Context context) throws NotesException {
		List<UserInformation> users = getAllUserInformationFromContainer(context, scope);
		List<UserInformation> usersFound = new ArrayList<UserInformation>();
		for(UserInformation user:users) {
			if (user.found(searchFor)) {
				usersFound.add(user);
			}
		}
		return usersFound;
	}

	@Override
	public UserInformation findUserByUserName(String userName, UserSearchScope scope, Context context) throws NotesException {
		List<UserInformation> users = getAllUserInformationFromContainer(context, scope);
		for (UserInformation user:users) {
			if (userName.equalsIgnoreCase(user.getEmail())) {
				return user;
			}
		}
		return null;
	}

	@Override
	public UserInformation findUserByEMail(String email, UserSearchScope scope, Context context) throws NotesException {
		List<UserInformation> users = getAllUserInformationFromContainer(context, scope);
		for (UserInformation user:users) {
			if (email.equalsIgnoreCase(user.getEmail())) {
				return user;
			}
		}
		return null;
	}

	@Override
	public List<UserInformation> allUser(UserSearchScope scope, Context context) throws NotesException {
		return getAllUserInformationFromContainer(context, scope);
	}

	private List<UserInformation> getAllUserInformationFromContainer(Context context, UserSearchScope scope) throws NotesException {
		this.userInformationContainer.checkDatabaseUsers(context);
		return userInformationContainer.getDBUsers();
	}

}
