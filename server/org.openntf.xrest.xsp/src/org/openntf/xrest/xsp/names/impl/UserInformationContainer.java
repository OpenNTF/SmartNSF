package org.openntf.xrest.xsp.names.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.names.UserInformation;
import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import lotus.domino.ACL;
import lotus.domino.ACLEntry;
import lotus.domino.Base;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;

public class UserInformationContainer {

	public enum NABEntryType {
		UNKNOW, PERSON, GROUP
	}

	// private List<UserInformation> allUsers = new
	// ArrayList<UserInformation>();
	private List<UserInformation> databaseUsers = new ArrayList<UserInformation>();
	private Date expires = new Date();

	public synchronized void checkDatabaseUsers(Context context) throws NotesException {
		if (expires.before(new Date()) || databaseUsers.isEmpty()) {
			databaseUsers = buildDBUsers(context);
			setExpires();
		}
	}

	private void setExpires() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, 1);
		expires = cal.getTime();
	}
	private List<UserInformation> buildDBUsers(Context context) throws NotesException {
		@SuppressWarnings("unchecked")
		List<Database> addressBooks = context.getSession().getAddressBooks();
		List<View> userViews = new ArrayList<View>();
		List<View> groupViews = new ArrayList<View>();
		initalizeViews(addressBooks, userViews, groupViews);
		Map<String, UserInformation> users = new HashMap<String, UserInformation>();
		Set<String> processedGroups = new HashSet<String>();
		processACL(context, users, processedGroups, userViews, groupViews);
		executeRecyle(userViews, groupViews);
		return new ArrayList<UserInformation>(users.values());
	}

	private void executeRecyle(List<View> userViews, List<View> groupViews) {
		NotesObjectRecycler.recycle(userViews.toArray(new Base[userViews.size()]));
		NotesObjectRecycler.recycle(groupViews.toArray(new Base[groupViews.size()]));
	}

	private void processACL(Context context, Map<String, UserInformation> users, Set<String> processedGroups, List<View> userViews, List<View> groupViews) throws NotesException {
		ACL acl = context.getSession().getCurrentDatabase().getACL();
		ACLEntry nextEntry = acl.getFirstEntry();
		while (nextEntry != null) {
			ACLEntry currentEntry = nextEntry;
			nextEntry = acl.getNextEntry();
			processACLEntry(currentEntry, users, processedGroups, userViews, groupViews);
		}

	}

	private void processACLEntry(ACLEntry entry, Map<String, UserInformation> users, Set<String> processedGroups, List<View> userViews, List<View> groupViews) throws NotesException {
		@SuppressWarnings("unchecked")
		List<String> roles = entry.getRoles();
		String name = entry.getName();
		processName(name, null, users, processedGroups, userViews, groupViews, roles);

	}

	private void processName(String name, String parentGroup, Map<String, UserInformation> users, Set<String> processedGroups, List<View> userViews, List<View> groupViews, List<String> roles)
			throws NotesException {
		Document doc = getDocument(name, userViews, groupViews);
		switch (getType(doc)) {
		case PERSON:
			UserInformation us = UserInformation.buildFormDocument(doc);
			if (!users.containsKey(us.getUserName().toLowerCase())) {
				users.put(us.getUserName().toLowerCase(), us);
			}
			if (parentGroup == null) {
				us.setRoles(new HashSet<String>(roles));
			} else {
				us.addRoles(roles);
			}
			if (parentGroup != null) {
				us.addGroup(parentGroup);
			}
			break;
		case GROUP:
			resolveGroup(doc, roles, users, processedGroups, userViews, groupViews);
			break;
		case UNKNOW:
			break;
		default:
			break;
		}
	}

	private void initalizeViews(List<Database> addressBooks, List<View> userViews, List<View> groupViews) throws NotesException {
		for (Database nab : addressBooks) {
			if (!nab.isOpen()) {
				nab.open();
			}
			if (nab.isOpen() && nab.isPublicAddressBook()) {
				View userView = nab.getView("($Users)");
				View groupView = nab.getView("($VIMGroups)");
				userViews.add(userView);
				groupViews.add(groupView);
			}
		}

	}


	private Document getDocument(String name, List<View> userViews, List<View> groupViews) throws NotesException {

		Document docRC = null;
		for (View view : userViews) {
			docRC = view.getDocumentByKey(name, true);
			if (docRC != null) {
				return docRC;
			}
		}
		for (View view : groupViews) {
			docRC = view.getDocumentByKey(name, true);
			if (docRC != null) {
				return docRC;
			}
		}
		return docRC;
	}

	private NABEntryType getType(Document doc) throws NotesException {
		if (doc == null) {
			return NABEntryType.UNKNOW;
		}
		String type = doc.getItemValueString("Type");
		String groupType = doc.getItemValueString("GroupType");
		if ("Person".equalsIgnoreCase(type)) {
			return NABEntryType.PERSON;
		}
		if ("Group".equalsIgnoreCase(type) && ("0".equals(groupType) || "2".equals(groupType))) {
			return NABEntryType.GROUP;
		}
		return NABEntryType.UNKNOW;
	}

	@SuppressWarnings("unchecked")
	private void resolveGroup(Document docGroup, List<String> roles, Map<String, UserInformation> users, Set<String> processedGroups, List<View> userViews, List<View> groupViews)
			throws NotesException {
		String groupName = docGroup.getItemValueString("ListName");
		if (!processedGroups.contains(groupName)) {
			processedGroups.add(groupName);
			List<String> members = docGroup.getItemValue("Members");
			for (String memberName : members) {
				processName(memberName, groupName, users, processedGroups, groupViews, groupViews, members);
			}
		}
	}

	public List<UserInformation> getDBUsers() {
		return databaseUsers;
	}
}
