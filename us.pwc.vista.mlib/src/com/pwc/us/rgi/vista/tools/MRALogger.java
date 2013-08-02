//---------------------------------------------------------------------------
// Copyright 2013 PwC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//---------------------------------------------------------------------------

package com.pwc.us.rgi.vista.tools;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MRALogger {
	public static <T> void logError(Class<T> cls, String msg, Throwable t) {
		Logger logger = Logger.getLogger(cls.getName());
		logger.log(Level.SEVERE, msg, t);
	}

	public static <T> void logError(Class<T> cls, String msg) {
		Logger logger = Logger.getLogger(cls.getName());
		logger.log(Level.SEVERE, msg);			
	}

	public static <T> void logInfo(Class<T> cls, String msg) {
		Logger logger = Logger.getLogger(cls.getName());
		logger.log(Level.INFO, msg);			
	}

	public static <T> void logError(Object obj, String msg, Throwable t) {
		MRALogger.logError(obj.getClass(), msg, t);	
	}
	
	public static <T> void logError(Object obj, String msg) {
		MRALogger.logError(obj.getClass(), msg);	
	}

	public static <T> void logError(String msg, Throwable t) {
		MRALogger.logError(MRoutineAnalyzer.class, msg, t);	
	}
	
	public static <T> void logError(String msg) {
		MRALogger.logError(MRoutineAnalyzer.class, msg);	
	}

	public static <T> void logInfo(String msg) {
		MRALogger.logInfo(MRoutineAnalyzer.class, msg);	
	}

}
