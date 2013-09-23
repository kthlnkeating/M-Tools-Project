package gov.va.mumps.debug.core.model;

import java.io.OutputStream;

import gov.va.mumps.debug.core.IMInterpreterConsumer;

public interface IMTerminal {
	String getId();
	
	void connect(IMTerminalManager terminalManager, IMInterpreterConsumer consumer, OutputStream messageStream);
	void disconnect();
	void terminate();
}
