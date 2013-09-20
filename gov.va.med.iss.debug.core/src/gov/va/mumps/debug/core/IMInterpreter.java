package gov.va.mumps.debug.core;

public interface IMInterpreter {
	void sendInfoCommand(String command);	
	void sendRunCommand(String command);	
	
	void resume();
	void stepOver();
	void stepInto();
	void stepReturn();
}
