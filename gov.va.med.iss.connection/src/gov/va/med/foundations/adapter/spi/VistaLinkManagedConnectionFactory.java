//SK
package gov.va.med.foundations.adapter.spi;

import gov.va.med.foundations.adapter.cci.VistaLinkConnectionFactory;
import gov.va.med.foundations.adapter.cci.VistaLinkResourceException;
import gov.va.med.foundations.adapter.heartbeat.VistaHeartBeatTimerManager;
import gov.va.med.foundations.utilities.ExceptionUtils;

import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.security.auth.Subject;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * An object of this class is a factory of both ManagedConnection 
 * <br>and connection factory instances. This class supports connection 
 * <br>pooling by defining methods for matching and creating connections.
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaLinkManagedConnectionFactory
	implements ManagedConnectionFactory, Serializable {

	public static final String ADAPTER_VERSION = "1.0";

	/**
	 * The logger for this class
	 */
	private static final Logger logger =
		Logger.getLogger(VistaLinkManagedConnectionFactory.class);

	/**
	 * The ip address of the Host M Server this factory will create
	 * <br>connections to
	 */
	private String hostIpAddress;

	/**
	 * The port of the Host M Server this factory will create connections to
	 */
	private int hostPort;

	/**
	 * The maximum number of connection handles that this factory's
	 * <br>managed connections will be able to allocate
	 */
	private int maxConnectionHandles = 1;

	/**
	 * The log writer that will be set by the application server
	 */
	private PrintWriter printWriter;

	/**
	 * The socket time out multiplier
	 * <br>This value is multiplied by the heart beat rate to determine
	 * <br>the socket time out
	 */
	private int heartBeatRateMultiplierForSocketTimeOut;

	/**
	 * The timer manager for this factory and its created managed connections
	 */
	private VistaHeartBeatTimerManager timerManager;

	/**
	 * Constructor
	 * <br>creates a new VistaHeartBeatTimerManager
	 */
	public VistaLinkManagedConnectionFactory() {
		if (logger.isDebugEnabled()) {
			String debugMsg = 
				getLoggerFormattedString(
					"Constructing");					
			logger.debug(debugMsg);
		}

		timerManager = new VistaHeartBeatTimerManager();
		setHeartBeatRateMultiplierForSocketTimeOut(10);
	}

	/**
	 * <br>Creates a connection factory instance. The connection 
	 * <br>factory instance gets initialized with a default connection 
	 * <br>manager provided by the resource adapter.
	 *
	 * @return VistaLinkConnectionFactory instance
	 * @see javax.resource.spi.ManagedConnectionFactory#createConnectionFactory(javax.resource.spi.ConnectionManager)
	 */
	public Object createConnectionFactory(ConnectionManager mgr)
		throws ResourceException {

		if (logger.isDebugEnabled()) {
			String debugMsg = 
				getLoggerFormattedString(
					"createConnectionFactory->managed");					
			logger.debug(debugMsg);
		}

		return new VistaLinkConnectionFactory(this, mgr);
	}

	/**
	 * <br>Creates a connection factory instance with the default 
	 * <br>connection manager
	 * @see javax.resource.spi.ManagedConnectionFactory#createConnectionFactory()
	 */
	public Object createConnectionFactory() throws ResourceException {
		if (!(logger == null)) {
		if (logger.isDebugEnabled()) {
			String debugMsg = 
				getLoggerFormattedString(
					"createConnectionFactory->unmanaged");					
			logger.debug(debugMsg);
		}
		}
		return new VistaLinkConnectionFactory(this, null);
	}

	/**
	 * <br>Creates a new physical connection to M
	 *
	 * @param subject Caller's security information
	 * @param info Additional resource adapter specific connection request information
	 * @return ManagedConnection instance
	 * @throws VistaLinkResourceException
	 * @see javax.resource.spi.ManagedConnectionFactory#createManagedConnection(javax.security.auth.Subject, javax.resource.spi.ConnectionRequestInfo)
	 */
	public ManagedConnection createManagedConnection(
		Subject subject,
		ConnectionRequestInfo info)
		throws ResourceException {

		try {

			if (logger.isDebugEnabled()) {
				String debugMsg = 
					getLoggerFormattedString(
						"createManagedConnection");					
				logger.debug(debugMsg);
			}
//			logDebug(debugMsg);

			VistaLinkManagedConnection mc =
				new VistaLinkManagedConnection(
					this,
					subject,
					InetAddress.getByName(getHostIPAddress()),
					getHostPort(),
					getMaxConnectionHandles());

			return mc;

		} catch (UnknownHostException e) {

			String errStr = "Unknown Host";

			if(logger.isEnabledFor(Priority.ERROR)){
				String errMsg = 
					getLoggerFormattedStringWStackTrace(
						errStr, e);					
	
				logger.error(errMsg);
			}
//			logError(errMsg, e);			

			throw new VistaLinkResourceException(errStr, e);

		} catch (VistaLinkResourceException e) {

			if(logger.isEnabledFor(Priority.ERROR)){
				String errMsg = 
					getLoggerFormattedStringWStackTrace(	
					"Resource exception in creatingManagedConnection", 
					e);					
	
				logger.error(errMsg);
			}
//			logError(errMsg, e);			

			throw e;
		}
	}

	/**
	 * <br>Returns a matched connection from the candidate set of connections.
	 *<br>This method returns a ManagedConnection instance that is the best 
	 *<br>match for handling the connection allocation request.
	 *<br>The method iterates through a Set of type VistaLinkManagedConnection
	 *<br>determines if the HostIPAddress and HostPort are the same as 
	 *<br>those defined for this instance of the VistaLinkManagedConnectionFactory
	 *<br>and then determines if the number of connection handles the 
	 *<br>VistaLinkManagedConnection holds is less than or equal to the allotted 
	 *<br>number if connection handles
	 *<br>
	 *
	 * @param connectionSet candidate connection set
	 * @param subject caller's security information
	 * @param info additional resource adapter specific connection request information
	 * @return ManagedConnection if resource adapter finds an acceptable match otherwise null
	 * @throws VistaLinkResourceException
	 * @see javax.resource.spi.ManagedConnectionFactory#matchManagedConnections(java.util.Set, javax.security.auth.Subject, javax.resource.spi.ConnectionRequestInfo)
	 */
	public ManagedConnection matchManagedConnections(
		Set connectionSet,
		Subject subject,
		ConnectionRequestInfo info)
		throws ResourceException {

		try {
			String debugMsg;
			if (logger.isDebugEnabled()) {
				debugMsg = 
					getLoggerFormattedString(
						"matchManagedConnection");					
				logger.debug(debugMsg);
			}
//			logDebug(debugMsg);

			Iterator it = connectionSet.iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (obj instanceof VistaLinkManagedConnection) {
					VistaLinkManagedConnection mc =
						(VistaLinkManagedConnection) obj;
					VistaLinkManagedConnectionFactory mcf =
						(VistaLinkManagedConnectionFactory) mc
							.getManagedConnectionFactory();
					if (this.equals(mcf)) {
						//if (mc.isConnectionHandleAvailable()) {
						if (subject != null) {
							throw new VistaLinkResourceException("security not yet supported.");
						} else { //if(info != null){
							/*	if (mc._ConInfo.equals(info)){
									                    	return mc;
														}
								}else{*/

	
							if (logger.isDebugEnabled()) {
								debugMsg = 
									getLoggerFormattedString(
										"matchManagedConnection->Found match");					
								logger.debug(debugMsg);
							}
//							logDebug(debugMsg);

							return mc;
						}
						//}
					}
				}
			}
	
			if (logger.isDebugEnabled()) {
				debugMsg = 
					getLoggerFormattedString(
						"matchManagedConnection->no match found");					
				logger.debug(debugMsg);
			}
//			logDebug(debugMsg);


			return null;
		} catch (VistaLinkResourceException e) {
	
			if(logger.isEnabledFor(Priority.ERROR)){
				String errMsg = 
					getLoggerFormattedStringWStackTrace(	
					"Error matching connections", e);					
	
				logger.error(errMsg);
			}
//			logError(errMsg, e);			

			throw e;
		}
	}

	/**
	 * @see javax.resource.spi.ManagedConnectionFactory#getLogWriter()
	 */
	public PrintWriter getLogWriter() throws ResourceException {
		return printWriter;
	}

	/**
	 * @see javax.resource.spi.ManagedConnectionFactory#setLogWriter(java.io.PrintWriter)
	 */
	public void setLogWriter(PrintWriter out) throws ResourceException {
		printWriter = out;
	}


	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof VistaLinkManagedConnectionFactory) {
			return (boolean) (obj.hashCode() == this.hashCode());
		} else {
			return false;
		}
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.toString().hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return (
			(new StringBuffer())
				.append(this.getClass().getName())
				.append("[]")
				.append(hostIpAddress)
				.append("[]")
				.append(hostPort)
				.append("[]")
				.append(maxConnectionHandles))
			.toString();
	}

	/**
	 * Returns the maxConnectionHandle.
	 * @return int
	 */
	public int getMaxConnectionHandles() {
		return maxConnectionHandles;
	}

	/**
	 * Sets the maxConnectionHandle.
	 * @param maxConnectionHandle The maxConnectionHandle to set
	 */
	public void setMaxConnectionHandles(int maxConnectionHandles) {
		this.maxConnectionHandles = maxConnectionHandles;
	}

	/**
	 * Returns the hostPort.
	 * @return int
	 */
	public int getHostPort() {
		return hostPort;
	}

	/**
	 * Sets the hostPort.
	 * @param hostPort The hostPort to set
	 */
	public void setHostPort(int hostPort) {
		this.hostPort = hostPort;
	}

	/**
	 * Method setHostIPAddress.
	 * <br> sets the host IP Address
	 * @param hostipaddress
	 */
	public void setHostIPAddress(String hostipaddress) {
		this.hostIpAddress = hostipaddress;
	}

	/**
	 * Method getHostIPAddress.
	 * <br> gets the host IP Address
	 * @return String
	 */
	public String getHostIPAddress() {
		return hostIpAddress;
	}


	/**
	 * Method logError.
	 * @param method
	 * @param e
	 */
	private void logError(String message, Exception e) {
//
	}
		

	/**
	 * Method logDebug.
	 * @param debug
	 */
	private void logDebug(String debug) {
//
	}

	/**
	 * Method getLoggerFormattedString.
	 * @param log
	 * @return String
	 */
	private String getLoggerFormattedString(String log) {
		return (new StringBuffer())
			.append(this.toString())
			.append("\n\t")
			.append(log)
			.toString();
	}

	/**
	 * Method getLoggerFormattedStringWStackTrace.
	 * @param e
	 * @return String
	 */
	private String getLoggerFormattedStringWStackTrace
		(Throwable e){
				
		return getLoggerFormattedString(
			ExceptionUtils.getFullStackTrace(e));
					
	}
		
	/**
	 * Method getLoggerFormattedStringWStackTrace.
	 * @param log
	 * @param e
	 * @return String
	 */
	private String getLoggerFormattedStringWStackTrace
		(String log, Throwable e){
				
		return getLoggerFormattedString((new StringBuffer())
			.append(log)
			.append("\n\t")
			.append(ExceptionUtils.getFullStackTrace(e))
			.toString());
					
	}


	/**
	 * Returns the heartBeatRateMultiplierForSocketTimeOut.
	 * @return int
	 */
	public int getHeartBeatRateMultiplierForSocketTimeOut() {
		return heartBeatRateMultiplierForSocketTimeOut;
	}

	/**
	 * Sets the heartBeatRateMultiplierForSocketTimeOut.
	 * @param heartBeatRateMultiplierForSocketTimeOut The heartBeatRateMultiplierForSocketTimeOut to set
	 */
	public void setHeartBeatRateMultiplierForSocketTimeOut(int heartBeatRateMultiplierForSocketTimeOut) {
		this.heartBeatRateMultiplierForSocketTimeOut =
			heartBeatRateMultiplierForSocketTimeOut;
	}

	/**
	 * Method getSocketTimeOut.
	 * <br>gets the timeout value for managed connections
	 * @return int
	 */
	protected int getSocketTimeOut() {
		return getHeartBeatRateMultiplierForSocketTimeOut()
			* (int) timerManager.getHeartBeatRate();
	}

	/**
	 * Returns the timerManager.
	 * @return VistaHeartBeatTimerManager
	 */
	protected VistaHeartBeatTimerManager getHeartBeatTimerManager() {
		return timerManager;
	}

}
