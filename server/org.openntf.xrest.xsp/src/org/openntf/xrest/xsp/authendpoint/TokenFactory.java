/**
 * Copyright 2014, WebGate Consulting AG
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
package org.openntf.xrest.xsp.authendpoint;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.utils.NotesObjectRecycler;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Session;
import lotus.domino.View;

public class TokenFactory {

	private Map<String, TokenConfiguration> tokenConfiguration = new HashMap<String,TokenConfiguration>();
	private boolean loaded = false;

	
	public Token buildLTPAToken(String user, String domainName) throws ExecutorException {
		TokenConfiguration tokenConfiguration = getConfiguration(domainName);
		if (tokenConfiguration == null) {
			throw new ExecutorException(500, "No TokenConfiguration found for Server: " + domainName,"","");
		}
		try {
			Token token = Token.generate(user, new Date(), tokenConfiguration);
			return token;
		} catch (Exception e) {
			throw new ExecutorException(500, e, "No TokenConfiguration found for Server: " + domainName,"");
		}
	}

	public TokenConfiguration getConfiguration(String serverFQDN) {
		if (tokenConfiguration.containsKey("." + serverFQDN.toLowerCase())) {
			return tokenConfiguration.get("." + serverFQDN.toLowerCase());
		}
		int nStart = serverFQDN.indexOf(".");
		while (nStart > 0 && serverFQDN.length() > 0) {
			serverFQDN = serverFQDN.substring(nStart);
			if (tokenConfiguration.containsKey(serverFQDN.toLowerCase())) {
				return tokenConfiguration.get(serverFQDN.toLowerCase());
			}
			nStart = serverFQDN.indexOf(".");
		}
		return null;
	}

	public synchronized void loadConfig(Session sessionAsSigner, String serverName) {
		Database ndbNames = null;
		View viwSSO = null;
		this.tokenConfiguration.clear();
		try {
			ndbNames = sessionAsSigner.getDatabase(serverName, "names.nsf");
			if (ndbNames != null) {
				viwSSO = ndbNames.getView("($WebSSOConfigs)");
				Document docNext = viwSSO.getFirstDocument();
				while (docNext != null) {
					Document docConfig = docNext;
					docNext = viwSSO.getNextDocument(docNext);
					TokenConfiguration tokenConfig = buildConfig(docConfig);
					if (tokenConfig != null) {
						this.tokenConfiguration.put(tokenConfig.getDomain().toLowerCase(), tokenConfig);
					}
					docConfig.recycle();
				}
			}
			NotesObjectRecycler.recycle(viwSSO, ndbNames);
		} catch (Exception e) {
			e.printStackTrace();
		}
		loaded = true;
	}
	
	public void refresh() {
		loaded = false;
	}
	
	public boolean isLoaded() {
		return this.loaded;
	}

	private TokenConfiguration buildConfig(Document docCurrent) {
		try {
			String strSecret = docCurrent.getItemValueString("LTPA_DominoSecret");
			String strDomain = docCurrent.getItemValueString("LTPA_TokenDomain");
			int expiration = docCurrent.getItemValueInteger("LTPA_TokenExpiration");
			String strTokenName = docCurrent.getItemValueString("LTPA_TokenName");
			if (strSecret != null && strDomain != null && strTokenName != null) {
				return TokenConfiguration.buildTokenConfiguration(strDomain, strSecret, expiration, strTokenName);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
