package us.pwc.vista.eclipse.terminal;

public interface IVistAStreamListener {
	void handleConnected();
	void handleCommandExecuteEnded();
	void handleBreak(String info);
	
	void handleConnectorCreated(VistATelnetConnector connector);
}
