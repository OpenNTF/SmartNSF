package org.openntf.xrest.xsp.command;

import java.io.Writer;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.Router;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import io.prometheus.client.Histogram.Timer;
import io.prometheus.client.exporter.common.TextFormat;

public class MetricsHandler implements CommandHandler {
	private final CollectorRegistry registry;

	public MetricsHandler(CollectorRegistry collectorRegistry) {
		registry = collectorRegistry;
	}


	@Override
	public Optional<Timer> execute(HttpServletResponse resp, HttpServletRequest request, Router router,
			Histogram histogram) throws ExecutorException {
		processMetricsRequest(resp, request);
		return Optional.empty();
	}


	private void processMetricsRequest(HttpServletResponse resp, HttpServletRequest request) throws ExecutorException {
		try {
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType(TextFormat.CONTENT_TYPE_004);
			Writer writer = resp.getWriter();
			try {
				TextFormat.write004(writer, registry.metricFamilySamples());
				writer.flush();
			} finally {
				writer.close();
			}
		} catch (Exception e) {
			throw new ExecutorException(500, "Error during build response object", e, request.getPathInfo(),
					"/?metrics");

		}
	}
}
