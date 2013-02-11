package gov.va.med.foundations.adapter.spi;

import gov.va.med.foundations.adapter.cci.VistaLinkResourceException;
import gov.va.med.foundations.utilities.ExceptionUtils;

import java.io.Serializable;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;

// import x.gov.va.med.iss.log4j.*;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * The default ConnectionManager implementation for the non-managed(J2SE)
 * <br>scenario  This provides a hook for a resource adapter to pass 
 * <br>a connection request to an application server.
 *
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaLinkConnectionManager
	implements ConnectionManager, Serializable {

	/**
	 * The event listener this connection manager will use to 
	 * <br>manage the managedConnection it allocates
	 */
	VistaLinkManagedConnectionEventListener eventListener;

	/**
	 * The logger used by this class
	 */
	private static final Logger logger =
		Logger.getLogger(VistaLinkConnectionManager.class);

	/**
	 * Constructor - creates a VistaLinkManagedConnectionEventListener
	 * <br>to manage events fired by the VistaLinkManagedConnections
	 * <br>that this instance allocates
	 */
	public VistaLinkConnectionManager() {
		if (logger.isDebugEnabled()) {
			logger.debug("Constructing");
		}

		eventListener = new VistaLinkManagedConnectionEventListener();
	}

	/**
	 * <br>allocates a new managed connection from the specified
	 * <br>managed connection factory. 

	 * @see javax.resource.spi.ConnectionManager#allocateConnection(javax.resource.spi.ManagedConnectionFactory, javax.resource.spi.ConnectionRequestInfo)
	 */
	public Object allocateConnection(
		ManagedConnectionFactory managedConnFactory,
		ConnectionRequestInfo info)
		throws ResourceException {

		try {

			if (logger.isDebugEnabled()) {
				logger.debug("allocating connection");
			}

			ManagedConnection managedConn =
				managedConnFactory.createManagedConnection(null, info);

			managedConn.addConnectionEventListener(eventListener);

			return managedConn.getConnection(null, info);
		} catch (VistaLinkResourceException e) {

			if(logger.isEnabledFor(Priority.ERROR)){

				String errMsg = (new StringBuffer())
					.append(
					"VistaLinkResourceException attempting to allocate connection.")
					.append("\n\t")
					.append(ExceptionUtils
							.getFullStackTrace(e))
					.toString();
						
				logger.error(errMsg);
			}
			throw e;

		}
	}

}
