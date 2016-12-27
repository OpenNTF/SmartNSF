package org.openntf.xrest.xsp.exec;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.model.EventException;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.domino.services.HttpServiceConstants;
import com.ibm.xsp.util.HtmlUtil;

import groovy.lang.Closure;

public abstract class AbstractRouteProcessorExecutor implements RouteProcessorExecutor {

	private final Context context;
	private final RouteProcessor routerProcessor;
	private final String path;
	private DataModel<?> model;
	private Object resultPayload;

	public AbstractRouteProcessorExecutor(Context context, RouteProcessor routerProcessor, String path) {
		super();
		this.context = context;
		this.routerProcessor = routerProcessor;
		this.path = path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.xrest.xsp.exec.RouteProcessorExecutor#execute(java.lang.
	 * String)
	 */
	@Override
	public void execute() {
		try {
			checkAccess();
			validateRequest();
			preLoadDocument();
			loadModel();
			postLoadDocument();
			executeMethodeSpecific();
			preSubmitValues();
			submitValues();
		} catch (ExecutorException ex) {
			try {
				processError(ex);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (JsonException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkAccess() throws ExecutorException {
		List<String> allowedUsersAndGroups = routerProcessor.getAccessGroups();
		if (allowedUsersAndGroups == null || allowedUsersAndGroups.isEmpty()) {
			return;
		}
		List<String> myGroups = new ArrayList<String>();
		myGroups.add(context.getUserName());
		myGroups.addAll(context.getGroups());
		myGroups.addAll(context.getRoles());
		for (String me : myGroups) {
			if (allowedUsersAndGroups.contains(me)) {
				return;
			}
		}
		throw new ExecutorException(403, "Access denied for user " + context.getUserName(), path, "checkAccess");
	}

	private void validateRequest() throws ExecutorException {
		try {
			Closure<?> cl = routerProcessor.getEventClosure(EventType.VALIDATE);
			if (cl != null) {
				DSLBuilder.callClosure(cl,context);
			}
		} catch(EventException e) {
			throw new ExecutorException(400, "Validation Error: " +e.getMessage(), path, "validation");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " +e.getMessage(), path, "validation");
		}
	}

	private void preLoadDocument()  throws ExecutorException {
		try {
			Closure<?> cl = routerProcessor.getEventClosure(EventType.PRE_LOAD_MODEl);
			if (cl != null) {
				DSLBuilder.callClosure(cl,context);
			}
		} catch(EventException e) {
			throw new ExecutorException(400, "Pre Load Error: " +e.getMessage(), path, "preloadmodel");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " +e.getMessage(), path, "preloadmodel");
		}
	}
	
	private void loadModel() throws ExecutorException {
		model = routerProcessor.getDataModel(context);
	}

	private void postLoadDocument() throws ExecutorException {
		try {
			Closure<?> cl = routerProcessor.getEventClosure(EventType.POST_LOAD_MODEL);
			if (cl != null) {
				DSLBuilder.callClosure(cl,context, model);
			}
		} catch(EventException e) {
			throw new ExecutorException(400, "Post Load Error: " +e.getMessage(), path, "postloadmodel");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " +e.getMessage(), path, "postloadmodel");
		}
	}

	public void applyMapping() {
		
	}
	private void preSubmitValues() throws ExecutorException {
		try {
			Closure<?> cl = routerProcessor.getEventClosure(EventType.PRE_SUBMIT);
			if (cl != null) {
				DSLBuilder.callClosure(cl,context, model);
			}
		} catch(EventException e) {
			throw new ExecutorException(400, "Post Load Error: " +e.getMessage(), path, "presubmit");
		} catch (Exception e) {
			throw new ExecutorException(500, "Runntime Error: " +e.getMessage(), path, "presubmit");
		}
	}
	private void submitValues() throws ExecutorException, IOException, JsonException {
		context.getResponse().addHeader("content-type", HttpServiceConstants.CONTENTTYPE_APPLICATION_JSON_UTF8);
        context.getResponse().setCharacterEncoding(HttpServiceConstants.ENCODING_UTF8);
        Writer os = new OutputStreamWriter(context.getResponse().getOutputStream(),HttpServiceConstants.ENCODING_UTF8);
        JsonGenerator.toJson(JsonJavaFactory.instanceEx, os, resultPayload, false);
        os.close();
        return;
	}


	abstract protected void executeMethodeSpecific();





	private void processError(ExecutorException ex) throws UnsupportedEncodingException, IOException {
		HttpServletResponse resp = context.getResponse();
		resp.setStatus(ex.getHttpErrorNr());
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
		PrintWriter w = new PrintWriter(new OutputStreamWriter(resp.getOutputStream(), "utf-8")); //$NON-NLS-1$
		try {
			w.println("<html>"); //$NON-NLS-1$
			w.println("<body>"); //$NON-NLS-1$
			w.println("<h1>" + ex.getHttpErrorNr() + "</h1>"); // $NLX-ProxyServlet.Errorwhileprocessingtherequest-1$
			w.println("<br>"); //$NON-NLS-1$
			w.println(HtmlUtil.toHTMLContentString(ex.getMessage(), true));
			w.println("<pre>");
			ex.printStackTrace(w);
			w.print("</pre>");
			w.println("</body>"); //$NON-NLS-1$
			w.println("</html>"); //$NON-NLS-1$
		} finally {
			w.flush();
		}
	}

	public void setResultPayload(Object rp) {
		resultPayload = rp;
	}
	public void setModel(DataModel<?> model) {
		this.model = model;
	}
}
