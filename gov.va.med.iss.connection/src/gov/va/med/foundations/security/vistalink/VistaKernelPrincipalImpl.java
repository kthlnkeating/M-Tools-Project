package gov.va.med.foundations.security.vistalink;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.utilities.FoundationsException;

import java.util.Hashtable;
import java.util.Set;

import javax.security.auth.Subject;

// import x.gov.va.med.iss.log4j.*;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;


/**
 * A JAAS  principal representing a logged on Kernel user on an M system. 
 * <p><b>Retrieving the principal after login</b><p>Upon a successful JAAS login, one or more principals may be
 * contained in the JAAS subject that is returned from a successful JAAS login (only one *Kernel* principal should be
 * returned, however. The situation in which multiple principals could be returned is if some kind of compound logon has
 * been set up that requires several logons to complete, for example one to Kernel, and one to a separate health data
 * repository). The VistaKernelPrincipal interface is a marker you can use to identify a "VistaKernelPrincipal" as one
 * of those principals. However, an easier approach is to use the helper method <code>getKernelPrincipal</code> in
 * <code>VistaKernelPrincipalImpl</code> to directly retrieve the single VistaKernelPrincipal. For example:
 * <pre>
 * String cfgName = "RpcSampleServer";
 * 
 * // create the callback handler
 * CallbackHandlerSwing cbhSwing = new CallbackHandlerSwing(myFrame);
 * 
 * // create the LoginContext
 * loginContext = new LoginContext(cfgName, cbhSwing);
 * 
 * // login to server
 * loginContext.login(); 
 * 
 * // get principal
 * userPrincipal = VistaKernelPrincipalImpl.getKernelPrincipal(loginContext.getSubject());
 * </pre>
 * 
 * <p><b>Retrieving the authenticated connection from the principal</b><p>
 * To execute RPCs, you'll need to retrieve the authenticated connection. Once a successful login has been completed,
 * you can retrieve the associated authenticated connection from the Kernel principal. For example:
 * <pre>
 * VistaLinkConnection myConnection = userPrincipal.getAuthenticatedConnection(); 
 * RpcRequest vReq = RpcRequestFactory.getRpcRequest(rpcContext, "XOB VL TEST PING"); 
 * RpcResponse vResp = myConnection.executeRPC(vReq);
 * </pre>
 * <p><b>Retrieving Demographics</b>
 * <p>You can use the KEY* field strings to retrieve user demographics values via the
 * <code>getUserDemographicValue</code> method. For example:
 * <pre>
 * private VistaKernelPrincipalImpl userPrincipal;
 * 
 * // left out: perform a login... then the subject in the logincontext is populated 
 *
 * // get the Kernel principal after successful login 
 * userPrincipal = VistaKernelPrincipalImpl.getKernelPrincipal(loginContext.getSubject()); 
 * 
 * // get user demographics
 * String duz = this.userPrincipal.getUserDemographicValue(VistaKernelPrincipalImpl.KEY_DUZ);
 * String name = userPrincipal.getUserDemographicValue(VistaKernelPrincipalImpl.KEY_NAME_DISPLAY);
 * </pre>
 * @see VistaKernelPrincipal
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public final class VistaKernelPrincipalImpl implements java.io.Serializable, VistaKernelPrincipal {

	// see http://java.sun.com/j2se/1.4/docs/guide/security/jaas/tutorials/SamplePrincipal.java

	private Hashtable userDemographicsHashTable;
	private transient VistaLinkConnection authenticatedConnection;

	private static final Logger LOGGER = Logger.getLogger(VistaKernelPrincipalImpl.class);

	/**
	 * Constructor used by the login module to also set the name of the logged-on user.
	 * @param nameNewPerson01 the value to set as the name of the logged-on user.
	 * @throws FoundationsException thrown if an error encountered.
	 */
	VistaKernelPrincipalImpl(String nameNewPerson01) throws FoundationsException {
		if (nameNewPerson01 == null) {
			throw new FoundationsException("VistaAVPrincipalImpl constructor: nameNewPerson01 cannot be null");
		}
		userDemographicsHashTable = new Hashtable();
		userDemographicsHashTable.put(KEY_NAME_NEWPERSON01, nameNewPerson01);
		authenticatedConnection = null;
	}
	
	/**
	 * Construct a principal from a hashtable (with appropriate values present in hashtable)
	 * @param userDemographicsHashtable hashtable containing appropriate values for a user principal
	 */
	VistaKernelPrincipalImpl(Hashtable userDemographicsHashtable) {
		userDemographicsHashTable = userDemographicsHashtable;
		authenticatedConnection = null;
	}

	/**
	 * get the Principal's full name from the New Person .01 field. 
	 * @return the user name (from the New Person .01 field) of the Principal.
	 */
	public String getName() {
		return (String) userDemographicsHashTable.get(KEY_NAME_NEWPERSON01);
	}

	/**
	 * Retrieve the authenticated connection from this principal after logon.
	 * @return VistaLinkConnection the authenticated Vista connection, post-successful logon.
	 */
	public VistaLinkConnection getAuthenticatedConnection() {
		return authenticatedConnection;
	}

	/**
	 * adds an authenticated connection to the principal
	 * @param vistaConnection authenticated connection
	 */
	void setAuthenticatedConnection(VistaLinkConnection vistaConnection) {
		authenticatedConnection = vistaConnection;
	}

	/**
	 * set a user demographic value into the principal
	 * @param key key under which to store the user demographic value
	 * @param value value to set
	 */
	void setUserDemographicValue(String key, String value) {
		userDemographicsHashTable.put(key, value);
	}

	/**
	 * @see gov.va.med.foundations.security.vistalink.VistaKernelPrincipal#getUserDemographicValue(String)
	 */
	public String getUserDemographicValue(String key) {
		return (String) userDemographicsHashTable.get(key);
	}

	/**
	 * Rudimentary toString method.
	 * @return the concatenated class name, user name, division name and number, and timeout.
	 */
	public String toString() {
		return (
			"VistaAVKernelPrincipalImpl Name:  "
				+ getName()
				+ " Division: "
				+ userDemographicsHashTable.get(KEY_DIVISION_STATION_NAME)
				+ " "
				+ userDemographicsHashTable.get(KEY_DIVISION_STATION_NUMBER)
				+ " Timeout: "
				+ userDemographicsHashTable.get(KEY_DTIME));
	}

	/**
	 * Returns whether some object equals this one. Because of the nature of a principal and an authenticated
	 * connection, we never can call two principals "equal" unless the object reference is to the same object. Otherwise
	 * its unfathomable whether the two connections held by the principals are "equal". So false is returned in all
	 * instances except where the object references are to the same object.
	 * @param o object to compare.
	 * @return true if the object references are the same (the only recognized 'equality' for thie object type)
	 * false if not.
	 */
	public boolean equals(Object o) {

		if (o == null) {
			return false;
		}

		if (this == o) {
			return true;
		}

		return false;
	}

	/**
	 * 
	 *Compute the hash code for this VistaKernelPrincipalImpl object.
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.userDemographicsHashTable.hashCode();
	}

	/**
	 * Returns the single Kernel principal contained in a JAAS subject returned by a JAAS LoginContext
	 * after a successful VistaLink logon to Vista.
	 * @param jaasSubject JAAS subject returned by a JAAS LoginContext after a successful VistaLink
	 * logon to Vista.
	 * @return VistaKernelPrincipalImpl Kernel principal contained in the JAAS subject
	 * @throws FoundationsException If no Kernel principal, or more than one, are found, an exception is thrown
	 */
	public static VistaKernelPrincipalImpl getKernelPrincipal(Subject jaasSubject) throws FoundationsException {
		VistaKernelPrincipalImpl myPrincipal = null;
		Set setPrincipals = jaasSubject.getPrincipals(VistaKernelPrincipal.class);
		if (setPrincipals.size() == 1) {
			myPrincipal = (VistaKernelPrincipalImpl) setPrincipals.iterator().next();
		} else if (setPrincipals.size() == 0) {
			String errMsg = "Error getting Kernel Principal: No Kernel Principals Found";
			FoundationsException e = new FoundationsException(errMsg);
			if (LOGGER.isEnabledFor(Priority.ERROR)) {
				LOGGER.error(errMsg, e);
			}
			throw e;
		} else {
			String errMsg = "Error getting Kernel Principal: Multiple Kernel Principals Found";
			FoundationsException e = new FoundationsException(errMsg);
			if (LOGGER.isEnabledFor(Priority.ERROR)) {
				LOGGER.error(errMsg, e);
			}
			throw e;
		}
		return myPrincipal;
	}

}
