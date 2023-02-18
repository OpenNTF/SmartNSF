package org.openntf.xrest.xsp.command;

import javax.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface CommandMatcher {

	boolean match(HttpServletRequest request);
}
