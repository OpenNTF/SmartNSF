package org.openntf.xrest.xsp.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import groovy.lang.Closure;

public class AuthorizationEndpointDefinition {
	private List<String> additionalDirectoryValue = Collections.emptyList();
	private String publicKeyValue;
	private Closure<?> onUserNotFoundClosure;
	private String ssoDomainValue;
	private String headerValue;
	private String issuerValue;
	
	public String getIssuerValue() {
		return issuerValue;
	}
	public void additionalDirectories(String[] directories) {
		this.additionalDirectoryValue = Arrays.asList(directories);
	}
	public void publicKey(String pk) {
		this.publicKeyValue = pk;
	}
	
	public void onUserNotFound(Closure<?> cl) {
		this.onUserNotFoundClosure = cl;
	}
	
	public void ssoDomain(String ssoDomain) {
		this.ssoDomainValue = ssoDomain;
	}
	public void header(String header) {
		this.headerValue = header;
	}
	public void issuer(String issuer) {
		this.issuerValue = issuer;
	}
	
	public List<String> getAdditionalDirectoryValue() {
		return additionalDirectoryValue;
	}
	public String getPublicKeyValue() {
		return publicKeyValue;
	}
	public Closure<?> getOnUserNotFoundClosure() {
		return onUserNotFoundClosure;
	}
	public String getSsoDomainValue() {
		return ssoDomainValue;
	}
	public String getHeaderValue() {
		return headerValue;
	}
}
