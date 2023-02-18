package org.openntf.xrest.xsp.authendpoint;

public class JwtUserInfo {
	private final String commonName;
	private final String eMail;
	private final String foreignId;
	public JwtUserInfo(String commonName, String eMail, String foreignId) {
		super();
		this.commonName = commonName;
		this.eMail = eMail;
		this.foreignId = foreignId;
	}
	public String getCommonName() {
		return commonName;
	}
	public String getEMail() {
		return eMail;
	}
	public String getForeignId() {
		return foreignId;
	}
	

}
