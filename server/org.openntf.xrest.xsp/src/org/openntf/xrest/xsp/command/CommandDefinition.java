package org.openntf.xrest.xsp.command;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.Router;

import io.prometheus.client.Histogram;
import io.prometheus.client.Histogram.Timer;

public class CommandDefinition {

	private final CommandMatcher commandMatcher;
	private final CommandHandler commandHandler;
	public CommandDefinition(CommandMatcher commandMatcher, CommandHandler commandHandler) {
		super();
		this.commandMatcher = commandMatcher;
		this.commandHandler = commandHandler;
	}
	
	public boolean match(HttpServletRequest request) {
		return commandMatcher.match(request);
	}
	
	public Timer execute(HttpServletRequest request, HttpServletResponse response, Router router, Histogram histogram) throws ExecutorException {
		Optional<Timer> timer = commandHandler.execute(response, request, router, histogram);
		return timer.isPresent() ? timer.get():null;
	}
}
