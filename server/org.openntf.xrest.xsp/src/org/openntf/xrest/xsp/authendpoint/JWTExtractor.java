package org.openntf.xrest.xsp.authendpoint;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.AuthorizationEndpointDefinition;

import com.ibm.commons.util.StringUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class JWTExtractor {
	private final String jwtHash;
	private final AuthorizationEndpointDefinition aed;
	public JWTExtractor(String jwtHash, AuthorizationEndpointDefinition aed) {
		super();
		this.jwtHash = jwtHash;
		this.aed = aed;
	}
	public JwtUserInfo validateAndExtract() throws InvalidKeySpecException, NoSuchAlgorithmException, ExecutorException {
		Jws<Claims> result= Jwts.parserBuilder()
		  .setSigningKey(this.aed.getPublicKeyValue())
		  .requireIssuer(this.aed.getIssuerValue())
		  .build()
		  .parseClaimsJws(this.jwtHash);
		if (!result.getBody().containsKey("email")) {
			throw new ExecutorException(400,"Missing attribute email in JWT Payload","/?authorization","parsejwt");
		}
		String email = (String)result.getBody().get("email");
		if (StringUtil.isEmpty(email)) {
			throw new ExecutorException(400,"Missing attribute email in JWT Payload","/?authorization","parsejwt");			
		}
		String foreignId = (String)result.getBody().get("foreignid");
		String commonName = result.getBody().getSubject();
		return new JwtUserInfo(commonName, email, foreignId);
		
	}
}
