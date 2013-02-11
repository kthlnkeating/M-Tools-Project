package gov.va.med.foundations.adapter.cci;

import java.net.InetAddress;

/**
 * Represents M VistA connection information, like address and port.
 * <br><br>
 * An instance of this class is used internally by VLJ to establish M VistA
 * connections. However, for developer debugging, a reference to an object
 * of this class is available for read-only purpose via the getServerInfo()
 * method of VistaRequest.
 *
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaLinkServerInfo {
	private InetAddress address = null;
	private int port;

	/**
	 * Method VistaLinkServerInfo.
	 * @param address
	 * @param port
	 */
	protected VistaLinkServerInfo(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}

	/**
	 * Gets the current address associated with an instance
	 * 
	 * @return InetAddress
	 */
	public InetAddress getAddress() {
		return address;
	}

	/**
	 * Set the address associated with an instance.
	 * @param address
	 */
	protected void setAddress(InetAddress address) {
		this.address = address;
	}

	/**
	 * Gets the current port associated with an instance
	 * 
	 * @return int
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Set the port associated with an instance.
	 * 
	 * @param port Port number for socket
	 */
	protected void setPort(int port) {
		this.port = port;
	}

}