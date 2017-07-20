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
import java.io.PrintWriter;
import java.net.URL;

import javax.faces.context.FacesContextFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.RouteProcessorExecutor;
import org.openntf.xrest.xsp.exec.RouteProcessorExecutorFactory;
import org.openntf.xrest.xsp.exec.impl.ContextImpl;
import org.openntf.xrest.xsp.exec.output.ExecutorExceptionProcessor;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.model.Router;
import org.openntf.xrest.xsp.yaml.YamlProducer;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.domino.xsp.module.nsf.NotesContext;

import lotus.domino.NotesException;

public class XRestAPIServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ServletConfig config;
	private FacesContextFactory contextFactory;
	private RouterFactory routerFactory;

	public XRestAPIServlet(final RouterFactory routerFactory) {
		this.routerFactory = routerFactory;
	}

	@Override
	public void init(final ServletConfig config) throws ServletException {
		this.config = config;
		// contextFactory = (FacesContextFactory)
		// FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
	}

	@Override
	protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		if (routerFactory.hasError()) {
			publishError(req, resp, routerFactory.getError());
			return;
		}
		try {
			String method = req.getMethod();
			String path = req.getPathInfo();
			if (StringUtil.isEmpty(path)) {
				processBuildInCommands(resp, req);
			} else {
				processRouteProcessorBased(req, resp, method, path);
			}
		} catch (ExecutorException ex) {
			try {
				ExecutorExceptionProcessor.INSTANCE.processExecutorException(ex, resp, routerFactory.getRouter().isTrace());
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

		}
	}

	private void processBuildInCommands(final HttpServletResponse resp, final HttpServletRequest request) throws JsonException, IOException,
			ExecutorException {
		if ("yaml".equals(request.getQueryString())) {
			processYamlRequest(resp, request);
			return;
		}
		if ("swagger".equals(request.getQueryString())) {
			processSwaggerRequest(resp, request);
			return;
		}
		throw new ExecutorException(500, "Path not found", request.getPathInfo(), "SERVLET");
	}

	private void processSwaggerRequest(final HttpServletResponse resp, final HttpServletRequest request) throws IOException {
		String path = request.getRequestURL().toString();
		URL url = new URL(path + "?yaml");
		URL urlSwagger = new URL(url.getProtocol(), url.getHost(), url.getPort(), "/xsp/.ibmxspres/.swaggerui/dist/index.html?url=" + url
				.toExternalForm());
		resp.sendRedirect(urlSwagger.toExternalForm());
	}

	private void processYamlRequest(final HttpServletResponse resp, final HttpServletRequest request) throws JsonException, IOException {
		Router router = routerFactory.getRouter();
		PrintWriter pw = resp.getWriter();
		YamlProducer yamlProducer = new YamlProducer(router, request, pw);
		yamlProducer.processYamlToPrintWriter();
		pw.close();
	}

	private void processRouteProcessorBased(final HttpServletRequest req, final HttpServletResponse resp, final String method,
			final String path) throws NotesException, IOException, ExecutorException {
		RouteProcessor rp = routerFactory.getRouter().find(method, path);
		ContextImpl context = new ContextImpl();
		if (rp != null) {
			NotesContext c = NotesContext.getCurrentUnchecked();
			context.addNotesContext(c).addRequest(req).addResponse(resp);
			context.addRouterVariables(rp.extractValuesFromPath(path));
			context.setTrace(routerFactory.getRouter().isTrace());
			if (req.getContentLength() > 0 && req.getContentType() != null && req.getContentType().toLowerCase().startsWith(
					"application/json")) {
				try {
					JsonJavaFactory factory = JsonJavaFactory.instanceEx2;
					JsonJavaObject json = (JsonJavaObject) JsonParser.fromJson(factory, req.getReader());
					context.addJsonPayload(json);
				} catch (JsonException jE) {
					jE.printStackTrace();
				}
			}
			RouteProcessorExecutor executor = RouteProcessorExecutorFactory.getExecutor(method, path, context, rp);
			executor.execute();
		} else {
			throw new ExecutorException(500, "Path not found", path, "SERVLET");
		}
	}

	private void publishError(final HttpServletRequest req, final HttpServletResponse resp, final Throwable error) {
		error.printStackTrace();

	}

	public void refresh() {
		routerFactory.refresh();
	}

}
