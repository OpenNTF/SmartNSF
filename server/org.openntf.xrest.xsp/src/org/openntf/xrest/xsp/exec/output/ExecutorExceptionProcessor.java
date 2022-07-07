package org.openntf.xrest.xsp.exec.output;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.exec.ExecutorException;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.domino.services.HttpServiceConstants;

public enum ExecutorExceptionProcessor {
	INSTANCE;

	public void processExecutorException(ExecutorException ex, HttpServletResponse resp, boolean trace) throws IOException, JsonException {
		resp.setStatus(ex.getHttpStatusCode());
		resp.setContentType(HttpServiceConstants.CONTENTTYPE_APPLICATION_JSON_UTF8);
		resp.setCharacterEncoding(HttpServiceConstants.ENCODING_UTF8);
		JsonObject jso = buildJsonError(ex, trace);
		Writer os = new OutputStreamWriter(resp.getOutputStream(), HttpServiceConstants.ENCODING_UTF8);
		JsonGenerator.toJson(JsonJavaFactory.instanceEx, os, jso, false);
		os.close();
	}

	public void processGeneralException(int httpStatus, Exception ex, HttpServletResponse resp) throws JsonException, IOException {
		resp.setStatus(httpStatus);
		resp.setContentType(HttpServiceConstants.CONTENTTYPE_APPLICATION_JSON_UTF8);
		resp.setCharacterEncoding(HttpServiceConstants.ENCODING_UTF8);
		JsonObject jso = buildJsonErrorFromException(ex);
		Writer os = new OutputStreamWriter(resp.getOutputStream(), HttpServiceConstants.ENCODING_UTF8);
		JsonGenerator.toJson(JsonJavaFactory.instanceEx, os, jso, false);
		os.close();
	}

	private JsonObject buildJsonErrorFromException(Exception ex) {
		JsonObject jso = new JsonJavaObject();
		jso.putJsonProperty("error", ex.getMessage());
		String trace = extractTrace(ex);
		jso.putJsonProperty("trace", trace);
		return jso;
	}

	private JsonObject buildJsonError(ExecutorException ex, boolean traceEnabled) {
		JsonObject jso = new JsonJavaObject();
		jso.putJsonProperty("error", ex.getMessage());
		jso.putJsonProperty("path", ex.getPath());
		jso.putJsonProperty("phase", ex.getPhase());
		if (traceEnabled) {
			String trace = extractTrace(ex);
			jso.putJsonProperty("trace", trace);
		}
		return jso;
	}

	private String extractTrace(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		return sw.toString();
	}
}
