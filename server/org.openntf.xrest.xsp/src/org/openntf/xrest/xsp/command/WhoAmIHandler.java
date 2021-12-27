package org.openntf.xrest.xsp.command;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.output.JsonPayloadProcessor;
import org.openntf.xrest.xsp.model.Router;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.domino.xsp.module.nsf.NotesContext;

import io.prometheus.client.Histogram;
import io.prometheus.client.Histogram.Timer;
import lotus.domino.Session;

public class WhoAmIHandler implements CommandHandler {

	@Override
	public Optional<Timer> execute(HttpServletResponse resp, HttpServletRequest request, Router router,
			Histogram histogram) throws ExecutorException {
		Timer timer = histogram.labels("whoami", request.getMethod()).startTimer();
		JsonJavaObject loginObject = new JsonJavaObject();
		try {
			NotesContext c = NotesContext.getCurrentUnchecked();
			Session ses = c.getCurrentSession();
			loginObject.put("username", ses.getEffectiveUserName());
			loginObject.put("groups", c.getGroupList());
			loginObject.put("accesslevel", c.getCurrentDatabase().getCurrentAccessLevel());
			loginObject.put("roles", c.getCurrentDatabase().queryAccessRoles(ses.getEffectiveUserName()));
			try {
				loginObject.put("email", c.getInetMail());
			} catch(Exception e) {
				//This could fail!
			}
			JsonPayloadProcessor.INSTANCE.processJsonPayload(loginObject, resp);
		} catch (Exception ex) {
			throw new ExecutorException(500, "Error during build response object", ex, request.getPathInfo(),
					"/?whoami");
		}
		return Optional.of(timer);
	}

}
