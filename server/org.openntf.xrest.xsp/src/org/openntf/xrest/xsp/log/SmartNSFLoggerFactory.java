package org.openntf.xrest.xsp.log;

import com.ibm.commons.log.Log;
import com.ibm.commons.log.LogMgr;

public class SmartNSFLoggerFactory extends Log{

	public final static LogMgr XSP = load("org.openntf.xrest.xsp", "Logger used for Logging all events around the SmartNSF Server side");
	public final static LogMgr DDE = load("org.openntf.xrest.designer", "Logger used for Logging all events about SmartNSF in the DDE");
	
}
