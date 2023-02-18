package org.openntf.xrest.xsp.model;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.openntf.xrest.xsp.exec.ExecutorException;

import groovy.lang.Closure;

public class AuthorizationEndpointDefinition {
	private List<String> additionalDirectoryValue = Collections.emptyList();
	private String publicKeyAsString;
	private PublicKey publicKeyValue;
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
		this.publicKeyAsString = pk;
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

	public PublicKey getPublicKeyValue() {
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

	public void startup() throws ExecutorException {
		try {
			String rsaPublicKey = this.publicKeyAsString;
			rsaPublicKey = rsaPublicKey.replace("-----BEGIN PUBLIC KEY-----", "");
			rsaPublicKey = rsaPublicKey.replace("-----END PUBLIC KEY-----", "");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(rsaPublicKey));
			KeyFactory kf = KeyFactory.getInstance("RSA");
			this.publicKeyValue = kf.generatePublic(keySpec);
		} catch (Exception e) {
			throw new ExecutorException(500, e, "Could not parse public key", "servletInit");
		}
	}
}
