package org.openntf.xrest.xsp.names;

import org.openntf.xrest.xsp.exec.Context;

public interface IdentityMapProvider {

	public String getNotesNameForIdentity(String identity, Context context);
	public String getIdentityForNotesName(String notesName, Context context);
}
