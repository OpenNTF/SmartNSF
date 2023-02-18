package org.openntf.xrest.xsp.command;

import java.io.PrintWriter;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.Router;
import org.openntf.xrest.xsp.yaml.YamlProducer;

import io.prometheus.client.Histogram;
import io.prometheus.client.Histogram.Timer;

public class YamlHandler implements CommandHandler {

	@Override
	public Optional<Timer> execute(HttpServletResponse resp, HttpServletRequest request, Router router,
			Histogram histogram) throws ExecutorException {
		Timer timer = histogram.labels("yaml", request.getMethod()).startTimer();
		try {
			PrintWriter pw = resp.getWriter();
			YamlProducer yamlProducer = new YamlProducer(router, request, pw);
			yamlProducer.processYamlToPrintWriter();
			pw.close();
		} catch (Exception e) {
			throw new ExecutorException(500, e, "/?yaml", "");
		}
		return Optional.of(timer);
	}
}
