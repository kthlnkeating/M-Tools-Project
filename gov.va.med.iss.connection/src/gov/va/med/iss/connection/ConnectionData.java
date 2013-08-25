package gov.va.med.iss.connection;

import us.pwc.vista.eclipse.core.ServerData;
import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcRequestParams;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.foundations.security.vistalink.EclipseConnection;
import gov.va.med.foundations.utilities.FoundationsException;

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
	
	public String rpcXML(String rpcName, String... paramNames) throws FoundationsException {
		RpcRequest vReq = RpcRequestFactory.getRpcRequest("", rpcName);
		vReq.setUseProprietaryMessageFormat(false);
		RpcRequestParams params = vReq.getParams();
		for (int i=1; i<=paramNames.length; ++i) {
			params.setParam(i, "string", paramNames[i-1]);
		}
		RpcResponse vResp = this.connection.executeRPC(vReq);
		String result = vResp.getResults();
		return result;
	}
}
