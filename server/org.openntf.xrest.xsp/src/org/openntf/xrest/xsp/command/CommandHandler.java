package org.openntf.xrest.xsp.command;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.Router;

import io.prometheus.client.Histogram;
import io.prometheus.client.Histogram.Timer;

@FunctionalInterface
public interface CommandHandler {

	Optional<Timer> execute(final HttpServletResponse resp, final HttpServletRequest request, Router router, Histogram histogram) throws ExecutorException;
}
