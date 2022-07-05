package org.openntf.xrest.xsp.command;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.authendpoint.JWTExtractor;
import org.openntf.xrest.xsp.authendpoint.JwtUserInfo;
import org.openntf.xrest.xsp.authendpoint.NABUserFinder;
import org.openntf.xrest.xsp.authendpoint.Token;
import org.openntf.xrest.xsp.authendpoint.TokenFactory;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.output.JsonPayloadProcessor;
import org.openntf.xrest.xsp.model.AuthorizationEndpointDefinition;
import org.openntf.xrest.xsp.model.Router;
import org.openntf.xrest.xsp.utils.NotesContextFactory;
import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.domino.xsp.module.nsf.NotesContext;

import io.prometheus.client.Histogram;
import io.prometheus.client.Histogram.Timer;
import lotus.domino.NotesException;

public class AuthorizationHandler implements CommandHandler {

	private static final String AUTHORIZATION_PHASE = "/?authorization";
	private final TokenFactory tokenFactory;

	public AuthorizationHandler(TokenFactory tokenFactory) {
		this.tokenFactory = tokenFactory;
	}

	@Override
	public Optional<Timer> execute(HttpServletResponse resp, HttpServletRequest request, Router router,
			Histogram histogram) throws ExecutorException {
		Timer timer = histogram.labels("authorization",  request.getMethod()).startTimer();
		if (router.getAuthorizationEndpoint() == null) {
			throw new ExecutorException(500, "No Authorization Endpoint defined", AUTHORIZATION_PHASE,
					"No Authorization Endpoint defined");
		}
		AuthorizationEndpointDefinition aep = router.getAuthorizationEndpoint();
		Optional<String> headerValue = extractHeaderValue(request, aep);
		headerValue.orElseThrow(() -> new ExecutorException(400,
				"No headervalue for " + aep.getHeaderValue() + " found.", "", AUTHORIZATION_PHASE));

		JWTExtractor jwtExtractor = new JWTExtractor(headerValue.get(), aep);
		try {
			JwtUserInfo jwtUserInfo = jwtExtractor.validateAndExtract();
			NotesContext nc = NotesContextFactory.buildModifiedNotesContext();
			NABUserFinder userFinder = new NABUserFinder(aep.getAdditionalDirectoryValue());
			Optional<String> userName = userFinder.findUserName(jwtUserInfo.getEMail(), nc.getSessionAsSigner());
			userName.orElseThrow(() -> new ExecutorException(400,
					"No user found for " + jwtUserInfo.getEMail() + " found.", "", AUTHORIZATION_PHASE));
			if (!this.tokenFactory.isLoaded()) {
				this.tokenFactory.loadConfig(nc.getSessionAsSigner(), nc.getSessionAsSigner().getServerName());
			}
			Token token = this.tokenFactory.buildLTPAToken(userName.get(), aep.getSsoDomainValue());
			JsonObject result = buildResult(token, userName.get());
			JsonPayloadProcessor.INSTANCE.processJsonPayload(result, resp);
			try {
				NotesObjectRecycler.recycle(nc.getCurrentDatabase(),nc.getCurrentSession(),nc.getSessionAsSigner(),nc.getSessionAsSignerFullAdmin());
			} catch(Exception e) {
				e.printStackTrace();
			}

		} catch (NoSuchAlgorithmException | InvalidKeySpecException | NotesException | SecurityException | JsonException
				| IOException e) {
			throw new ExecutorException(500, e, "", AUTHORIZATION_PHASE);
		}
		return Optional.of(timer);
	}

	private JsonObject buildResult(Token token, String userName) {
		JsonObject result = new JsonJavaObject();
		result.putJsonProperty("userName", userName);
		result.putJsonProperty("ltpaToken", token.getLtpaToken());
		return result;
	}

	private Optional<String> extractHeaderValue(HttpServletRequest request, AuthorizationEndpointDefinition aep)
			throws ExecutorException {
		String headerValue = aep.getHeaderValue();
		if (StringUtil.isEmpty(headerValue)) {
			throw new ExecutorException(500, "No HeaderValue defined in SmartNSF", "", AUTHORIZATION_PHASE);
		}
		return Optional.ofNullable(request.getHeader(headerValue));
	}

}
