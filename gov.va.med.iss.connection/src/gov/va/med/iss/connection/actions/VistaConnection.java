package gov.va.med.iss.connection.actions;

import java.util.List;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.iss.connection.ConnectionManager;
import gov.va.med.iss.connection.preferences.ConnectionPreferencePage;
import gov.va.med.iss.connection.preferences.ServerData;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class VistaConnection {
	private static ConnectionData connectionData;
	
	public static void run(IWorkbenchWindow window) {
		VistaConnection.run();
	}
	
	/***
	 * Used without arguments to get the primary connection
	 * 
	 * @return an instance of the VistaLinkConnection for the 
	 * connection to the primary server
	 */
	public static VistaLinkConnection getConnection() {
		ConnectionManager cm = new ConnectionManager();
		if (connectionData != null) {
			ServerData serverData = connectionData.getServerData();
			if (cm.checkConnection(connectionData.getConnection(), serverData)) {
				return connectionData.getConnection();
			}
			connectionData.getEclipseConnection().logout();
			connectionData = cm.getConnectionData(serverData);
			if (connectionData == null) {
				return null;
			} else {
				return connectionData.getConnection();
			}
		}
		ServerData serverData = getDefaultConnectionData();
		if ((serverData == null) || (serverData.serverAddress.isEmpty())) {
			Shell parentShell = Display.getDefault().getActiveShell();
			MessageDialog.openInformation(
					parentShell,
					"Server, Port Specification", 
			"Open Windows | Preferences | Vista | Connection and enter Server and Port data");
			return null;
		}
		connectionData = cm.getConnectionData(serverData);
		if (connectionData == null) {
			return null;
		} else {
			return connectionData.getConnection();
		}
	}
	
	public static void disconnect() {
		if (connectionData != null) {
			connectionData.getEclipseConnection().logout();
			connectionData = null;
		}		
	}
	
	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public static void run() {
		getConnection();
	}
	
	static public ServerData getDefaultConnectionData() {
		List<String> servers = ConnectionPreferencePage.getServerList();
		if ((servers == null) || (servers.size() == 0)) {
			return null;
		}
		String serverString = servers.get(0);
		String[] serverFields = serverString.split(";");
		if (serverFields.length < 4) {
			return null;
		}		
		String serverName = serverFields[0];
		String serverAddress = serverFields[1];
		String port = serverFields[2];
		String serverProject = serverFields[3];

		return new ServerData(serverAddress, serverName, port, serverProject);
	}
	
 	/*
	 * checkForProductionAccount - determines whether the current connection is to a PRODUCTION account or not
	 * 
	 * argument myConnection - VistaLinkConnection - the current connection
	 * 
	 * returns boolean indicating whether the current connection is indicated as a PRODUCTION system (true) or not (false).
	 */
	static public boolean checkForProductionAccount(VistaLinkConnection myConnection) throws Exception {
		RpcRequest vReq1 = RpcRequestFactory.getRpcRequest("","XT ECLIPSE M EDITOR");
		vReq1.setUseProprietaryMessageFormat(true);
		vReq1.getParams().setParam(1, "string", "PROD");  // RD  RL  GD  GL  RS PROD
		RpcResponse vResp1 = myConnection.executeRPC(vReq1);
		boolean response = false;
		if (vResp1.getResults().indexOf("1") == 0) {
			response = true;
		}
		return response;
	}
	
	static public ServerData getServerData() {
		if (connectionData == null) {
			return null;
		} else {
			return connectionData.getServerData();
		}
	}
}
