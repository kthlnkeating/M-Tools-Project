package gov.va.mumps.debug.core.model;

import gov.va.mumps.debug.core.IMInterpreterConsumer;

public interface IMTerminalManager {
	IMTerminal create(String id, IMInterpreterConsumer consumer);
	
	void giveFocus(String id);
	void close(IMTerminal terminal);
}
