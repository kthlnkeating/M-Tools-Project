package gov.va.med.foundations.adapter.spi;

import gov.va.med.foundations.utilities.ExceptionUtils;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;

// import x.gov.va.med.iss.log4j.*;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * This class represents the default(J2SE) ConnectionEventListener
 * <br>that listens for the connectionClosed() and 
 * <br>connectionErrorOccurred events fired by VistaLinkManagedConnection
 * <br>This class will not be used in J2EE as application server 
 * <br>will provide its own implementation
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaLinkManagedConnectionEventListener
	implements ConnectionEventListener {

	/**
	 * The logger for this class
	 */
	private static final Logger logger =
		Logger.getLogger(VistaLinkManagedConnectionEventListener.class);

	/**
	 * Constructor for VistaLinkManagedConnectionEventListener.
	 */
	public VistaLinkManagedConnectionEventListener() {
		super();
	}

	/**
	 * <br>Destroys the VistaLinkManagedConnection
	 * @see javax.resource.spi.ConnectionEventListener#connectionClosed(javax.resource.spi.ConnectionEvent)
	 */
	public void connectionClosed(ConnectionEvent ce) {
		try {
			((VistaLinkManagedConnection) ce.getSource()).destroy();
		} catch (ResourceException e) {

			if(logger.isEnabledFor(Priority.ERROR)){
				String errMsg = (new StringBuffer())
					.append(
					"Error occured while handling managedConnection close() event")
					.append("\n\t")
					.append(ExceptionUtils
							.getFullStackTrace(e))
					.toString();
						
				logger.error(errMsg);
			}

		}
	}

	/**
	 * @see javax.resource.spi.ConnectionEventListener#localTransactionStarted(javax.resource.spi.ConnectionEvent)
	 */
	public void localTransactionStarted(ConnectionEvent arg0) {
	}

	/**
	 * @see javax.resource.spi.ConnectionEventListener#localTransactionCommitted(javax.resource.spi.ConnectionEvent)
	 */
	public void localTransactionCommitted(ConnectionEvent arg0) {
	}

	/**
	 * @see javax.resource.spi.ConnectionEventListener#localTransactionRolledback(javax.resource.spi.ConnectionEvent)
	 */
	public void localTransactionRolledback(ConnectionEvent arg0) {
	}

	/**
	 * <br>Destroys the VistaLinkManagedConnection
	 * @see javax.resource.spi.ConnectionEventListener#connectionErrorOccurred(javax.resource.spi.ConnectionEvent)
	 */
	public void connectionErrorOccurred(ConnectionEvent ce) {
		try {
			
			VistaLinkManagedConnection mc = 
				(VistaLinkManagedConnection) ce.getSource();
			mc.cleanup();
			mc.destroy();

		} catch (ResourceException e) {
			if(logger.isEnabledFor(Priority.ERROR)){

				String errMsg = (new StringBuffer())
					.append(
					"Error occured while handling managedConnection error() event")
					.append("\n\t")
					.append(ExceptionUtils
							.getFullStackTrace(e))
					.toString();
						
				logger.error(errMsg);
			}

		}
	}

}
