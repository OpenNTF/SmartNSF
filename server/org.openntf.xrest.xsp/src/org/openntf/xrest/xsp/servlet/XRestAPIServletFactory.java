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
import java.io.InputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.model.Router;

import com.ibm.commons.util.io.StreamUtil;
import com.ibm.designer.runtime.domino.adapter.ComponentModule;
import com.ibm.designer.runtime.domino.adapter.IServletFactory;
import com.ibm.designer.runtime.domino.adapter.ServletMatch;

public class XRestAPIServletFactory implements IServletFactory {

	private ComponentModule module;
	public static final String SERVLET_PATH = "/xsp/.xrest/";
	private XRestAPIServlet servlet;
	private String dsl;

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
		try {
			InputStream is = module.getResourceAsStream("/WEB-INF/routes.groovy");
			if (is != null) {
				this.dsl = StreamUtil.readString(is);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized Servlet getExecutorServlet() throws ServletException {
		if (servlet == null) {
			Router router = DSLBuilder.buildRouterFromDSL(this.dsl, getClass().getClassLoader());
			servlet = (XRestAPIServlet) module.createServlet(new XRestAPIServlet(router), "XRestAPI Servlet", null);
		}
		return servlet;
	}
}
