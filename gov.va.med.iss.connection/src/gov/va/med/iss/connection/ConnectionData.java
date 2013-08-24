package gov.va.med.iss.connection;

import us.pwc.vista.eclipse.core.ServerData;
import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.security.vistalink.EclipseConnection;

public class ConnectionData {
	private ServerData serverData;
	private VistaLinkConnection connection;
	private EclipseConnection eclipseConnection;
			
	public ConnectionData(ServerData serverData, VistaLinkConnection connection, EclipseConnection eclipseConnection) {
		super();
		this.serverData = serverData;
		this.connection = connection;
		this.eclipseConnection = eclipseConnection;
	}

	public ServerData getServerData() {
		return this.serverData;
	}
	
	public VistaLinkConnection getConnection() {
		return connection;
	}

	public EclipseConnection getEclipseConnection() {
		return eclipseConnection;
	}

	public String getServerName() {
		return this.serverData.getName();
	}
}
