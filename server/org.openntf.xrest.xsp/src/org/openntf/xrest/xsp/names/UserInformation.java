package org.openntf.xrest.xsp.names;

import java.util.List;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

public class UserInformation {

	private String userName;
	private String commonName;
	private String email;
	private List<String> roles;
	private List<String> groups;

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
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	
	public JsonObject toJSON() {
		JsonObject jso = new JsonJavaObject();
		jso.putJsonProperty("userName", userName);
		jso.putJsonProperty("commonName", commonName);
		jso.putJsonProperty("email", email);
		jso.putJsonProperty("roles", roles);
		jso.putJsonProperty("groups", groups);
		return jso;
	}

}
