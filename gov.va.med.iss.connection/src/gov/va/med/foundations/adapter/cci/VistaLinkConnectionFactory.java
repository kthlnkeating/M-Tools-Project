package gov.va.med.foundations.adapter.cci;

import gov.va.med.foundations.adapter.spi.VistaLinkConnectionManager;
import gov.va.med.foundations.adapter.spi.VistaLinkManagedConnectionFactory;
import gov.va.med.foundations.utilities.ExceptionUtils;

import java.io.PrintWriter;
import java.io.Serializable;

import javax.naming.Reference;
import javax.resource.NotSupportedException;
import javax.resource.Referenceable;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;
import javax.resource.cci.RecordFactory;
import javax.resource.cci.ResourceAdapterMetaData;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapterInternalException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
/**
 * This implementation class provides provides an inteface for getting
 * connection to an EIS instance.  For each type of adapter derived from this
 * class, it should overide/implement getConnection() to achieve
 * adapter-specific connection.
 *
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaLinkConnectionFactory
	implements ConnectionFactory, Serializable, Referenceable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The logger used by this class
	 */

	private static final Logger logger =
		Logger.getLogger(VistaLinkConnectionFactory.class);

	/**
	 * The managed connection factory used to allocate managed connections
	 */
	private ManagedConnectionFactory managedFactory;

	/**
	 * The connection manager used to allocate connection handles
	 */
	private ConnectionManager connectionManager;

	/**
	 * The reference
	 */
	private Reference reference;

	/**
	 * The PrintWriter used for logging to app server
	 */
	private PrintWriter printWriter;

	/**
	 * Default empty constructor.
	 * @see java.lang.Object#Object()
	 */
	public VistaLinkConnectionFactory() {
		this(null, null);
	}

	/**
	 * Method VistaLinkConnectionFactory. constructor
	 * @param managedFactory - the factory that the connection manager will use
	 * <br>to construct managed connections
	 * @param connectionManager - the connection manager that will allocate new
	 * <br>VistaLinkConnections. This value is null in an unmanaged environment
	 * <br>and defaults to VistaLinkConnectionManager
	 * <br>this value is specified by the application server in a managed
	 * environment
	 */
	public VistaLinkConnectionFactory(
		VistaLinkManagedConnectionFactory managedFactory,
		ConnectionManager connectionManager) {
		this.managedFactory = managedFactory;
		if (connectionManager == null) {
			this.connectionManager = new VistaLinkConnectionManager();
		} else {
			this.connectionManager = connectionManager;
		}
		reference = null;
		printWriter = null;
	}

	/**
	 * Method allocateConnection
	 * <br>Allocates a VistaLinkConnection Connection Handle using the 
	 * <br>specified connection manager
	 * 
	 * @return Connection - The connection handle that may be used to 
	 * <br>interact with M
	 * @throws ResourceException
	 */
	private Connection allocateConnection() throws ResourceException {
		if (managedFactory == null) {
			throw new ResourceAdapterInternalException("managedConnectionFactory is not set for this connectionFactory");
		}
		return (Connection) connectionManager.allocateConnection(
			managedFactory,
			null);
	}

	/**
	 * Method getLogWriter.
	 * 
	 * @return PrintWriter
	 * @throws ResourceException
	 */
	public PrintWriter getLogWriter() throws ResourceException {
		return printWriter;
	}

	/**
	 * Method setLogWriter.
	 * 
	 * @param printWriter
	 * @throws ResourceException
	 */
	public void setLogWriter(PrintWriter printWriter)
		throws ResourceException {
		this.printWriter = printWriter;
	}

	/**
	 * Returns the meta data information associated with this adapater.
	 * 
	 * @see javax.resource.cci.ConnectionFactory#getMetaData()
	 */
	public ResourceAdapterMetaData getMetaData() throws ResourceException {
		return new VistaLinkResourceAdapterMetaData();
	}

	/**
	 * Method for Referenceable interface
	 * 
	 * @see javax.naming.Referenceable#getReference()
	 */
	public Reference getReference() {
		return reference;
	}

	/**
	 * Method for Referenceable interface
	 * 
	 * @see javax.resource.Referenceable#setReference(javax.naming.Reference)
	 */
	public void setReference(Reference reference) {
		this.reference = reference;
	}

	/**
	 * Gets  a VistaLinkConnection handle with the connectionspec
	 * <br>Empty method. throws NotSupportedException
	 * 
	 * @see javax.resource.cci.ConnectionFactory#getConnection(javax.resource.cci.ConnectionSpec)
	 */
	public Connection getConnection(ConnectionSpec connectionSpec)
		throws ResourceException {
		throw new NotSupportedException("getConnection(ConnectionSpec) is not supported at this time. Use getConnection() instead.");
	}

	/**
	 * Gets a VistaLinkConnection connection handle
	 * <br>calls allocateConnection()
	 * 
	 * @see javax.resource.cci.ConnectionFactory#getConnection()
	 */
	public Connection getConnection() throws ResourceException {
		try {
			return allocateConnection();
		} catch (ResourceException e) {
			if(logger.isEnabledFor(Level.ERROR)){
				String errMsg = (new StringBuffer())
					.append(
					"Can not allocate VistaLinkConnection.")
					.append("\n\t")
					.append(ExceptionUtils
							.getFullStackTrace(e))
					.toString();
				logger.error(errMsg);
			}
			throw e;
		}
	}

	/**
	 * <br>Empty method. throws NotSupportedException
	 * 
	 * @see javax.resource.cci.ConnectionFactory#getRecordFactory()
	 */
	public RecordFactory getRecordFactory() throws ResourceException {
		throw new NotSupportedException("getRecordFactory() is not supported at this time.");
	}

	/**
	 * <br> Method getVistaLinkConnectionFactory creates
	 * VistaLinkConnectionFactory to be used in non-managed environment (J2SE).
	 * <br> This method should never be called in managed environment (J2EE) as
	 * application server is responsible for creating ConnectionFactories.
	 * <br>Client code in that case uses JNDI lookup to get a reference to the
	 * ConnectionFactory.
	 * 
	 * @param hostIPAddress - the IP address to connect to
	 * @param hostPort - the port to open
	 * @return VistaLinkConnectionFactory - The connection factory that will
	 * <br>return Connections for the specified IPAddress and port
	 * @throws ResourceException
	 */
	public static final VistaLinkConnectionFactory getVistaLinkConnectionFactory(
		String hostIPAddress,
		int hostPort)
		throws ResourceException {
		try {
			VistaLinkManagedConnectionFactory mcf =
				new VistaLinkManagedConnectionFactory();
			mcf.setHostIPAddress(hostIPAddress);
			mcf.setHostPort(hostPort);
			return (VistaLinkConnectionFactory) mcf.createConnectionFactory();
		} catch (ResourceException e) {
			if(logger.isEnabledFor(Level.ERROR)){
				String errMsg = (new StringBuffer())
					.append(
					"Can not create VistaLinkConnectionFactory.")
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
