package gov.va.mumps.launching;

/**
 * Listens for when input is ready to be sent to the Launched MUMPs code.
 * 
 */
public interface InputReadyListener {
	
	void handleInput(String input);

}
