package gov.va.med.foundations.adapter.spi;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.adapter.cci.VistaLinkConnectionMetaData;
import gov.va.med.foundations.adapter.cci.VistaLinkResourceException;
import gov.va.med.foundations.adapter.heartbeat.HeartBeatInitializationFailedException;
import gov.va.med.foundations.adapter.record.VistaLinkFaultException;
import gov.va.med.foundations.adapter.record.VistaLinkRequestVO;
import gov.va.med.foundations.adapter.record.VistaLinkResponseFactory;
import gov.va.med.foundations.adapter.record.VistaLinkResponseFactoryImpl;
import gov.va.med.foundations.adapter.record.VistaLinkResponseVO;
import gov.va.med.foundations.net.VistaSocketException;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.foundations.rpc.RpcResponseFactory;
import gov.va.med.foundations.utilities.ExceptionUtils;
import gov.va.med.foundations.utilities.FoundationsException;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Iterator;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.ConnectionMetaData;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**  
 * This class represents a managed connection to M
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaLinkManagedConnection implements ManagedConnection {
	/**
	 * constant value indicating that the managed connection 
	 * <br>can allocate unlimited connection handles
	 */
	private static final int UNLIMITED_CONNECTION_HANDLES = -1;

	/**
	 * constant error message indicating there are no more available 
	 * <br>connection handles
	 */
	private static final String EXCEPTION_CONNECTION_HANDLES_EXCEEDED =
		"There are no available connection handles.";

	/**
	 * The logger used by this class
	 */

	private static final Logger logger =
		Logger.getLogger(VistaLinkManagedConnection.class);

	/**
	 * The event notifier this class will use to notify event listeners
	 * <br>of CONNECTION_CLOSED and CONNECTION_ERROR_OCCURRED events
	 */
	private VistaLinkConnectionEventNotifier connectionEventNotifer;

	/**
	 * The managed connection factory that constructed this managedConnection
	 */
	private VistaLinkManagedConnectionFactory managedConnectionFactory;


	/**
	 * The log writer specified by the Application server
	 */
	private PrintWriter logWriter;

	/**
	 * The ip address of the connected host M server
	 */
	private InetAddress hostAddr;

	/**
	 * The port of the connected host M Server
	 */
	private int hostPort;

	/**
	 * A set to contain all allocated connection handles
	 */
	private HashSet<VistaLinkConnection> conSet;

	/**
	 * The raw socket connection to the host M Server
	 */
	private VistaSocketConnection socketCon;

	/**
	 * The maximum number of connection handles this managed connection 
	 * <br>can allocate
	 */
	private int maxConnectionHandles;

	/**
	 * Indicates whether this managed connection is valid
	 * <br>Will be true if the socket has been created and the managed 
	 * <br>connection has been added to the HeartBeatManager
	 * <br>Will be false if he socket failed to create, or a socket 
	 * <br>transfer failed, or the socket has been removed from the
	 * <br>HeartBeatManager
	 */
	private boolean valid;

	/**
	 * Indicates the last time an interaction with the host M Server was
	 * <br>executed
	 */
	private long lastInteractionTimeMillis;

	@SuppressWarnings("unused")
	/**
	 * The JAAS subject - not used
	 */
	private Subject subject;


	/**
	 * Method VistaLinkManagedConnection.
	 * <br>Constructs this instance of the managed connection
	 * <br>Creates a new raw socket connection with timeout
	 * <br>Adds this managed connection to the Heart beat timer 
	 * <br>Sets the socketTimeout property
	 * @param mcf - the managed connection factory that created this managed
	 * connection
	 * @param subject - not used
	 * @param address - the ip address to connect to
	 * @param port - the port to open
	 * @param maxConnectionHandles - the maximum number of allowable connection
	 * handles
	 * @throws VistaLinkResourceException
	 */
	protected VistaLinkManagedConnection(
		VistaLinkManagedConnectionFactory mcf,
		Subject subject,
		InetAddress address,
		int port,
		int maxConnectionHandles)
		throws VistaLinkResourceException {

		this.subject = subject;
		setHostAddr(address);
		setHostPort(port);

/*
		if (logger.isDebugEnabled()) {
			String debugMsg = 
				getLoggerFormattedString(
					"Constructing");					

			logger.debug(debugMsg);
		}
*/
//		logDebug(debugMsg);

		try {
			socketCon = new VistaSocketConnection(address, port);
		} catch (VistaSocketException e) {


			String errStr = 
				"Can not create VistaSocketConnection";
/*
			if(logger.isEnabledFor(Priority.ERROR)){

				String errMsg = 
					getLoggerFormattedStringWStackTrace(
						errStr, e);					
	
				logger.error(errMsg);
			}
*/
//			logError(errMsg, e);			

			throw new VistaLinkResourceException(
				errStr, e);

		}
		setManagedConnectionFactory(mcf);
		conSet = new HashSet<VistaLinkConnection>();
		connectionEventNotifer = new VistaLinkConnectionEventNotifier();
		setMaxConnectionHandles(maxConnectionHandles);

		addToTimerTask();

		try {
			setSocketTimeOut();
		} catch (VistaSocketException e) {
			String errStr = 
				"Can not set socket timeout";
/*
			if(logger.isEnabledFor(Priority.ERROR)){
				String errMsg = 
					getLoggerFormattedStringWStackTrace(
						errStr, e);					
	
	
				logger.error(errMsg);
			}
*/
//			logError(errMsg, e);			

			throw new VistaLinkResourceException(
				errStr, e);
		}
	}

	/**
	 * <br>Adds a connection event listener to the event notifier for
	 * <br>this managed connection
	 * @param listener - the ConnectionEventListener to add
	 * @see javax.resource.spi.ManagedConnection#addConnectionEventListener(javax.resource.spi.ConnectionEventListener)
	 */
	public void addConnectionEventListener(ConnectionEventListener listener) {


/*
		if (logger.isDebugEnabled()) {
			String debugMsg = 
				getLoggerFormattedString(
					"addConnectionEventListener");					
			logger.debug(debugMsg);
		}
*/
//		logDebug(debugMsg);


		connectionEventNotifer.addConnectorListener(listener);
	}

	/**
	 * <br>Removes a connection event listener from the event notifier for
	 * <br>this managed connection
	 * @param listener - the ConnectionEventListener to remove
	 * @see javax.resource.spi.ManagedConnection#removeConnectionEventListener(javax.resource.spi.ConnectionEventListener)
	 */
	public void removeConnectionEventListener(ConnectionEventListener listener) {
/*
		if (logger.isDebugEnabled()) {
			String debugMsg = 
				getLoggerFormattedString(
					"removeConnectionEventListener");					
			logger.debug(debugMsg);
		}
*/
//		logDebug(debugMsg);

		connectionEventNotifer.removeConnectorListener(listener);
	}

	/**
	 * Method addConHandle.
	 * <br>Adds a VistaLinkConnection object (handle) to conSet
	 * @param con - the connection handle to add
	 * @throws ConnectionHandlesExceededException - thrown if maximum 
	 * number of allowable VistaLinkConnection handles has been reached
	 */
	private void addConHandle(VistaLinkConnection con)
		throws ConnectionHandlesExceededException {

/*
		if (logger.isDebugEnabled()) {
			String debugMsg = 
				getLoggerFormattedString(
					"addConHandle");					
			logger.debug(debugMsg);
		}
*/
//		logDebug(debugMsg);

		if (isConnectionHandleAvailable()) {
			conSet.add(con);
		} else {

			String errStr = (new StringBuffer())
				.append(EXCEPTION_CONNECTION_HANDLES_EXCEEDED)
				.append("[")
			 	.append(getMaxConnectionHandles())
				.append("]").toString();



			throw new ConnectionHandlesExceededException(errStr);
		}
	}

	/**
	 * Method isConnectionHandleAvailable.
	 * <br>Returns true if number of Connection Handles of 
	 * <br>this instance of the VistaLinkManagedCoonection is 
	 * <br>less than the Max number allotted
	 * @return boolean
	 */
	protected boolean isConnectionHandleAvailable() {
		if (getMaxConnectionHandles() == UNLIMITED_CONNECTION_HANDLES) {
			return true;
		}
		if (conSet.size() < getMaxConnectionHandles()) {
			return true;
		}
		return false;
	}

	/**
	 * Method removeConHandle.
	 * <br>Removes the connection Handle from conSet
	 * @param con - the connection handle to remove
	 */
	private void removeConHandle(VistaLinkConnection con) {
/*
		if (logger.isDebugEnabled()) {
			String debugMsg = 
				getLoggerFormattedString(
					"removeConHandle");					
			logger.debug(debugMsg);
		}
*/
//		logDebug(debugMsg);

		conSet.remove(con);
	}

	/**
	 * <br>Used by the container to change the association of an 
	 * <br>application-level connection handle with a ManagedConnection
	 * <br>instance. The container should find the right ManagedConnection
	 * <br>instance and call the associateConnection method
	 * 
	 * <br>This method will throw a ConnectionHandlesExceededException
	 * <br>if there are no more allowable connection handles
	 *
	 * @param connection Application-level connection handle
	 * @throws ConnectionHandlesExceededException
	 * @throws VistaLinkResourceException 
	 * @see javax.resource.spi.ManagedConnection#associateConnection(java.lang.Object)
	 */
	public void associateConnection(Object connection)
		throws ResourceException {

		try {


			if (logger.isDebugEnabled()) {
				String debugMsg = 
					getLoggerFormattedString(
						"associateConnection");					
				logger.debug(debugMsg);
			}
//			logDebug(debugMsg);

			if (!(connection instanceof VistaLinkConnection)) {
				String errStr =
					"This connection is not of type VistaLinkConnection";
				throw new VistaLinkResourceException(errStr);
			}
			if (!isConnectionHandleAvailable()) {

				String errStr = (new StringBuffer())
					.append(EXCEPTION_CONNECTION_HANDLES_EXCEEDED)
					.append("[")
					.append(getMaxConnectionHandles())
					.append("]").toString();

				throw new ConnectionHandlesExceededException(errStr);
			}
			VistaLinkConnection con = (VistaLinkConnection) connection;

			VistaLinkManagedConnection mc = null;
			try {
				mc = con.getManagedConnection();
			} catch (FoundationsException e) {
				
				if (logger.isEnabledFor(Level.ERROR)) {
					String errMsg =
						getLoggerFormattedStringWStackTrace(
							"The managed connection is null and cannot be disassociated, but the operation will continue",
							e);

					logger.error(errMsg);
				}

				mc = null;
			}

			if(mc != null){
				mc.disassociateConnection(con);
			}

			con.setManagedConnection(this);
			this.addConHandle(con);
		} catch (VistaLinkResourceException e) {
			
			if(logger.isEnabledFor(Level.ERROR)){
				String errMsg = 
					getLoggerFormattedStringWStackTrace(
						"Error associating connection handle", 
						e);					
	
				logger.error(errMsg);
			}
//			logError(errMsg, e);			

			throw e;
		}
	}

	/**
	 * Method disassociateConnection.
	 * <br>Disassociates this instance of VistaLinkManagedConnection 
	 * <br>from the VistaLinkConnection handle
	 * @param con
	 */
	public void disassociateConnection(VistaLinkConnection con) {

		if (logger.isDebugEnabled()) {
			String debugMsg = 
				getLoggerFormattedString(
					"disassociate connection");					
			logger.debug(debugMsg);
		}
//		logDebug(debugMsg);
		con.setManagedConnection(null);
		this.removeConHandle(con);
	}

	/**
	 * <br>Application server calls this method to force any cleanup 
	 * <br>on the ManagedConnection instance.  This method is usually 
	 * <br>called by the container after CONNECTION_CLOSED event is 
	 * <br>fired.
	 * 
	 * <br>ManagedConnection should be cleaned-up to the initial state 
	 * <br>that is was before getConnection() was called on this object
	 * <br> and be ready to be returned to the connection pool.
	 * @see javax.resource.spi.ManagedConnection#cleanup()
	 */
	public synchronized void cleanup() throws ResourceException {
		//Cleanup must set managedconnection to initial state
		//		try {

		if (logger.isDebugEnabled()) {
			String debugMsg = 
				getLoggerFormattedString(
					"Managedconnection cleanup");					
			logger.debug(debugMsg);
		}
//		logDebug(debugMsg);

		Iterator<VistaLinkConnection> it = conSet.iterator();
		while (it.hasNext()) {
			VistaLinkConnection con = it.next();
			this.removeConHandle(con);
			con.setManagedConnection(null);
		}
		//		// code for future implementations not to forget
		//		} catch (ResourceException e) {
		//			logErrorWithStackTrace("Can not perform managed connection cleanup.", e);
		//			throw e;
		//		}
	}

	/**
	 * <br>Destroys the physical socket connection to M
	 * <br>Application server calls this method either in case of 
	 * <br>shrinking the pool size or if exception occurs that is 
	 * <br>reported to the application server by firing an exception 
	 * <br>event.
	 * @throws VistaLinkResourceException
	 * @see javax.resource.spi.ManagedConnection#destroy()
	 */
	public synchronized void destroy() throws ResourceException {
		try {

			if (logger.isDebugEnabled()) {
				String debugMsg = 
					getLoggerFormattedString(
						"Destroying managedconnection");					
				logger.debug(debugMsg);
			}
//			logDebug(debugMsg);


			synchronized (this) {
				removeFromTimerTask();
				try {
					socketCon.close();
				} catch (VistaSocketException sockExc) {
					throw new VistaLinkResourceException(
						"Can not close VistaSocketConnection.",
						sockExc);
				}
				socketCon = null;
			}
		} catch (VistaLinkResourceException e) {

			if(logger.isEnabledFor(Level.ERROR)){
				String errMsg = 
					getLoggerFormattedStringWStackTrace(
						"Can not perform destroy on managed connection", 
						e);					
	
				logger.error(errMsg);
			}
//			logError(errMsg, e);			



			throw e;
		}
	}

	/**
	 * Method closeHandle.
	 * <br>Closes the associated connection handle.
	 * <br>Should not perform any cleanup on the managedConnection 
	 * <br>system resources.
	 * @param con
	 */
	public void closeHandle(VistaLinkConnection con) throws ResourceException {
/*
		if (logger.isDebugEnabled()) {
			String debugMsg = 
				getLoggerFormattedString(
					"Closing handle");					
			logger.debug(debugMsg);
		}
*/
//		logDebug(debugMsg);

		removeConHandle(con);
		ConnectionEvent event =
			new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
		event.setConnectionHandle(con);
		connectionEventNotifer.connectionClosed(event);
	}

	/**
	 * <br>Creates a new connection handle for this 
	 * <br>ManagedConnection instance.
	 * <br>If there are no more allowable connection handles
	 * <br>this method will throw ConnectionHandlesExceededException
	 *
	 * @param subject security context as JAAS subject
	 * @param req ConnectionRequestInfo instance
	 * @return VistaLinkConnection Object instance representing the 
	 * <br>connection handle
	 * @throws VistaLinkResourceException
	 * @see javax.resource.spi.ManagedConnection#getConnection(javax.security.auth.Subject, javax.resource.spi.ConnectionRequestInfo)
	 */
	public Object getConnection(Subject subject, ConnectionRequestInfo req)
		throws ResourceException {

		try {
			VistaLinkConnection con = null;

			String debugMsg;
			if (logger.isDebugEnabled()) {
				debugMsg = 
					getLoggerFormattedString(
						"Getting connection handle");					
				logger.debug(debugMsg);
			}
//			logDebug(debugMsg);

			if (isConnectionHandleAvailable()) {
				if (logger.isDebugEnabled()) {
					debugMsg = 
						getLoggerFormattedString(
							"Connection handle available");					
	
					logger.debug(debugMsg);
				}
//				logDebug(debugMsg);

				con = new VistaLinkConnection(this);
				addConHandle(con);
				return con;
			} else {

				String errStr = (new StringBuffer())
					.append(EXCEPTION_CONNECTION_HANDLES_EXCEEDED)
					.append("[")
					.append(getMaxConnectionHandles())
					.append("]").toString();

				throw new ConnectionHandlesExceededException(errStr);
			}
		} catch (VistaLinkResourceException e) {

			if(logger.isEnabledFor(Level.ERROR)){
				String errMsg = 
					getLoggerFormattedStringWStackTrace(
						"Error getting connection", 
						e);					
	
				logger.error(errMsg);
			}
//			logError(errMsg, e);			


			throw e;
		}
	}

	/**
	 * <br>This method is not supported and will throw a 
	 * <br>NotSupportedException
	 * @throws ResourceException
	 * @see javax.resource.spi.ManagedConnection#getLocalTransaction()
	 */
	public LocalTransaction getLocalTransaction() throws ResourceException {
		throw new NotSupportedException("Transaction Not Supported!");
	}


	/**
	 * <br>Returns information about this managed connection 
	 * @throws ResourceException
	 * @see javax.resource.spi.ManagedConnection#getMetaData()
	 */
	public ManagedConnectionMetaData getMetaData() throws ResourceException {
		return new VistaLinkManagedConnectionMetaData(this);
	}

	/**
	 * Method getConnectionMetaData.
	 * <br>Delegated from VistaLinkConnection class to return 
	 * <br>connection oriented metadata
	 * @return ConnectionMetaData
	 * @throws ResourceException
	 */
	public ConnectionMetaData getConnectionMetaData()
		throws ResourceException {
		VistaLinkConnectionMetaData ret = new VistaLinkConnectionMetaData(this);
		return ret;
	}

	/**
	 * <br>Returns distributed transaction resource, not supported
	 * @see javax.resource.spi.ManagedConnection#getXAResource()
	 */
	public XAResource getXAResource() throws ResourceException {
		throw new NotSupportedException("Distributed Transaction Not Supported!");
	}


  /**
   * Method setManagedConnectionFactory.
   * @param mcf
   */
	private void setManagedConnectionFactory(VistaLinkManagedConnectionFactory mcf) {
		managedConnectionFactory = mcf;
	}

	/**
	 * Method getManagedConnectionFactory.
	 * @return VistaLinkManagedConnectionFactory
	 */
	protected VistaLinkManagedConnectionFactory getManagedConnectionFactory() {
		return managedConnectionFactory;
	}

	/**
	 * @see javax.resource.spi.ManagedConnection#setLogWriter(java.io.PrintWriter)
	 */
	public void setLogWriter(PrintWriter wr) throws ResourceException {
		logWriter = wr;
	}

	/**
	 * @see javax.resource.spi.ManagedConnection#getLogWriter()
	 */
	public PrintWriter getLogWriter() throws ResourceException {
		return logWriter;
	}

	/**
	 * Returns the maxConnectionHandles.
	 * @return int
	 */
	public int getMaxConnectionHandles() {
		return maxConnectionHandles;
	}

	/**
	 * Sets the maxConnectionHandles.
	 * @param maxConnectionHandles The maxConnectionHandles to set
	 */
	public void setMaxConnectionHandles(int maxConnectionHandles) {
		this.maxConnectionHandles = maxConnectionHandles;
	}

	/**
	 * Returns the hostAddr.
	 * @return InetAddress
	 */
	public InetAddress getHostAddr() {
		return hostAddr;
	}

	/**
	 * Returns the hostPort.
	 * @return int
	 */
	public int getHostPort() {
		return hostPort;
	}

	/**
	 * Sets the hostAddr.
	 * @param hostAddr The hostAddr to set
	 */
	public void setHostAddr(InetAddress hostAddr) {
		this.hostAddr = hostAddr;
	}

	/**
	 * Sets the hostPort.
	 * @param hostPort The hostPort to set
	 */
	public void setHostPort(int hostPort) {
		this.hostPort = hostPort;
	}

	/**
	 * Method executeRPC. 
	 * <br>Executes an interaction with the RpcResponseFactory
	 * @param request
	 * @return RpcResponse
	 * @throws VistaLinkFaultException
	 * @throws FoundationsException
	 */
	public RpcResponse executeRPC(RpcRequest request)
		throws VistaLinkFaultException, FoundationsException {

		return (RpcResponse) executeInteraction(
			request,
			new RpcResponseFactory());
	}

	/**
	 * Method executeInteraction.
	 * <br>Executes an interaction with M and returns a response 
	 * <br>constructed by the specified response factory
	 * @param requestVO
	 * @param responseFactory
	 * @return VistaLinkResponseVO
	 * @throws VistaLinkFaultException
	 * @throws FoundationsException
	 */
	public VistaLinkResponseVO executeInteraction(
		VistaLinkRequestVO requestVO,
		VistaLinkResponseFactory responseFactory)
		throws VistaLinkFaultException, FoundationsException {


		if (logger.isDebugEnabled()) {
			String debugMsg = 
				getLoggerFormattedString(
					"Executing interaction");					
			logger.debug(debugMsg);
		}
//		logDebug(debugMsg);

		try {
			String requestStr = requestVO.getRequestString();

			String responseStr = getResponseFromSocket(requestStr);

			VistaLinkResponseVO responseVO =
				responseFactory.handleResponse(responseStr, requestVO);

			return responseVO;
		} catch (VistaLinkResourceException e) {

			String errStr = 
			"Can not send/receive data from socket (getResponseFromSocket)";
			
			if(logger.isEnabledFor(Level.ERROR)){
				String errMsg = 
					getLoggerFormattedStringWStackTrace(
						errStr, 
						e);					
	
				logger.error(errMsg);
			}
//			logError(errMsg, e);			

			throw new FoundationsException(errStr, e);

		} catch (FoundationsException e) {

			if(logger.isEnabledFor(Level.ERROR)){
				String errMsg = 
					getLoggerFormattedStringWStackTrace(
						"Can not send/receive data from socket (getResponseFromSocket)", 
						e);					
	
				logger.error(errMsg);
			}
//			logError(errMsg, e);			

			throw e;
		}
	}

	/**
	 * Method executeInteraction.
	 * <br>Executes an interaction and returns a response 
	 * <br>from the default response factory.
	 * <br>VistaLinkResponsefactoryImpl
	 * @param requestVO
	 * @return VistaLinkResponseVO
	 * @throws VistaLinkFaultException
	 * @throws FoundationsException
	 */
	public VistaLinkResponseVO executeSystemInteraction(VistaLinkRequestVO requestVO)
		throws VistaLinkFaultException, FoundationsException {

		return executeInteraction(
			requestVO,
			new VistaLinkResponseFactoryImpl());

	}

	/**
	 * Method getResponseFromSocket. 
	 * <br>executes a transfer with M
	 * @param request
	 * @return String
	 * @throws VistaSocketException
	 * @throws VistaLinkSocketClosedException
	 * @throws VistaLinkResourceException
	 **/
	private synchronized String getResponseFromSocket(String request)
		throws VistaLinkSocketClosedException, 
			VistaLinkResourceException {

		if ((socketCon != null) && (isValid())) {
			setLastInteractionTimeMillis(System.currentTimeMillis());


			if (logger.isDebugEnabled()) {
				String debugMsg = 
					getLoggerFormattedString(
						"Executing transfer");					
				logger.debug(debugMsg);
			}
//			logDebug(debugMsg);

			try {
				return socketCon.transfer(request);
			} catch (VistaSocketException e) {
				removeFromTimerTask();

				String errStr = 
				"Socket timeout or some other VistaSocketException has occurred";

				if(logger.isEnabledFor(Level.ERROR)){
					String errMsg = 
						getLoggerFormattedStringWStackTrace(
							errStr, 
							e);					
	
					logger.error(errMsg);
				}
//				logError(errMsg, e);			

				notifyErrorOccurred(e);

				throw new VistaLinkResourceException(errStr, e);
			}
		} else {
			removeFromTimerTask();

			VistaLinkSocketClosedException e = new
				 VistaLinkSocketClosedException(
				"This managed connection is closed. ");
			
			if(logger.isEnabledFor(Level.ERROR)){
				String errMsg = getLoggerFormattedString(
					"Error accessing socket");
				
				logger.error(errMsg, e);
			}
//			logError(errMsg, e);			
			throw e;
		}

	}

	/**
	 * Method notifyErrorOccurred. 
	 * <br>Notifies event listeners(Application server) that
	 * <br>an error has occurred on this managed connection instance.
	 */
	public void notifyErrorOccurred(Exception e) {

		if(logger.isEnabledFor(Level.FATAL)){
			String errMsg = 
				getLoggerFormattedStringWStackTrace(
					"Fatal Error has occurred, notifying event listeners", 
					e);					
	
			logger.fatal(errMsg); 
		}
//		logError(errMsg, e);

		ConnectionEvent event =
			new ConnectionEvent(
				this,
				ConnectionEvent.CONNECTION_ERROR_OCCURRED,
				e);
		connectionEventNotifer.connectionErrorOccurred(event);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		return (
			(new StringBuffer())
				.append(this.getClass().getName())
				.append("[]")
				.append(hostAddr.getHostAddress())
				.append("[]")
				.append(hostPort)
				.append("[]")
				.append(maxConnectionHandles))
			.toString();

	}

	/**
	 * Method addToTimerTask.
	 * <br>Adds this managed connection to the Heartbeat Timer Task
	 * @throws VistaLinkResourceException
	 */
	private void addToTimerTask() throws VistaLinkResourceException {
		setValid(true);

		try {
			this
				.getManagedConnectionFactory()
				.getHeartBeatTimerManager()
				.addManagedConnection(this);
		} catch (HeartBeatInitializationFailedException e) {
			setValid(false);
			throw e;
		}


		if (logger.isDebugEnabled()) {
			String debugMsg = 
				getLoggerFormattedString(
					"Added managedconnection to Heartbeat TimerTask");					
			logger.debug(debugMsg);
		}
//		logDebug(debugMsg);
	}

	/**
	 * Method removeFromTimerTask.
	 * <br>removes this managed connection from the Heartbeat Timer task
	 */
	private void removeFromTimerTask() {
		setValid(false);
		this
			.getManagedConnectionFactory()
			.getHeartBeatTimerManager()
			.removeManagedConnection(this);


		if (logger.isDebugEnabled()) {
			String debugMsg = 
				getLoggerFormattedString(
					"removed managedconnection to Heartbeat TimerTask");					
			logger.debug(debugMsg);
		}
//		logDebug(debugMsg);

	}

	/**
	 * getLastInteractionTimeMillis
	 *  Returns the lastInteractionTimeMillis.
	 * @return long
	 */
	public long getLastInteractionTimeMillis() {
		return lastInteractionTimeMillis;
	}

	/**
	 * Returns the valid.
	 * @return boolean
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Sets the last interaction time in millis.
	 * @param lastinteractiontimemillis 
	 */
	public void setLastInteractionTimeMillis(long lastinteractiontimemillis) {
		this.lastInteractionTimeMillis = lastinteractiontimemillis;
	}

	/**
	 * Sets the valid.
	 * @param valid  
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	@SuppressWarnings("unused")
	/**
	 * Method logError.To be implemented for App server
	 * @param method
	 * @param e
	 */
	private void logError(String message, Exception e) {
//
	}

	@SuppressWarnings("unused")
	/**
	 * Method logError. To be implemented for App server
	 * @param error
	 */
	private void logError(String error) {
//		
	}


	@SuppressWarnings("unused")
	/**
	 * Method logDebug. To be implemented for App server
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

	@SuppressWarnings("unused")
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
	 * Returns the socketCon.
	 * @return VistaSocketConnection
	 */
	protected VistaSocketConnection getSocketConnection() {
		return socketCon;
	}

	/**
	 * Method setSocketTimeOut.
	 * <br>sets the socket timeout from the value retrieved by the 
	 * <br>managed connection factory
	 * @throws VistaSocketException
	 */
	private void setSocketTimeOut() throws VistaSocketException {

		if (logger.isDebugEnabled()) {
			String debugMsg = 
				getLoggerFormattedString(
					"Setting socket timeout");					
			logger.debug(debugMsg);
		}
//		logDebug(debugMsg);

		getSocketConnection().setTransferTimeOut(
			getManagedConnectionFactory().getSocketTimeOut());
	}

	/**
	 * This implementation will have to change for release to Application Server
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof VistaLinkManagedConnection) {
			return (boolean) (obj.hashCode() == this.hashCode());
		} else {
			return false;
		}
		
	}

	/**
	 * This implementation will have to change for release to Application Server
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.toString().hashCode();
	}

}
