package gov.va.mumps.debug.core;

public interface IMInterpreter {
	void connect(IMInterpreterConsumer consumer, String namespace);
	void disconnect();
	void terminate();
	
	void sendInfoCommand(String command);	
	void sendRunCommand(String command);	
	
	void resume();
	void stepOver();
	void stepInto();
	void stepReturn();
}
