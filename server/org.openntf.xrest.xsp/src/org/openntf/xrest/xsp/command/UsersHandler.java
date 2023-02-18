package org.openntf.xrest.xsp.command;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.Router;
import org.openntf.xrest.xsp.names.UserAndGroupHandler;
import org.openntf.xrest.xsp.utils.NotesContextFactory;

import io.prometheus.client.Histogram;
import io.prometheus.client.Histogram.Timer;

public class UsersHandler implements CommandHandler {

	@Override
	public Optional<Timer> execute(HttpServletResponse resp, HttpServletRequest request, Router router,
			Histogram histogram) throws ExecutorException {
		Timer timer = histogram.labels("users", request.getMethod()).startTimer();
		try {
			Context context = NotesContextFactory.createSimpleContext(request, resp);
			UserAndGroupHandler handler = new UserAndGroupHandler(resp, router.getTypeAHeadResolverValue(),
					router.getUserInformationResolverValue(), context);
			handler.execute(request.getQueryString());
		} catch (Exception e) {
			throw new ExecutorException(500, e, "/?users", "");
		}
		return Optional.of(timer);
	}

}
