package gov.va.med.iss.connection.actions;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.security.vistalink.EclipseConnection;
import gov.va.med.foundations.security.vistalink.VistaKernelPrincipalImpl;
import gov.va.med.iss.connection.preferences.ServerConnectionData;

/***
 * This class holds data related to servers which may not be the primary server.
 * Data is collected as each server connection is made.  Server connections are stored
 * in an ArrayList and each entry in the ArrayList is an instance of this class providing 
 * information about the connection.
 * 
 * @author vhaisfiveyj
 *
 */
public class ConnectionData {
	private String serverAddress = "";
	private String serverPort = "";
	private String serverName = "";
	private VistaLinkConnection connection = null;
	private VistaKernelPrincipalImpl principal = null;
	private EclipseConnection eclipseConnection = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public VistaLinkConnection getConnection() {
		return connection;
	}
	
	public void setConnection(VistaLinkConnection inputConnection) {
		connection = inputConnection;
	}
	
	public VistaKernelPrincipalImpl getPrincipal() {
		return principal;
	}
	
	public EclipseConnection getEclipseConnection() {
		return eclipseConnection;
	}
	
	public void setPrincipal(VistaKernelPrincipalImpl inputPrincipal) {
		principal = inputPrincipal;
	}
	
	public String getServerAddress() {
		return serverAddress;
	}
	
	public void setServerAddress(String inputAddress) {
		serverAddress = inputAddress;
	}
	
	public String getServerPort() {
		return serverPort;
	}
	
	public void setServerPort(String inputPort) {
		serverPort = inputPort;
	}
	
	public void setServerName(String name) {
		serverName = name;
	}
	
	public String getServerName() {
		return serverName;
	}
	
	public void setEclipseConnection(EclipseConnection connection) {
		eclipseConnection = connection;
	}
	
	public boolean matches(ServerConnectionData data) {		
		if (this.getServerPort().equalsIgnoreCase(data.port) && this.getServerAddress().equalsIgnoreCase(data.serverAddress)) {
			return true;
		} else {
			return false;
		}
	}

}
