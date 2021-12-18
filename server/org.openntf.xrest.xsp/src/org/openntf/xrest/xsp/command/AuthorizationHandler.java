package org.openntf.xrest.xsp.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.Router;

import io.prometheus.client.Histogram;
import io.prometheus.client.Histogram.Timer;

public class AuthorizationHandler implements CommandHandler {

	@Override
	public Timer execute(HttpServletResponse resp, HttpServletRequest request, Router router, Histogram histogram) throws ExecutorException {
		Timer timer = histogram.labels("authorization").startTimer();
		if (router.getAuthorizationEndpoint() == null) {
			throw new ExecutorException(500,  "No Authorization Endpoint defined", "/?authorization", "No Authorization Endpoint defined");
		}
		
		return timer;
	}

}
