package gov.va.mumps.debug.ui.terminal;


public interface IVistAStreamListener {
	void handleConnected();
	void handleCommandExecuteEnded(String info);
	void handleBreak(String info);
	void handleEnd();
	
	void handleConnectorCreated(VistATelnetConnector connector);
}
