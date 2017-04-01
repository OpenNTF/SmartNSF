package org.openntf.xrest.xsp.yaml;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.openntf.xrest.xsp.model.MappingField;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.model.Router;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.xsp.module.nsf.NotesContext;

import lotus.domino.Database;

public class YamlProducer {
	private final Router router;
	private final PrintWriter printWriter;
	private final HttpServletRequest request;
	private final Map<String, RouteProcessor> responseReference = new TreeMap<String, RouteProcessor>();
	private boolean hasPaged;

	public YamlProducer(Router router, HttpServletRequest request, PrintWriter pw) {
		this.router = router;
		this.request = request;
		this.printWriter = pw;
	}

	public void processYamlToPrintWriter() {
		writeProperty(0, "swagger", "\"2.0\"");
		buildInfo(request);
		buildSecurity();
		buildOperations(router);
		buildDefinitions();
		if (hasPaged) {
			buildParameters();
		}
	}

	private void buildSecurity() {
		writeKey(0, "securityDefinitions");
		writeKey(1, "basic");
		writeProperty(2, "type", "basic");

		writeKey(0, "security");
		writeProperty(2, "basic", "[]");
	}

	private void buildInfo(HttpServletRequest request) {

		try {
			writeProperty(0, "basePath", request.getContextPath() + "/xsp/.xrest");
			writeProperty(0, "host", request.getLocalName() + ":" + request.getLocalPort());
			writeProperty(0, "schemes", "[" + extractProtocol(request) + "]");
			writeKey(0, "info");
			NotesContext c = NotesContext.getCurrentUnchecked();
			Database db = c.getCurrentDatabase();
			writeProperty(1, "title", db.getTitle());
			writeProperty(1, "version", "\"1.0.0\"");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String extractProtocol(HttpServletRequest request) {
		String protocol = request.getProtocol();
		int nSlash = protocol.indexOf("/");
		return protocol.substring(0, nSlash).toLowerCase();
	}

	private void buildOperations(Router router) {
		Map<String, List<RouteProcessor>> mapping = router.routesMapping();
		writeKey(0, "paths");
		for (String route : mapping.keySet()) {
			writeKey(1, "/" + route);
			List<RouteProcessor> routes = mapping.get(route);
			for (RouteProcessor rp : routes) {
				writeProperty(2, rp.getMethod().toLowerCase(), "");
				buildOperation(rp);
				processParameters(rp);
			}

		}
	}

	private void buildOperation(RouteProcessor rp) {
		writeProperty(3, "consumes", "[application/json]");
		writeKey(3, "responses");
		writeKey(4, "200");
		okResponse(rp);
	}

	private void okResponse(RouteProcessor rp) {
		writeProperty(5, "description", "Successful response");
		writeKey(5, "schema");
		String schemaKey = buildSchemaKey(rp);

		switch (rp.getStrategyValue()) {
		case ATTACHMENT:
			writeProperty(6, "type", "file");
			break;
		case DOCUMENTS_BY_FORMULA_PAGED:
		case DOCUMENTS_BY_SEARCH_FT_PAGED:
		case DOCUMENTS_BY_VIEW_PAGED:
		case DOCUMENTS_FROM_VIEW_BY_KEY_PAGED:
		case VIEWENTRIES_BY_CATEGORY_PAGED:
		case VIEWENTRIES_PAGED:
			buildPagedSchema(schemaKey, rp);
			break;
		case DOCUMENTS_FROM_VIEW_BY_KEY:
		case DOCUMENTS_BY_FORMULA:
		case DOCUMENTS_BY_SEARCH_FT:
		case DOCUMENTS_BY_VIEW:
			buildArraySchema(schemaKey, rp);
			break;
		default:
			writeProperty(6, "$ref", "'#/definitions/" + schemaKey + "'");
			responseReference.put(schemaKey, rp);
		}
	}

	private void buildArraySchema(String schemaKey, RouteProcessor rp) {
		writeProperty(6, "type", "array");
		writeKey(6, "items");
		writeProperty(7, "$ref", "'#/definitions/" + schemaKey + "'");
		responseReference.put(schemaKey, rp);

	}

	private void buildPagedSchema(String schemaKey, RouteProcessor rp) {
		writeProperty(6, "type", "object");
		writeKey(6, "properties");
		writeKey(7, "start");
		writeProperty(8, "type", "integer");
		writeProperty(8, "format", "int32");
		writeProperty(8, "description", "Cursor start");
		writeKey(7, "count");
		writeProperty(8, "type", "integer");
		writeProperty(8, "format", "int32");
		writeProperty(8, "description", "Count of result set");
		writeKey(7, "total");
		writeProperty(8, "type", "integer");
		writeProperty(8, "format", "int32");
		writeProperty(8, "description", "Total result set");
		writeKey(7, "entries");
		writeProperty(8, "type", "array");
		writeKey(8, "items");
		writeProperty(9, "$ref", "'#/definitions/" + schemaKey + "'");
		responseReference.put(schemaKey, rp);

	}

	private String buildSchemaKey(RouteProcessor rp) {
		String[] keys = rp.getRoute().split("/");
		StringBuilder sb = new StringBuilder();
		for (String key : keys) {
			String value = key.replace("{", "").replace("}", "");
			sb.append(Character.toUpperCase(value.charAt(0)));
			sb.append(value.substring(1));
		}
		sb.append(rp.getMethod());
		return sb.toString();
	}

	private void processParameters(RouteProcessor rp) {
		if (!rp.getVariables().isEmpty() || rp.getStrategyValue().name().endsWith("PAGED")) {
			writeKey(3, "parameters");
			this.hasPaged = true;
			for (String var : rp.getVariables()) {
				writeNoKeyChar(4, "-");
				writeProperty(5, "name", var);
				writeProperty(5, "in", "path");
				writeProperty(5, "type", "string");
				writeProperty(5, "required", "true");
			}
			if (rp.getStrategyValue().name().endsWith("PAGED")) {
				writeProperty(4, "- $ref", "'#/parameters/startParam'");
				writeProperty(4, "- $ref", "'#/parameters/countParam'");
			}
		}
	}

	private void buildDefinitions() {
		if (responseReference.isEmpty()) {
			return;
		}
		writeKey(0, "definitions");
		for (String key : responseReference.keySet()) {
			RouteProcessor rp = responseReference.get(key);
			writeKey(1, key);
			buildResponseProperties(rp);
		}
	}

	private void buildResponseProperties(RouteProcessor rp) {
		writeProperty(2, "type", "object");
		writeKey(2, "properties");
		if ("DELETE".equals(rp.getMethod())) {
			writeDeletePropertiesResponse();
		} else {
			writeRegularResponseObject(rp);
		}
	}

	private void writeRegularResponseObject(RouteProcessor rp) {
		for (MappingField mf : rp.getMappingFields().values()) {
			if (!mf.isWriteOnly()) {
				writeProperty(3, mf.getJsonName(), "");
				if (mf.getType().name().startsWith("ARRAY")) {
					writeProperty(4, "type", "array");
					writeKey(4, "items");
					writeProperty(5, "type", mf.getType().yamlType());
					if (!StringUtil.isEmpty(mf.getType().yamlFormat())) {
						writeProperty(5, "format", mf.getType().yamlFormat());
					}

				} else {
					writeProperty(4, "type", mf.getType().yamlType());
					if (!StringUtil.isEmpty(mf.getType().yamlFormat())) {
						writeProperty(4, "format", mf.getType().yamlFormat());
					}
				}
			}
		}
	}

	private void buildParameters() {
		writeKey(0, "parameters");
		writeParametersProps("startParam", "start");
		writeParametersProps("countParam", "count");

	}

	private void writeParametersProps(String name, String var) {
		writeKey(1, name);
		writeProperty(2, "name", var);
		writeProperty(2, "in", "query");
		writeProperty(2, "type", "integer");
		writeProperty(2, "required", "false");
	}

	private void writeDeletePropertiesResponse() {
		writeKey(3, "deleted");
		writeProperty(4, "type", "array");
		writeKey(4, "items");
		writeProperty(5, "type", "string");
		writeProperty(5, "description", "UNID's of all deleted documents.");
	}

	private void writeProperty(int indent, String key, String value) {
		StringBuilder sb = getSBwithCorrectIntend(indent);
		sb.append(key);
		sb.append(": ");
		sb.append(value);
		printWriter.println(sb.toString());
	}

	private StringBuilder getSBwithCorrectIntend(int indent) {
		StringBuilder sb = new StringBuilder();
		int spaces = indent * 2;
		for (int counter = 0; counter < spaces; counter++) {
			sb.append(" ");
		}
		return sb;
	}

	private void writeKey(int indent, String key) {
		StringBuilder sb = getSBwithCorrectIntend(indent);
		sb.append(key);
		sb.append(":");
		printWriter.println(sb.toString());
	}
	
	private void writeNoKeyChar(int indent, String key) {
		StringBuilder sb = getSBwithCorrectIntend(indent);
		sb.append(key);
		printWriter.println(sb.toString());
	}
}
