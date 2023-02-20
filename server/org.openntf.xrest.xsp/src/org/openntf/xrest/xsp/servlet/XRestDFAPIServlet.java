/*
 * Copyright 2013, WebGate Consulting AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package org.openntf.xrest.xsp.servlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.faces.context.FacesContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.openntf.xrest.xsp.command.CommandDefinition;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.RouteProcessorExecutor;
import org.openntf.xrest.xsp.exec.RouteProcessorExecutorFactory;
import org.openntf.xrest.xsp.exec.impl.ContextImpl;
import org.openntf.xrest.xsp.exec.output.ExecutorExceptionProcessor;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.model.Router;
import org.openntf.xrest.xsp.utils.NotesContextFactory;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.context.FacesContextExImpl;
import com.ibm.xsp.webapp.DesignerFacesServlet;

import io.prometheus.client.Histogram;
import io.prometheus.client.Histogram.Timer;
import lotus.domino.NotesException;

public class XRestDFAPIServlet extends DesignerFacesServlet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private RouterFactory routerFactory;
	private Histogram histogram;

	public XRestDFAPIServlet(final RouterFactory routerFactory) {
		this.routerFactory = routerFactory;
	}

	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
		histogram = routerFactory.getHistogram();
	}

	@Override
	public void service(final ServletRequest servletRequest, final ServletResponse servletResponse)
			throws ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest) servletRequest;
		HttpServletResponse resp = (HttpServletResponse) servletResponse;
		if (routerFactory.hasError()) {
			publishError(req, resp, routerFactory.getError());
			return;
		}
		Timer timer = null;
		Router router = routerFactory.getRouter();
		FacesContext fc = null;
		try {
			if (router.useFacesContext()) {
				fc = (FacesContextExImpl) this.getFacesContext(req, resp);
			}
			String method = req.getMethod();
			String path = req.getPathInfo();
			if (router.isEnableCORS()) {
				processCORSHeaders(req, resp, router, method);
			}
			if (StringUtil.isEmpty(path)) {
				timer = processBuildInCommands(resp, req, router);
			} else {
				timer = processRouteProcessorBased(req, resp, method, path, fc);
			}
		} catch (ExecutorException ex) {
			try {
				ExecutorExceptionProcessor.INSTANCE.processExecutorException(ex, resp, router.isTrace());
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception ex) {
			try {
				ExecutorExceptionProcessor.INSTANCE.processGeneralException(500, ex, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} finally {
			if (timer != null) {
				timer.observeDuration();
			}
			if (!resp.isCommitted()) {
				try {
					resp.getOutputStream().close();
				} catch (IllegalStateException stateException) {
					resp.getWriter().close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				if (fc != null) {
					fc.responseComplete();
					fc.release();
				}
			} catch (Exception ex) {

			}
		}
	}

	private void processCORSHeaders(HttpServletRequest req, HttpServletResponse resp, Router router, String method) {
		if ("OPTIONS".equals(method)) {
			resp.addHeader("Access-Control-Allow-Headers",
					"origin, content-type, accept, " + router.getCORSTokenHeader());
		}
		if (router.isCORSAllowCredentials()) {
			resp.addHeader("Access-Control-Allow-Credentials", "true");
		}
		resp.addHeader("Access-Control-Allow-Origin", toColonValue(router.getCORSOrginValue()));
		resp.addHeader("Access-Control-Allow-Methods", toColonValue(router.getCORSAllowMethodValue()));
	}

	private String toColonValue(List<String> corsOrginValue) {
		StringBuilder sb = new StringBuilder();
		if (corsOrginValue.isEmpty()) {
			return "";
		}
		for (String value : corsOrginValue) {
			sb.append(value);
			sb.append(",");
		}
		return sb.substring(0, sb.length() - 1);
	}

	private Timer processBuildInCommands(final HttpServletResponse resp, final HttpServletRequest request,
			Router router) throws ExecutorException {
		String queryString = request.getQueryString();
		if (StringUtil.isEmpty(queryString)) {
			throw new ExecutorException(500, "Path not found and no built-in command found.", request.getPathInfo(),
					"SERVLET");
		}
		Optional<CommandDefinition> command = routerFactory.findCommand(request);
		if (command.isPresent()) {
			return command.get().execute(request, resp, router, histogram);
		}

		throw new ExecutorException(500, queryString + " is not a built-in command.", request.getPathInfo(), "SERVLET");
	}

	private Timer processRouteProcessorBased(final HttpServletRequest req, final HttpServletResponse resp,
			final String method, final String path, FacesContext fc)
			throws NotesException, IOException, ExecutorException {
		RouteProcessor rp = routerFactory.getRouter().find(method, path);
		if (rp != null) {
			Timer timer = histogram.labels(rp.getRoute(), rp.getMethod()).startTimer();
			ContextImpl context = new ContextImpl();
			NotesContext c = NotesContextFactory.buildModifiedNotesContext();
			context.addNotesContext(c).addRequest(req).addResponse(resp);
			context.addRouterVariables(rp.extractValuesFromPath(path));
			context.addQueryStringVariables(rp.extractValuesFromQueryString(req.getQueryString()));
			context.setTrace(routerFactory.getRouter().isTrace());
			context.addFacesContext(fc);
			context.addIdentityMapProvider(routerFactory.getRouter().getIdentityMapProviderValue());
			if (req.getContentLength() > 0 && req.getContentType() != null
					&& req.getContentType().toLowerCase().startsWith("application/json")) {
				try {
					String payloadValue = IOUtils.toString(req.getInputStream(), "UTF-8");
					JsonJavaFactory factory = JsonJavaFactory.instanceEx2;

					Object pl = JsonParser.fromJson(factory, payloadValue);
					if (pl instanceof JsonJavaObject) {
						context.addJsonPayload((JsonJavaObject) pl);
					} else {
						context.addJsonPayloadAsArray((JsonJavaArray) pl);
					}
				} catch (JsonException jE) {
					jE.printStackTrace();
				} finally {
					req.getInputStream().close();
				}
			}
			RouteProcessorExecutor executor = RouteProcessorExecutorFactory.getExecutor(method, path, context, rp);
			executor.execute(context, rp);
			try {
				context.cleanUp();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return timer;
		} else {
			throw new ExecutorException(500, "Path not found", path, "SERVLET");
		}
	}

	private void publishError(final HttpServletRequest req, final HttpServletResponse resp, final Throwable error) {
		error.printStackTrace();

	}

	@Override
	public void destroy() {
		super.destroy();
		this.histogram = null;
		this.routerFactory = null;
	}
}
