package org.openntf.xrest.xsp.exec.output;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.domino.services.HttpServiceConstants;

public enum JsonPayloadProcessor {
	INSTANCE;

	public void processJsonPayload(Object payload, HttpServletResponse resp) throws IOException, JsonException {
		resp.setStatus(200);
		resp.addHeader("content-type", HttpServiceConstants.CONTENTTYPE_APPLICATION_JSON_UTF8);
		resp.setCharacterEncoding(HttpServiceConstants.ENCODING_UTF8);
		Writer os = new OutputStreamWriter(resp.getOutputStream(), HttpServiceConstants.ENCODING_UTF8);
		JsonGenerator.toJson(JsonJavaFactory.instanceEx, os, payload, false);
		os.close();
	}
}
