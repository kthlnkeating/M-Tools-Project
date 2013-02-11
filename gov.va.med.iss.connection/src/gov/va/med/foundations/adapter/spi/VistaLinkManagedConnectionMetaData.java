package gov.va.med.foundations.adapter.spi;

import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnectionMetaData;

/**
 * This class gives info about a VistaLinkManagedConnection
 *
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaLinkManagedConnectionMetaData
	implements ManagedConnectionMetaData {


	/**
	 * The managed connection that this class gives information about
	 */
	private VistaLinkManagedConnection managedConnection;

	/**
	 * This adapters product name
	 */
	private String eisProductName = "VistALink J2M Adapter";

	/**
	 * This adapters version number
	 */
	private String eisProductVersion = "1.0.0";

	/**
	 * Not used
	 */
	private String userName = "";

	/**
	 * Constructor
	 * @param mc
	 */
	public VistaLinkManagedConnectionMetaData
		(VistaLinkManagedConnection mc) {
		managedConnection = mc;
	}

	/**
	 * <br>Returns Product name of the underlying EIS instance 
	 * <br>connected through the ManagedConnection
	 * @see javax.resource.spi.ManagedConnectionMetaData#getEISProductName()
	 */
	public String getEISProductName() throws ResourceException {
		return eisProductName;
	}

	/**
	 * <br>Returns product version of the underlying EIS instance 
	 * <br>connected through the ManagedConnection
	 * @see javax.resource.spi.ManagedConnectionMetaData#getEISProductVersion()
	 */
	public String getEISProductVersion() throws ResourceException {
		return eisProductVersion;
	}

	/**
	 * <br>Returns maximum limit on number of active concurrent 
	 * <br>connections that an EIS instance can support across 
	 * <br>client processes
	 * @see javax.resource.spi.ManagedConnectionMetaData#getMaxConnections()
	 */
	public int getMaxConnections() throws ResourceException {
		return managedConnection.getMaxConnectionHandles();
	}

	/**
	 * <br>Returns ""
	 * @see javax.resource.spi.ManagedConnectionMetaData#getUserName()
	 */
	public String getUserName() throws ResourceException {
		return userName;
	}

}
