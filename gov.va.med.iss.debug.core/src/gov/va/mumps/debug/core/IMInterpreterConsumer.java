package gov.va.mumps.debug.core;

public interface IMInterpreterConsumer {
	void handleConnected(IMInterpreter interpreter);
	
	void handleCommandExecuted();	
	void handleBreak(String info);

	void handleEnd();
}
