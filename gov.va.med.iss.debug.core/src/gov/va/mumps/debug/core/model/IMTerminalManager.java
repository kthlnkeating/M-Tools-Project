package gov.va.mumps.debug.core.model;

import gov.va.mumps.debug.core.IMInterpreterConsumer;

public interface IMTerminalManager {
	IMTerminal create(String id, IMInterpreterConsumer consumer);
	
	void disconnect(String id);
	void giveFocus(String id);
	void close(IMTerminal terminal);
}
