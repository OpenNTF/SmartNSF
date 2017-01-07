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
import java.util.concurrent.ExecutionException;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.RouteProcessorExecutor;
import org.openntf.xrest.xsp.exec.RouteProcessorExecutorFactory;
import org.openntf.xrest.xsp.exec.impl.ContextImpl;
import org.openntf.xrest.xsp.exec.output.ExecutorExceptionProcessor;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.NotImplementedException;
import com.ibm.domino.xsp.module.nsf.NotesContext;

public class XRestAPIServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The FacesContext factory requires a lifecycle parameter which is not
	// used,
	// but when not present, it generates
	// a NUllPointer exception. Silly thing! So we create an empty one that does
	// nothing...
	private static Lifecycle dummyLifeCycle = new Lifecycle() {

		@Override
		public void render(FacesContext context) throws FacesException {
			throw new NotImplementedException();
		}

		@Override
		public void removePhaseListener(PhaseListener listener) {
			throw new NotImplementedException();
		}

		@Override
		public PhaseListener[] getPhaseListeners() {
			throw new NotImplementedException();
		}

		@Override
		public void execute(FacesContext context) throws FacesException {
			throw new NotImplementedException();
		}

		@Override
		public void addPhaseListener(PhaseListener listener) {
			throw new NotImplementedException();
		}

	};

	private ServletConfig config;
	private FacesContextFactory contextFactory;
	private RouterFactory routerFactory;

	public XRestAPIServlet(RouterFactory routerFactory) {
		System.out.println("Servlet created...");
		this.routerFactory = routerFactory;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		this.config = config;
		contextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// FacesContext fcCurrent = initContext(req, resp);
		if (routerFactory.hasError()) {
			publishError(req, resp, routerFactory.getError());
			return;
		}
		try {
			String method = req.getMethod();
			String path = req.getPathInfo();
			RouteProcessor rp = routerFactory.getRouter().find(method, path);
			ContextImpl context = new ContextImpl();
			if (rp != null) {
				NotesContext c = NotesContext.getCurrentUnchecked();
				context.addNotesContext(c).addRequest(req).addResponse(resp);
				RouteProcessorExecutor executor = RouteProcessorExecutorFactory.getExecutor(method, path, context, rp);
				executor.execute();
			} else {
				throw new ExecutorException(500, "Path not found", path, "SERVLET");
			}
		} catch (ExecutorException ex) {
			try {
				ExecutorExceptionProcessor.INSTANCE.processExecutorException(ex, resp);
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
			// releaseContext(fcCurrent);

		}
	}

	private void publishError(HttpServletRequest req, HttpServletResponse resp, Throwable error) {
		error.printStackTrace();

	}

	public FacesContext initContext(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		return contextFactory.getFacesContext(config.getServletContext(), request, response, dummyLifeCycle);
	}

	public void releaseContext(FacesContext context) throws ServletException, IOException {
		context.release();
	}

	public void refresh() {
		routerFactory.refresh();

	}

}
