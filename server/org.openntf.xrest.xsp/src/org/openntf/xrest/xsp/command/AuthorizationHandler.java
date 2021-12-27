package org.openntf.xrest.xsp.command;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.authendpoint.JWTExtractor;
import org.openntf.xrest.xsp.authendpoint.JwtUserInfo;
import org.openntf.xrest.xsp.authendpoint.NABUserFinder;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.AuthorizationEndpointDefinition;
import org.openntf.xrest.xsp.model.Router;
import org.openntf.xrest.xsp.utils.NotesContextFactory;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.xsp.module.nsf.NotesContext;

import io.prometheus.client.Histogram;
import io.prometheus.client.Histogram.Timer;
import lotus.domino.NotesException;

public class AuthorizationHandler implements CommandHandler {

	private static final String AUTHORIZATION_PHASE = "/?authorization";

	@Override
	public Optional<Timer> execute(HttpServletResponse resp, HttpServletRequest request, Router router, Histogram histogram) throws ExecutorException {
		Timer timer = histogram.labels("authorization").startTimer();
		if (router.getAuthorizationEndpoint() == null) {
			throw new ExecutorException(500,  "No Authorization Endpoint defined", AUTHORIZATION_PHASE, "No Authorization Endpoint defined");
		}
		AuthorizationEndpointDefinition aep = router.getAuthorizationEndpoint();
		Optional<String> headerValue = extractHeaderValue(request, aep);
		headerValue.orElseThrow(()-> new ExecutorException(400, "No headervalue for "+ aep.getHeaderValue() +" found.", "", AUTHORIZATION_PHASE));
		
		JWTExtractor jwtExtractor = new JWTExtractor(headerValue.get(), aep);
		try {
			JwtUserInfo jwtUserInfo = jwtExtractor.validateAndExtract();
			NotesContext nc = NotesContextFactory.buildModifiedNotesContext();
			NABUserFinder userFinder = new NABUserFinder(aep.getAdditionalDirectoryValue());
			Optional<String> userName =userFinder.findUserName(jwtUserInfo.getEMail(), nc.getSessionAsSigner());
			userName.orElseThrow(()-> new ExecutorException(400, "No user found for "+ jwtUserInfo.getEMail() +" found.", "", AUTHORIZATION_PHASE));
		} catch(NoSuchAlgorithmException | InvalidKeySpecException | NotesException | SecurityException e) {
			throw new ExecutorException(500,e,"", AUTHORIZATION_PHASE);
		}
		return Optional.of(timer);
	}

	private Optional<String> extractHeaderValue(HttpServletRequest request, AuthorizationEndpointDefinition aep) throws ExecutorException {
		String headerValue = aep.getHeaderValue();
		if (StringUtil.isEmpty(headerValue)) {
			throw new ExecutorException(500, "No HeaderValue defined in SmartNSF", "",AUTHORIZATION_PHASE);
		}
		return Optional.ofNullable(request.getHeader(headerValue));
	}

}
