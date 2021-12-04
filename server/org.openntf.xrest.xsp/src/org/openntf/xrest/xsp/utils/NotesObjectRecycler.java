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
package org.openntf.xrest.xsp.utils;

import java.util.ArrayList;
import java.util.List;

import lotus.domino.Base;

public class NotesObjectRecycler {


	public static void recycleObjects(List<Object> recyclingObjects) {
		List<Base> baseObject = new ArrayList<Base>();
		for (Object obj : recyclingObjects) {
			if (obj instanceof Base) {
				baseObject.add((Base) obj);
			}
		}
		recycle(baseObject.toArray(new Base[baseObject.size()]));
	}

	public static void recycleObjects(Object... recyclingObjects) {
		List<Base> baseObject = new ArrayList<Base>();
		for (Object obj : recyclingObjects) {
			if (obj instanceof Base) {
				baseObject.add((Base) obj);
			}
		}
		recycle(baseObject.toArray(new Base[baseObject.size()]));
	}

	public static void recycle(Base... recyclingObjects) {
		for (Base torecycle : recyclingObjects) {
			if (torecycle != null) {
				try {
					torecycle.recycle();
				} catch (Exception ex) {
				}
			}
		}
	}
}
