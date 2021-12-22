package org.openntf.xrest.xsp.command;

import java.net.URL;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.Router;

import io.prometheus.client.Histogram;
import io.prometheus.client.Histogram.Timer;

public class SwaggerHandler implements CommandHandler {

	@Override
	public Optional<Timer> execute(HttpServletResponse resp, HttpServletRequest request, Router router,
			Histogram histogram) throws ExecutorException {
		Timer timer = histogram.labels("swagger", request.getMethod()).startTimer();
		try {
			String path = request.getRequestURL().toString();
			URL url = new URL(path + "?yaml");
			URL urlSwagger = new URL(url.getProtocol(), url.getHost(), url.getPort(),
					"/xsp/.ibmxspres/.swaggerui/dist/index.html?url=" + url.toExternalForm());
			resp.sendRedirect(urlSwagger.toExternalForm());
		} catch (Exception e) {
			throw new ExecutorException(500, e, "/?swagger", "");
		}
		return Optional.of(timer);
	}

}
