package gov.va.mumps.debug.core;

import gov.va.mumps.debug.core.model.MStackInfo;

public interface IMInterpreterConsumer {
	void handleConnected(IMInterpreter interpreter);
	
	void handleCommandExecuted(String info);	
	void handleBreak(MStackInfo[] stackInfos);

	void handleEnd();
	
	void handleError(Throwable throwable);
	
	String getLaunchId();
	
	String getPrompt();
}
