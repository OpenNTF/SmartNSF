package org.openntf.xrest.xsp.names;

import java.util.List;

import org.openntf.xrest.xsp.exec.Context;

public interface TypeAHeadResolver {

	List<UserInformation> findUsers(String searchFor, UserSearchScope scope, Context context);	
}
