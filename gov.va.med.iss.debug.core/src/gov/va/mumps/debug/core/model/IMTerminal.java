package gov.va.mumps.debug.core.model;

import gov.va.mumps.debug.core.IMInterpreterConsumer;

public interface IMTerminal {
	String getId();
	
	void connect(IMTerminalManager terminalManager, IMInterpreterConsumer consumer);
	void disconnect();
	void terminate();
}
