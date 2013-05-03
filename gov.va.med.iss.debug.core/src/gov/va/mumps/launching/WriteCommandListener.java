package gov.va.mumps.launching;

/**
 * Listen to write events from the MUMPS launch model.
 * 
 * For example, the code that has been launched may write output. Liseteners
 * will be notified of what output when this happens.
 */
public interface WriteCommandListener {
	
	void handleWriteCommand(String output);

}
