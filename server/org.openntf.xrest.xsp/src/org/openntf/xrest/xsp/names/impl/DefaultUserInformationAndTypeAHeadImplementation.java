package org.openntf.xrest.xsp.names.impl;

import java.util.List;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.names.TypeAHeadResolver;
import org.openntf.xrest.xsp.names.UserInformation;
import org.openntf.xrest.xsp.names.UserInformationResolver;
import org.openntf.xrest.xsp.names.UserSearchScope;

public class DefaultUserInformationAndTypeAHeadImplementation implements UserInformationResolver, TypeAHeadResolver {

	@Override
	public List<UserInformation> findUsers(String searchFor, UserSearchScope scope, Context context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserInformation findUserByUserName(String userName, UserSearchScope scope, Context context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserInformation findUserByEMail(String userName, UserSearchScope scope, Context context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserInformation> allUser(UserSearchScope scope, Context context) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
