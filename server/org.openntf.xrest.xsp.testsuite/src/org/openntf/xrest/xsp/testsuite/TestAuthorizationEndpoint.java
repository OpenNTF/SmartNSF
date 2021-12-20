package org.openntf.xrest.xsp.testsuite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.junit.Test;
import org.openntf.xrest.xsp.authendpoint.JWTExtractor;
import org.openntf.xrest.xsp.authendpoint.JwtUserInfo;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.AuthorizationEndpointDefinition;
import org.openntf.xrest.xsp.model.Router;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class TestAuthorizationEndpoint extends AbstractRouterBasics {

	// NEVER to us in Production!!!
	private final String TEST_PUBLICKEY = "-----BEGIN PUBLIC KEY-----"
			+ "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3A5MO1GdDcSD8svKhxvw"
			+ "R6yd00EQ2qSrERL8vizkciDEAYh9GP7Ebx0gq77tngFyOqJCO0i7jEob8SuT9/F1"
			+ "wwQv9F/mtB5cpMv5e7kXpMjIaQuJI4mdxqt4T8dGLZ6icoTW/q7WMaqLUPzMdJif"
			+ "lpMzYRozM87eQWrmuK0hEcWS1/0SWs5fTfCUetsywPd+AVvF1QyPpcy51G9iYiZY"
			+ "n4wm9RLBdyBGawoKSkH3T72qIyJudxQLoXlqfXF6BcEQRS0NZjvZsRReykE7hvDo"
			+ "dJr3g17RlhW/Xd+XQK2qilrJNmUx7SbHTIZwqMTaR7j1XxFtQEym4g2nwxsuIDFw"
			+ "8wIDAQAB"
			+ "-----END PUBLIC KEY-----";

	private final String TEST_PRIVATEKEY = "-----BEGIN PRIVATE KEY-----"
			+ "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDcDkw7UZ0NxIPy"
			+ "y8qHG/BHrJ3TQRDapKsREvy+LORyIMQBiH0Y/sRvHSCrvu2eAXI6okI7SLuMShvx"
			+ "K5P38XXDBC/0X+a0Hlyky/l7uRekyMhpC4kjiZ3Gq3hPx0YtnqJyhNb+rtYxqotQ"
			+ "/Mx0mJ+WkzNhGjMzzt5Baua4rSERxZLX/RJazl9N8JR62zLA934BW8XVDI+lzLnU"
			+ "b2JiJlifjCb1EsF3IEZrCgpKQfdPvaojIm53FAuheWp9cXoFwRBFLQ1mO9mxFF7K"
			+ "QTuG8Oh0mveDXtGWFb9d35dAraqKWsk2ZTHtJsdMhnCoxNpHuPVfEW1ATKbiDafD"
			+ "Gy4gMXDzAgMBAAECggEAbnkLEjiNtOy0JT7j5OJExWbGMNkYYfuLGd5DXiHhad5D"
			+ "KFXiH5s++F1SGoaWed1WAFOXFnYUGYDW+EMdXLsumIHQpUlD46becakpDVDFqudV"
			+ "nU66QUoEGEna84oiFCLwgLdpy5/wTizFFhpjdiFs9MXfZF8n11mCQKsvOwVHdcig"
			+ "+NMV8o0ahl0xmH5rLGlv65UmajQ7OJSC35/OcfVupZowEkorZ5gVG1w1+QnoNJfF"
			+ "UZMZp0egMLS1RhiNSmOhtx7ClqoUBap85ctZ+5wUPG4cfEOS+923mQDE+zCXR/5c"
			+ "RAxnNLEZO/tDZrXjhFJILoZxm0UYrAPizRQYD4UykQKBgQDtZ85Gox6CHOZi224m"
			+ "pzkV2q6edNj+172VQCg14LeW0uhqpoREFss+u8/SH7c2gDCi+nf6uOTeYQT06Js6"
			+ "UmrEgJsQncBKgrl3sSS4D0c3UTt2pCd9e44Pj0FMYEdi5wzICbjq3U6HeHtk8Ex4"
			+ "QoK9A6JeszGphewxPuGQ3d9JWwKBgQDtSp227AViLev/FJFNGIxtaynZ0BYEqA8p"
			+ "pryiUXzR+cMuSSuavEx/JjXz7vDsKYGbNCWvPZo5FX9MXA+XWGL0Z9djQSjVw4bi"
			+ "RY/UF/cBHUEqkCVeRRYQ8ECcP+3fzgZVIJ913wRgIFLKaGMDxLmgz0ltYyMw9Ew8"
			+ "cKQsQoFySQKBgBevBWsSltaQH4O5NFHy2MUPXeA88/DogOdE0T4evvHy9HC/T+6i"
			+ "/3+wqNjjhRmQzElWB2Yhz+NHdoEgO2wkELatxcpwkmBcjjgcmfcqvwYLuozpb28b"
			+ "Rl7TAcamzDhXqHTEU4hKr6zgHh9Bwb81k2lb1XTxF4E79QYsqcAUAlSFAoGAK9yI"
			+ "/pDuBdHbN5FDsm6BNpCCceKGz2GJO/e4EoDhg6aZL2sTBk0cDryvaGGYYu7hKCEh"
			+ "fhzy7u5MU38I2r7Zu7eeFpiqxaiYvCaDiX7Mh4Yyz31Jiv1WMNdX+gnPHHlmrkGT"
			+ "eCQ7GNGgvA1DkCTC8zidp/yVOq1NkjZ4Aj5g0xECgYEAh2qcQ1uh16poCCqiUJjP"
			+ "6htvOzdDhPyvG5kCpYQ5sBoAldJoordJIFdabLpuDm3EEf2r2VN3KRO6mGAN3oAw"
			+ "AdZHc+/LY69SjqCqLyPcyQ3LRctX9oqorHcRl6SXz71ekn4KLueToVH62jwhCG4e"
			+ "l8jP9RdkHJsptlV+Hjorlok="
			+ "-----END PRIVATE KEY-----";

	@Override
	protected String getRouterDSLFileName() {
		return "authorizationendpoint_router.groovy";
	}

	@Test
	public void testLoadEndpointDefinition() throws ExecutorException {
		Router router = getRouter();
		assertNotNull(router);
		assertNotNull(router.getAuthorizationEndpoint());
		AuthorizationEndpointDefinition aep = router.getAuthorizationEndpoint();
		assertEquals(".smart.nsf", aep.getSsoDomainValue());
		assertEquals(2, aep.getAdditionalDirectoryValue().size());
		assertNotNull(aep.getOnUserNotFoundClosure());
		assertEquals("smartnsf-auth", aep.getHeaderValue());
		assertEquals(TEST_PUBLICKEY, aep.getPublicKeyValue());
	}

	@Test
	public void testDecodeHeaderValueWithPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
		Router router = getRouter();
		String headerValue = buildHeaderValue();
		System.out.println(headerValue);
		JWTExtractor jwtExtractor = new JWTExtractor(headerValue, router.getAuthorizationEndpoint());
		JwtUserInfo jwtUserInfo = jwtExtractor.validateAndExtract();
		assertNotNull(jwtUserInfo);
		assertEquals("John.Doe@smart.nsf",jwtUserInfo.getEMail());

	}

	private String buildHeaderValue() throws NoSuchAlgorithmException, InvalidKeySpecException {
		PrivateKey key = buildPrivateKey();
		Map<String,Object> claims = new HashMap<String, Object>();
		claims.put("email", "John.Doe@smart.nsf");
		claims.put("foreignid", "007-008-009");
		String jws = Jwts.builder()

				.setIssuer("iss.smartnsf.proxy").setSubject("Johne Doy").setAudience("you")
				// .setExpiration(expiration) //a java.util.Date
				// .setNotBefore(notBefore) //a java.util.Date
				.setIssuedAt(new Date()) // for example, now
				.setId(UUID.randomUUID().toString()) // just an example id
				.addClaims(claims)
				.signWith(key).compact();
		return jws;
	}

	private String buildHeaderValueNoEmail() throws NoSuchAlgorithmException, InvalidKeySpecException {
		PrivateKey key = buildPrivateKey();
		Map<String,Object> claims = new HashMap<String, Object>();
		claims.put("foreignid", "007-008-009");
		String jws = Jwts.builder()

				.setIssuer("iss.smartnsf.proxy").setSubject("Johne Doy").setAudience("you")
				// .setExpiration(expiration) //a java.util.Date
				// .setNotBefore(notBefore) //a java.util.Date
				.setIssuedAt(new Date()) // for example, now
				.setId(UUID.randomUUID().toString()) // just an example id
				.addClaims(claims)
				.signWith(key).compact();
		return jws;
	}

	private PrivateKey buildPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		String rsaPrivateKey = TEST_PRIVATEKEY;
		rsaPrivateKey = rsaPrivateKey.replace("-----BEGIN PRIVATE KEY-----", "");
		rsaPrivateKey = rsaPrivateKey.replace("-----END PRIVATE KEY-----", "");

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(rsaPrivateKey));
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey privKey = kf.generatePrivate(keySpec);
		return privKey;
	}
}
