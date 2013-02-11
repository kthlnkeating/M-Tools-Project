package gov.va.med.foundations.adapter.cci;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionMetaData;
import javax.resource.spi.ManagedConnection;

/**
 * provides information about an EIS instance connected through a 
 * Connection instance. A component calls the method 
 * Connection.getMetaData() to get a ConnectionMetaData instance
 *
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaLinkConnectionMetaData implements ConnectionMetaData {

	/**
	 * The managed connection that return the meta data information
	 */
	private ManagedConnection managedConnection;

	/**
	 * Method VistaLinkConnectionMetaData - Constructor
	 * @param mc
	 */
	public VistaLinkConnectionMetaData(ManagedConnection mc) {
		managedConnection = mc;
	}

	/**
	 * Returns Product name of this adapter
	 * 
	 * @see javax.resource.cci.ConnectionMetaData#getEISProductName()
	 */
	public String getEISProductName() throws ResourceException {
		return managedConnection.getMetaData().getEISProductName();
	}

	/**
	 * Returns product version of this adapter
	 * 
	 * @see javax.resource.cci.ConnectionMetaData#getEISProductVersion()
	 */
	public String getEISProductVersion() throws ResourceException {
		return managedConnection.getMetaData().getEISProductVersion();
	}

	/**
	 * Returns name of the user associated with the ManagedConnection instance.
	 * 
	 * @see javax.resource.cci.ConnectionMetaData#getUserName()
	 */
	public String getUserName() throws ResourceException {
		return managedConnection.getMetaData().getUserName();
	}

	//----------------------------------------------------------------------------------------------

}
