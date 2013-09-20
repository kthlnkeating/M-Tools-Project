package gov.va.mumps.debug.core;

public interface IMInterpreterConsumer {
	void handleConnected(IMInterpreter interpreter);
	
	void handleCommandExecuted(String info);	
	void handleBreak(String info);

	void handleEnd();
	
	void handleError(Throwable throwable);
	
	String getLaunchId();
	
	String getPrompt();
}
