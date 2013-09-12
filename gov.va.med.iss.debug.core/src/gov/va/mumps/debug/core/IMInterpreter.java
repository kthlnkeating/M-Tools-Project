package gov.va.mumps.debug.core;

import java.io.IOException;

public interface IMInterpreter {
	void connect(IMInterpreterConsumer consumer);
	void disconnect();
	void terminate();
	
	void sendCommand(String command) throws IOException;
	void debugCommand(String command) throws IOException;
}
