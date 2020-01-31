package org.openntf.xrest.xsp.names;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.output.JsonPayloadProcessor;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

import lotus.domino.NotesException;

public class UserAndGroupHandler {

	private final HttpServletResponse resp;
	private final TypeAHeadResolver taResolver;
	private final UserInformationResolver uiResolver;
	private final Context context;

	public UserAndGroupHandler(HttpServletResponse resp, TypeAHeadResolver typeAHeadResolverValue, UserInformationResolver userInformationResolverValue, Context context) {
		this.resp = resp;
		this.taResolver = typeAHeadResolverValue;
		this.uiResolver = userInformationResolverValue;
		this.context = context;
	}

	public void execute(String queryString) throws ExecutorException {
		try {
			Map<String, String> parameters = extractParams(queryString);
			if (!parameters.containsKey("action")) {
				throw new ExecutorException(500, "Please provide a action for user built-in command.", "", "Built-in command: users");
			}
			String action = parameters.get("action");
			if ("ta".equals(action)) {
				handleTypeAHead(parameters);
				return;
			}
			if ("search".equals(action)) {
				handleSearch(parameters);
				return;
			}
			if ("byEmail".equals(action)) {
				handleByEMail(parameters);
				return;
			}
			if ("byUserName".equals(action)) {
				handleByUserName(parameters);
				return;
			}
			if ("all".equals(action)) {
				handleAll(parameters);
			}
			throw new ExecutorException(500, action + "is not supported.", "", "Built-in command: users");

		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "Built-in command: users");
		}
	}

	private void handleAll(Map<String, String> parameters) throws NotesException, JsonException, IOException {
		JsonJavaArray result = new JsonJavaArray();
		UserSearchScope scope = getScope(parameters);
		List<UserInformation> users = uiResolver.allUser(scope, context);
		for (UserInformation ui : users) {
			result.add(ui.toJSON());
		}
		JsonPayloadProcessor.INSTANCE.processJsonPayload(result, resp);

	}

	private void handleByUserName(Map<String, String> parameters) throws JsonException, IOException, NotesException {
		JsonObject result = new JsonJavaObject();
		String search = getSearch(parameters);
		UserSearchScope scope = getScope(parameters);
		UserInformation user = uiResolver.findUserByUserName(search, scope, context);
		if (user != null) {
			result = user.toJSON();
		}
		JsonPayloadProcessor.INSTANCE.processJsonPayload(result, resp);
	}

	private void handleByEMail(Map<String, String> parameters) throws JsonException, IOException, NotesException {
		JsonObject result = new JsonJavaObject();
		String search = getSearch(parameters);
		UserSearchScope scope = getScope(parameters);
		UserInformation user = uiResolver.findUserByEMail(search, scope, context);
		if (user != null) {
			result = user.toJSON();
		}
		JsonPayloadProcessor.INSTANCE.processJsonPayload(result, resp);

	}

	private void handleSearch(Map<String, String> parameters) throws JsonException, IOException, NotesException {
		JsonJavaArray result = new JsonJavaArray();
		String search = getSearch(parameters);
		UserSearchScope scope = getScope(parameters);
		List<UserInformation> users = taResolver.findUsers(search, scope, context);
		for (UserInformation ui : users) {
			result.add(ui.toJSON());
		}
		JsonPayloadProcessor.INSTANCE.processJsonPayload(result, resp);

	}

	private void handleTypeAHead(Map<String, String> parameters) throws JsonException, IOException, NotesException {
		JsonJavaArray result = new JsonJavaArray();
		String search = getSearch(parameters);
		UserSearchScope scope = getScope(parameters);
		List<UserInformation> users = taResolver.findUsers(search, scope, context);
		for (UserInformation ui : users) {
			result.add(ui.toJSON());
		}
		JsonPayloadProcessor.INSTANCE.processJsonPayload(result, resp);
	}

	private Map<String, String> extractParams(String query) throws UnsupportedEncodingException {
		Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			if (idx > -1) {
				query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
			}
		}
		return query_pairs;
	}
	private String getSearch(Map<String,String> params) {
		if (!params.containsKey("search")) {
			throw new IllegalArgumentException("Missing parameter 'search=' in URL");
		}
		return params.get("search");
	}
	private UserSearchScope getScope(Map<String, String> parameters) {
		if (!parameters.containsKey("scope")) {
			return UserSearchScope.APPLICATON;
		}
		if ("all".equalsIgnoreCase(parameters.get("scope"))) {
			return UserSearchScope.ALL;
		}
		return UserSearchScope.APPLICATON;
	}

}
