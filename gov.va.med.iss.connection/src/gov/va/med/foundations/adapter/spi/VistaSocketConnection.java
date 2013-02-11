package gov.va.med.foundations.adapter.spi;

import gov.va.med.foundations.net.SocketManager;
import gov.va.med.foundations.net.VistaSocketException;
import gov.va.med.foundations.net.VistaSocketTimeOutException;
import gov.va.med.foundations.utilities.AuditTimer;
import gov.va.med.foundations.utilities.ExceptionUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

//import x.gov.va.med.iss.log4j.*;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193

 * This class represents a raw socket connection to a M-Server
 *
 */
public class VistaSocketConnection extends SocketManager {

	/**
	 * The logger used by this class
	 */
	private static final Logger logger =
		Logger.getLogger(VistaSocketConnection.class);
		
	/**
	 * Audit logger used by this class
	 */
	private static final Logger auditLogger =
		Logger.getLogger(VistaSocketConnection.class.getName() + ".AuditLog");

	/**
	 * The xml request used to send a close request to M
	 */
	protected static final String CLOSE_SOCKET_REQUEST =
		"<VistaLink messageType='gov.va.med.foundations.vistalink.system.request'"
			+ " version='1.0'"
			+ " mode='single call'"
			+ " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
			+ " xsi:noNamespaceSchemaLocation='vlSimpleRequest.xsd'"
		//+ " xmlns='http://med.va.gov/Foundations'"
	+">" + "<Request type='closeSocket'/>" + "</VistaLink>";

	/**
	 * The port this socket is connected to
	 */
	private int port;

	/**
	 * The ip address this socket is connected to
	 */
	private InetAddress address;

	/**
	 * Method VistaSocketConnection Constructor.
	 * @param address
	 * @param port
	 * @throws VistaSocketException 
	 */
	protected VistaSocketConnection(InetAddress address, int port)
		throws VistaSocketException {
		this.address = address;
		this.port = port;
		try {
			setSoc(new Socket(this.address, this.port));
		} catch (IOException e) {

			String errStr = "Can not create TCP/IP socket.";
			if(logger.isEnabledFor(Priority.ERROR)){

				String errMsg = (new StringBuffer())
					.append(errStr)
					.append("\n\t")
					.append(ExceptionUtils
							.getFullStackTrace(e))
					.toString();
						
				logger.error(errMsg);
			}

			throw new VistaSocketException(errStr, e);
		}
	}

	/**
	 * Method transfer.
	 * <br>sends the request to M and returns the response
	 * <br>This method also audits time it takes to make both transfers
	 * @param request
	 * @return String
	 * @throws VistaSocketTimeOutException
	 * @throws VistaSocketException
	 */
	public String transfer(String request)
		throws VistaSocketTimeOutException, VistaSocketException {

		AuditTimer auditTimer = new AuditTimer(auditLogger);
		auditTimer.start();
		//send data
		this.sendData(request);

		//read response			
		String response = this.receiveData();

		auditTimer.stop();
//		Since we are using custom logger, there is no need to
//    polute log with additional text.		
//		auditTimer.log("VistaLink send/receive");
		auditTimer.log();

		return response;
	}

	/**
	 * Method close.
	 * <br>Sends a close request to M
	 * <br>Closes the socket
	 * <br>sets the socket to null
	 * 
	 * @throws VistaSocketException
	 */
	public void close() throws VistaSocketException {
		if (getSoc() != null) {
			this.sendData(CLOSE_SOCKET_REQUEST);
			try {
				getSoc().close();
			} catch (IOException e) {
				String errStr = "Can not close socket connection.";
				if(logger.isEnabledFor(Priority.ERROR)){

					String errMsg = (new StringBuffer())
						.append(errStr)
						.append("\n\t")
						.append(ExceptionUtils
								.getFullStackTrace(e))
						.toString();
						
					logger.error(errMsg);
				}

				throw new VistaSocketException(errStr, e);
			}
			setSoc(null);
		}
	}

	/**
	 * Method closeDontNotify.
	 * <br>Closes the socket
	 * <br>sets the socket to null
	 * <br>Does NOT send a close request to M
	 * @throws VistaSocketException
	 */
	public void closeDontNotify() throws VistaSocketException {
		if (getSoc() != null) {
			try {
				getSoc().close();
			} catch (IOException e) {
				String errStr = "Can not close socket connection.";
				if(logger.isEnabledFor(Priority.ERROR)){

					String errMsg = (new StringBuffer())
						.append(errStr)
						.append("\n\t")
						.append(ExceptionUtils
								.getFullStackTrace(e))
						.toString();
						
					logger.error(errMsg);
				}

				throw new VistaSocketException(errStr, e);
			}
			setSoc(null);
		}
	}

	/**
	 * Method getAddress.
	 * @return InetAddress
	 */
	public InetAddress getAddress() {
		return address;
	}

	/**
	 * Method getPort.
	 * @return int
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Method setTransferTimeOut.
	 * <br>Sets the time out for socket send/read operations
	 * @param timeout
	 * @throws VistaSocketException
	 */
	public void setTransferTimeOut(int timeout) throws VistaSocketException {
		try {
			getSoc().setSoTimeout(timeout);
		} catch (SocketException e) {
			String errStr = "Can not set socket timeout.";
			if(logger.isEnabledFor(Priority.ERROR)){

				String errMsg = (new StringBuffer())
					.append(errStr)
					.append("\n\t")
					.append(ExceptionUtils
							.getFullStackTrace(e))
					.toString();
						
				logger.error(errMsg);
			}
			throw new VistaSocketException(errStr, e);
		}
	}

	/**
	 * <br>Precautionary code to clean-up socket connections in J2SE environment.
	 * <br>In application server spi.ManagedConnection:destroy() will be called
	 * to clean-up connections, hence this method will not be performing socket
	 * clean-up.
	 * 
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		this.closeDontNotify();
		super.finalize();
	}

}
