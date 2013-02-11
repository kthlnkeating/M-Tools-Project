package gov.va.med.foundations.security.vistalink;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;

import javax.security.auth.callback.Callback;

/**
 * VistaLoginModule callback for external interactions in the Commit phase of a login.
 * @see VistaLoginModule
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class CallbackCommit implements Callback {

	private String nameNewPerson01;
	private VistaLinkConnection authenticatedConnection;

	/**
	 * Instantiates this callback
	 * @param nameNewPerson01 the name of the person logging in, from the New Person .01 field
	 * @param authenticatedConnection set an authenticated connection, for later retrieval
	 */
	CallbackCommit(String nameNewPerson01, VistaLinkConnection authenticatedConnection) {
		this.nameNewPerson01 = nameNewPerson01;
		this.authenticatedConnection = authenticatedConnection;
	}

	/**
	 * set a person name for later retrieval
	 * @return the person name from the New Person .01 field
	 */
	String getNameNewPerson01() {
		return this.nameNewPerson01;
	}

	/**
	 * retrieve an authenticated connection
	 * @return the authenticated connection
	 */	
	VistaLinkConnection getAuthenticatedConnection() {
		return this.authenticatedConnection;
	}
}
