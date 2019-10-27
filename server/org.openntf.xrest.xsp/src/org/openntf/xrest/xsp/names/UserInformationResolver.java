package org.openntf.xrest.xsp.names;

import java.util.List;

import org.openntf.xrest.xsp.exec.Context;

public interface UserInformationResolver {
	UserInformation findUserByUserName(String userName, UserSearchScope scope, Context context);
	UserInformation findUserByEMail(String userName, UserSearchScope scope, Context context);
	List<UserInformation> allUser(UserSearchScope scope, Context context);

	
}
