package org.openntf.xrest.xsp.names;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.Document;
import lotus.domino.Name;
import lotus.domino.NotesException;

public class UserInformation {

	private String userName;
	private String commonName;
	private String email;
	private Set<String> roles = new HashSet<String>();
	private Set<String> groups = new HashSet<String>();

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getRoles() {
		return new ArrayList<String>(roles);
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public List<String> getGroups() {
		return new ArrayList<String>(groups);
	}

	public void setGroups(Set<String> groups) {
		this.groups = groups;
	}

	public JsonObject toJSON() {
		JsonObject jso = new JsonJavaObject();
		jso.putJsonProperty("userName", userName);
		jso.putJsonProperty("commonName", commonName);
		jso.putJsonProperty("email", email);
		jso.putJsonProperty("roles", new ArrayList<String>(roles));
		jso.putJsonProperty("groups", new ArrayList<String>(groups));
		return jso;
	}

	public static UserInformation buildFormDocument(Document doc) throws NotesException {
		UserInformation us = new UserInformation();
		Name userName = getNotesName(doc);
		us.email = doc.getItemValueString("InternetAddress");
		us.commonName = userName.getCommon();
		us.userName = userName.getCanonical();
		userName.recycle();
		return us;
	}

	private static Name getNotesName(Document doc) throws NotesException {
		String name = (String) doc.getItemValue("fullName").elementAt(0);
		return doc.getParentDatabase().getParent().createName(name);
	}

	public void addGroup(String parentGroup) {
		groups.add(parentGroup);
	}

	public void addRoles(List<String> allRoles) {
		roles.addAll(allRoles);
	}

	public boolean found(String searchFor) {
		String searchForLC = searchFor.toLowerCase();
		return contains(commonName,searchForLC) || contains(email,searchForLC);
	}
	private boolean contains(String attributeValue, String searchForLC) {
		return !StringUtil.isEmpty(attributeValue) && attributeValue.toLowerCase().contains(searchForLC);
	}
}
