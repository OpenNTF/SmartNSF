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

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.ibm.designer.runtime.domino.adapter.ComponentModule;
import com.ibm.designer.runtime.domino.adapter.IServletFactory;
import com.ibm.designer.runtime.domino.adapter.ServletMatch;

public class XRestAPIServletFactory implements IServletFactory {

	private ComponentModule module;
	public static final String SERVLET_PATH = "/xsp/.xrest/";
	private XRestAPIServlet servlet;
	private long lastUpdate;

	@Override
	public ServletMatch getServletMatch(String contextPath, String path) throws ServletException {

		if (path.startsWith(SERVLET_PATH)) { // $NON-NLS-1$
			int len = SERVLET_PATH.length(); // $NON-NLS-1$
			String servletPath = path.substring(0, len);
			String pathInfo = path.substring(len);
			return new ServletMatch(getExecutorServlet(), servletPath, pathInfo);
		}
		return null;
	}

	@Override
	public void init(ComponentModule module) {
		this.module = module;
		this.lastUpdate = module.getLastRefresh();
	}

	public synchronized Servlet getExecutorServlet() throws ServletException {
		if (servlet == null) {
			RouterFactory rf = new RouterFactory(module);
			servlet = (XRestAPIServlet) module.createServlet(new XRestAPIServlet(rf), "XRestAPI Servlet", null);
		} else {
			if (lastUpdate < this.module.getLastRefresh()) {
				lastUpdate = this.module.getLastRefresh();
				servlet.refresh();
			}
		}
		return servlet;
	}
}
