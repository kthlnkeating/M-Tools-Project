package gov.va.med.foundations.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.Socket;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Represents a socket that can be used to communicate with IP end points
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class SocketManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The logger used for this class
	 */
	private static final Logger logger =
		Logger.getLogger(SocketManager.class);

	/**
	 * The socket
	 */
	private Socket soc;

	/**
	 * The buffersize used for recieve operations, the socket will read
	 * <br>this many chars at a time
	 */
	private int bufferSize = 512;

	/**
	 * Represents an End of Transmission
	 */
	private static final char EOT = '\u0004';

	@SuppressWarnings("unused")
	/**
	 * Represents UTF8 encoding
	 */
	private static final String ENCODE_OUTF8 = "UTF8";

	/**
	 * Represents ASCII encoding
	 */
	private static final String ENCODE_ASCII = "US-ASCII";

	/**
	 * Default constructor
	 */
	public SocketManager() {
	}

	/**
	 * Method SocketManager
	 * <br> Constructs this SocketManager with the specified socket
	 * @param soc
	 */
	public SocketManager(Socket soc) {
		this.soc = soc;
	}

	/**
	 * Method getBufferSize.
	 * <br>Returns the buffer size 
	 * @return int
	 */
	public int getBufferSize() {
		return bufferSize;
	}

	/**
	 * Method setBufferSize.
	 * <br> Sets the buffer size
	 * @param value
	 */
	public void setBufferSize(int value) {
		bufferSize = value;
	}

	/**
	 * Method getSoc.
	 * <br> gets the socket associated with this SocketManager
	 * @return Socket
	 */
	public Socket getSoc() {
		return soc;
	}

	/**
	 * Method setSoc.
	 * <br> Sets the socket associated with this SocketManager
	 * @param value
	 */
	public void setSoc(Socket value) {
		this.soc = value;
	}

	/**
	 * Method SendRequest.
	 * <br> Writes the xmlRequest param to the open socket
	 * @param xmlRequest
	 * @throws VistaSocketException
	 */
	public void sendData(String xmlRequest) throws VistaSocketException {
		BufferedWriter out;

//		if(logger.isDebugEnabled()){
//
//			logger.debug((new StringBuffer())
//				.append(("sending data ->"))
//				.append(xmlRequest).toString());
//
//		}

		try {
			out =
				new BufferedWriter(
					new OutputStreamWriter(soc.getOutputStream(), ENCODE_ASCII),
					soc.getSendBufferSize());
			out.write(xmlRequest + EOT);
			out.flush();
			//			} catch (UnsupportedEncodingException e) {
			//			} catch (SocketException e) {
			//			} catch (IOException e) {
		} catch (Exception e) {
			throw new VistaSocketException(
				"Error occurred writing to socket.",
				e);
		}
	}

	/**
	 * Method ReceiveResponse.
	 * <br>Reads the socket for a response and writes to String
	 * @return String
	 * @throws VistaSocketException
	 * @throws VistaSocketTimeOutException 
	 */
	public String receiveData()
		throws VistaSocketException, VistaSocketTimeOutException {
		StringBuffer sb = new StringBuffer();
		char[] buffer = new char[bufferSize];
		boolean moreData = true;
		int dataRead = 0;
		BufferedReader in;

		

		try {
			in =
				new BufferedReader(
					new InputStreamReader(soc.getInputStream(), ENCODE_ASCII),
					soc.getReceiveBufferSize());

			while (moreData) {
				dataRead = in.read(buffer);

				if(logger.isDebugEnabled()){

					logger.debug((new StringBuffer())
						.append("read data->")
						.append(dataRead)
						.toString());

				}

				if (dataRead > 0) {
					if (buffer[dataRead - 1] == EOT) {

						if(logger.isDebugEnabled()){

							logger.debug((new StringBuffer())
								.append("read data->Got EOT->")
								.append(dataRead)
								.toString());

						}
						moreData = false;
						sb.append(buffer, 0, dataRead - 1);
					} else {
						sb.append(buffer, 0, dataRead);
					}
				}else if (dataRead == -1) {
					moreData = false;
					throw new VistaSocketException(
							"End of stream encountered unexpectedly.");
				}else{
					moreData = false;
					throw new VistaSocketException(
							"BufferedReader.read() returned " + dataRead);
				}

			}
			//			} catch (UnsupportedEncodingException e) {
			//			} catch (SocketException e) {
			//			} catch (IOException e) {
			// Can not catch a specific exception since jdk 1.3 and 1.4 throws different exceptions
		} catch (Exception e) {

			if(logger.isEnabledFor(Level.ERROR)){

				logger.error((new StringBuffer())
					.append("recieving data exception->")
					.append(buffer, 0, dataRead).toString(), e);

			}

			String SOCKET_TIMEOUT_EXCEPTION_CLASS_1_3_1 =
				"java.io.InterruptedIOException";
			String SOCKET_TIMEOUT_EXCEPTION_CLASS_1_4_1 =
				"java.net.SocketTimeoutException";
			if (e
				.getClass()
				.getName()
				.equals(SOCKET_TIMEOUT_EXCEPTION_CLASS_1_4_1)) {
				throw new VistaSocketTimeOutException("Socket Timeout occured.", e);
			} else if (
				e.getClass().getName().equals(
					SOCKET_TIMEOUT_EXCEPTION_CLASS_1_3_1)) {
				throw new VistaSocketTimeOutException("Socket Timeout occured.", e);
			} else {
				throw new VistaSocketException(
					"Error occurred reading from socket. ",
					e);
			}
		}

//		if(logger.isDebugEnabled()){
//
//			logger.debug((new StringBuffer())
//				.append(("recieving data ->"))
//				.append(sb.toString()).toString());
//
//		}

		return sb.toString();
	}
}
