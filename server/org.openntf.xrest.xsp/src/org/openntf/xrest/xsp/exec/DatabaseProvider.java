/*
 * � Copyright WebGate Consulting AG, 2013
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
package org.openntf.xrest.xsp.exec;

import com.ibm.commons.util.StringUtil;

import lotus.domino.Database;
import lotus.domino.Session;

public enum DatabaseProvider {
	INSTANCE;

	public Database getDatabase(String strDB, Database dbCurrent, Session session) {
		Database ndbAccess = null;
		try {
			if (StringUtil.isEmpty(strDB)) {
				return dbCurrent;
			} else {
				if (strDB.contains("!!")) {
					String[] arrDB = strDB.split("!!");
					ndbAccess = session.getDatabase(arrDB[0], arrDB[1]);
				} else {
					ndbAccess = session.getDatabase(dbCurrent.getServer(), strDB);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ndbAccess;

	}

	public void handleRecylce(Database ndbRecylce, Database dbCurrent) {
		try {
			if (ndbRecylce.equals(dbCurrent)) {
				return;
			}
			ndbRecylce.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
