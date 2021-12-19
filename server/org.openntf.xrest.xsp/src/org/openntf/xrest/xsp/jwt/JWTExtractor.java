package org.openntf.xrest.xsp.jwt;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.openntf.xrest.xsp.model.AuthorizationEndpointDefinition;

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
	public Jws<Claims> validateAndExtract() throws InvalidKeySpecException, NoSuchAlgorithmException {
		System.out.println(this.aed.getPublicKeyValue());
		PublicKey publicKey = getPublicKey();
		System.out.println(publicKey.toString());
		 return Jwts.parserBuilder()
		  .setSigningKey(publicKey) // <---- publicKey, not privateKey
		  .build()
		  .parseClaimsJws(this.jwtHash);
		
	}
	
	private PublicKey getPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
		String rsaPublicKey = this.aed.getPublicKeyValue();
		rsaPublicKey = rsaPublicKey.replace("-----BEGIN PUBLIC KEY-----", "");
	    rsaPublicKey = rsaPublicKey.replace("-----END PUBLIC KEY-----", "");
	    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(rsaPublicKey));
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    PublicKey publicKey = kf.generatePublic(keySpec);
	    return publicKey;
	}
	
	

}
