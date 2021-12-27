package org.openntf.xrest.xsp.authendpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

public class NABUserFinder {
	private final List<String> addressBooks;

	public NABUserFinder(List<String> additionAddressBooks) {
		this.addressBooks = new ArrayList<>();
		this.addressBooks.add("names.nsf");
		if (additionAddressBooks != null) {
			this.addressBooks.addAll(additionAddressBooks);
		}
	}

	public Optional<String> findUserName(String eMail, Session session) throws NotesException {
		for (String path : this.addressBooks) {
			Database db = getDatabase(path, session);
			View users = db.getView("($Users)");
			Document userDocument = users.getDocumentByKey(eMail, true);
			try {
				if (userDocument != null) {
					String userName = (String)userDocument.getItemValue("FullName").elementAt(0);
					return Optional.of(userName);
				}
			} finally {
				NotesObjectRecycler.recycle(userDocument,users, db);
			}
		}
		return Optional.empty();
	}

	public Database getDatabase(String path, Session session) throws NotesException {
		if (path.contains("!!")) {
			String[] arrDB = path.split("!!");
			return session.getDatabase(arrDB[0], arrDB[1]);
		} else {
			return session.getDatabase(session.getServerName(), path);
		}

	}

}
