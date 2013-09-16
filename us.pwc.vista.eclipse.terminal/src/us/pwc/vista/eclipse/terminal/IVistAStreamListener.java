package us.pwc.vista.eclipse.terminal;

public interface IVistAStreamListener {
	void handleConnected();
	void handleCommandExecuteEnded(String info);
	void handleBreak(String info);
	void handleEnd();
	
	void handleConnectorCreated(VistATelnetConnector connector);
}
