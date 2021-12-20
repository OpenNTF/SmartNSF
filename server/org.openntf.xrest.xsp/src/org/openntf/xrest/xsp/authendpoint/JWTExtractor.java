package org.openntf.xrest.xsp.authendpoint;

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
	public JwtUserInfo validateAndExtract() throws InvalidKeySpecException, NoSuchAlgorithmException {
		System.out.println(this.aed.getPublicKeyValue());
		PublicKey publicKey = getPublicKey();
		System.out.println(publicKey.toString());
		Jws<Claims> result= Jwts.parserBuilder()
		  .setSigningKey(publicKey) // <---- publicKey, not privateKey
		  .build()
		  .parseClaimsJws(this.jwtHash);
		String email = (String)result.getBody().get("email");
		String foreignId = (String)result.getBody().get("foreignid");
		String commonName = result.getBody().getSubject();
		return new JwtUserInfo(commonName, email, foreignId);
		
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
