package gov.va.med.foundations.security.vistalink;

import java.security.Principal;

/**
 * Provides an interface to marks a principal that represents a logged on Kernel user on an M system. Upon a successful
 * JAAS login, one or more principals may be contained in the JAAS subject that is returned from a successful JAAS login
 * (only one *Kernel* principal should be returned, however. The situation in which multiple principals could be
 * returned is if some kind of compound logon has been set up that requires several logons to complete, for example one
 * to Kernel, and one to a separate health data repository). The VistaKernelPrincipal interface is a marker you can use
 * to identify a "VistaKernelPrincipal" as one of those principals. However, an easier approach is to use the helper
 * method <code>getKernelPrincipal</code> in <code>VistaKernelPrincipalImpl</code> to directly retrieve the single
 * VistaKernelPrincipal.<p> You can use the KEY* field strings to retrieve user demographics values via the
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
 * @see VistaKernelPrincipalImpl
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public interface VistaKernelPrincipal extends Principal {
	/**
	 * map key to store/retrieve New Person .01 Field name from userDemographicsHashTable
	 */
	public static final String KEY_NAME_NEWPERSON01 = "KEY_NAME_NEWPERSON01";

	/**
	 * map key to store/retrieve the display name from userDemographicsHashTable
	 */
	public static final String KEY_NAME_DISPLAY = "KEY_NAME_DISPLAY";
	/**
	 * map key to store/retrieve name component prefix from userDemographicsHashTable
	 */
	public static final String KEY_NAME_PREFIX = "KEY_NAME_PREFIX";

	/**
	 * map key to store/retrieve name component suffix from userDemographicsHashTable
	 */
	public static final String KEY_NAME_SUFFIX = "KEY_NAME_SUFFIX";

	/**
	 * map key to store/retrieve name component given-first from userDemographicsHashTable
	 */
	public static final String KEY_NAME_GIVENFIRST = "KEY_NAME_GIVENFIRST";

	/**
	 * map key to store/retrieve name component middle from userDemographicsHashTable
	 */
	public static final String KEY_NAME_MIDDLE = "KEY_NAME_MIDDLE";

	/**
	 * map key to store/retrieve name component family-last from userDemographicsHashTable
	 */
	public static final String KEY_NAME_FAMILYLAST = "KEY_NAME_FAMILYLAST";

	/**
	 * map key to store/retrieve degree from userDemographicsHashTable
	 */
		public static final String KEY_NAME_DEGREE = "KEY_NAME_DEGREE";

	/**
	 * map key to store/retrieve DUZ from userDemographicsHashTable
	 */
	public static final String KEY_DUZ = "KEY_DUZ";

	/**
	 * map key to store/retrieve user title from userDemographicsHashTable
	 */
	public static final String KEY_TITLE = "KEY_TITLE";

	/**
	 * map key to store/retrieve user service/section from userDemographicsHashTable
	 */
	public static final String KEY_SERVICE_SECTION = "KEY_SERVICE_SECTION";

	/**
	 * map key to store/retrieve user language from userDemographicsHashTable
	 */
	public static final String KEY_LANGUAGE = "KEY_LANGUAGE";

	/**
	 * map key to store/retrieve division station IEN from userDemographicsHashTable
	 */
	public static final String KEY_DIVISION_IEN = "KEY_DIVISION_IEN";

	/**
	 * map key to store/retrieve division station name from userDemographicsHashTable
	 */
	public static final String KEY_DIVISION_STATION_NAME = "KEY_IVISION_STATION_NAME";

	/**
	 * map key to store/retrieve division station number from userDemographicsHashTable
	 */
	public static final String KEY_DIVISION_STATION_NUMBER = "KEY_DIVISION_STATION_NUMBER";

	/**
	 * map key to store/retrieve user timeout value from userDemographicsHashTable
	 */
	public static final String KEY_DTIME = "KEY_DTIME";

	/**
	 * returns the name given to the principal when it was created.
	 * @see java.security.Principal#getName()
	 */
	String getName();
	/**
	 * returns a given user demographic value. Use the various KEY* field strings to retrieve
	 * various values.
	 * @param key The key under which the demographic value is stored
	 * @return String the value of the demographic value requests
	 */
	String getUserDemographicValue(String key);
	/**
	 * A string representation of the principal
	 * @see java.security.Principal#toString()
	 */
	String toString();
	/**
	 * Returns whether some object equals this one. Because of the nature of a principal and an authenticated
	 * connection, we never can call two principals "equal" unless the object reference is to the same object. Otherwise
	 * its unfathomable whether the two connections held by the principals are "equal". So false is returned in all
	 * instances except where the object references are to the same object.
	 * @see java.security.Principal#equals(Object)
	 */
	boolean equals(Object o);
}