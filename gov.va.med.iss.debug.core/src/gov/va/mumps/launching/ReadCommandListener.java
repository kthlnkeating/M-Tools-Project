package gov.va.mumps.launching;

/**
 * Listen to read events from the MUMPS launch model.
 * 
 * For example, the code that has been launched will return back as suspended
 * and wait for input when a read command ins encountered. This listener will
 * receive events for that.
 * 
 */
public interface ReadCommandListener {
	
	void handleReadCommand(int maxCharsToRead);
	
}
