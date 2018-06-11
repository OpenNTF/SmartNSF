package org.openntf.xrest.xsp.utils;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

public enum HttpHelper {
	INSTANCE;
	
	public String extractProtocolFromRequest(HttpServletRequest request) throws MalformedURLException {
		URL url = new URL(request.getRequestURL().toString());
		return url.getProtocol();
	}

}
