package gov.va.med.iss.connection;

import java.util.ArrayList;
import java.util.List;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.foundations.security.vistalink.EclipseConnection;
import gov.va.med.foundations.security.vistalink.VistaKernelPrincipalImpl;
import gov.va.med.foundations.utilities.FoundationsException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.statushandlers.StatusManager;

import us.pwc.vista.eclipse.core.ServerData;
import us.pwc.vista.eclipse.core.VistACorePrefs;
import us.pwc.vista.eclipse.core.helper.MessageDialogHelper;

public class ConnectionManager {
	private List<ConnectionData> connections = new ArrayList<ConnectionData>();
	
	private void checkProductionConnection(VistaLinkConnection connection, String serverAddress, String portNumber) {
		try {
			if (this.checkForProductionAccount(connection)) {
				MessageDialog.openWarning(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"Meditor Plug-in Routine Load - PRODUCTION account",
						"** WARNING **\n\n"+
								serverAddress + " is a PRODUCTION account.\n\n" +
						"You should use extreme caution in editing and saving a " +
						"routine back into a PRODUCTION account.");
			}
		} catch (Exception e) {
			MessageDialog.openError(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Meditor Plug-in Routine Load Error",
					"An error occured while checking for whether the current\n" +
					"server is a\n"+
					"production server or not.");
		}		
	}
	
	private ConnectionData createConnectionData(ServerData serverData) {
		try {
			EclipseConnection eclipseConnection = new EclipseConnection();
			VistaKernelPrincipalImpl principal = eclipseConnection.getConnection(serverData.getAddress(), serverData.getPort());
			if (principal != null) {
				VistaLinkConnection connection = principal.getAuthenticatedConnection();
				ConnectionData result = new ConnectionData(serverData, connection, eclipseConnection);
				checkProductionConnection(result.getConnection(), serverData.getAddress(), serverData.getPort());
				this.connections.add(result);
				return result;
			}
			return null;
		} catch (Throwable t) {
			IStatus status = new Status(IStatus.ERROR, VLConnectionPlugin.PLUGIN_ID, "VistA connection error.", t);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
			return null;
		}
	}

	public boolean checkConnection(VistaLinkConnection connection, ServerData serverData) {
		if (connection == null)
			return false;
		RpcRequest vReq = null;
		boolean result = true;
		try {
			vReq = RpcRequestFactory.getRpcRequest("", "XWB GET BROKER INFO");
		} catch (FoundationsException fe) {
			result = false;
		}
		if (result) {
			vReq.setUseProprietaryMessageFormat(false);
			try {
				connection.executeRPC(vReq);
			} catch (Exception e) {
				result = false;
			}
		}
		if (! result) {
			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
			MessageDialog.openInformation(
					window.getShell(),
					"VistA Connection Error",
					"The connection to the server has been lost, sign in again");
 		}
		return result;
	}

	public void removeAllConnections() {
		for (ConnectionData cd : this.connections) {
			cd.getEclipseConnection().logout();
		}
		this.connections.clear();
	}
	
	public void removeConnection() {
		List<ServerData> serverDataList = new ArrayList<ServerData>();
		for (ConnectionData cd : this.connections) {
			serverDataList.add(cd.getServerData());
		}
		if (serverDataList.size() == 0) {
			MessageDialogHelper.showWarning("Connection Manager", "No existing connection is found.");
			return;			
		}
		ServerData serverData = this.selectConnectionData(serverDataList, false, "Select an existing connection:");
		if (serverData != null) {
			ConnectionData connectionData = this.findConnection(serverData.getName());
			connectionData.getEclipseConnection().logout();
			this.connections.remove(connectionData);
		}
	}

	public ConnectionData findConnection(String serverName) {
		for (ConnectionData cd : this.connections) {
			String existingServerName = cd.getServerName();
			if (existingServerName.equals(serverName)) {
				return cd;
			}
		}
		return null;
	}
	
	public ConnectionData getConnectionData(IProject project) {
		try {
			String serverName = VistACorePrefs.getServerName(project);
			if (serverName.isEmpty()) {
				String message = "Server name is not specified for project " + project.getName() + ".";
				message += "\nUse VistA project properties to specify.";
				MessageDialogHelper.showError("Connection Manager", message);
				return null;
			}	
			return this.getConnectionData(serverName);
		} catch (CoreException coreException) {
			StatusManager.getManager().handle(coreException, VLConnectionPlugin.PLUGIN_ID);
			return null;
		}
	}
	
	public ConnectionData getConnectionData(String serverName) {
		ConnectionData cd = this.findConnection(serverName);
		if (cd == null) {
			return this.createConnectionData(serverName);
		} else {
			VistaLinkConnection connection = cd.getConnection();
			ServerData serverData = cd.getServerData();
			if (this.checkConnection(connection, serverData)) {
				return cd;
			} else {
				cd.getEclipseConnection().logout();					
				this.connections.remove(cd);
				return this.createConnectionData(serverData);
			}
		}		
	}
	
	private ServerData selectConnectionData(List<ServerData> serverDataList, boolean forceSelectionForOne, String message) {
		if (serverDataList != null) {
			if ((! forceSelectionForOne) && (serverDataList.size() == 1)) {
				return serverDataList.get(0);
			} else {						
				LabelProvider lp = ServerData.getLabelProvider();
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				ElementListSelectionDialog dlg = new ElementListSelectionDialog(shell, lp);
				dlg.setMultipleSelection(false);
				dlg.setTitle("Connection Manager");
				dlg.setMessage(message);
				dlg.setElements(serverDataList.toArray());
				if (ListSelectionDialog.OK == dlg.open()) {
					dlg.getResult();
					return (ServerData) dlg.getFirstResult();
				}
			}
		}		
		return null;
		
	}
 	
	public ConnectionData selectConnectionData(boolean forceSelectionForOne) {
		List<ServerData> serverDataList = this.getServerDataList();
		ServerData serverData = this.selectConnectionData(serverDataList, forceSelectionForOne, "Select a server for connection:");
		if (serverData == null) {
			return null;
		} else {
			ConnectionData cd = this.getConnectionData(serverData.getName());
			return cd;			
		}
	}
	
	private ConnectionData createConnectionData(String serverName) {
		ServerData serverData = this.getServerData(serverName);
		if (serverData == null) {
			return null;
		} else {
			ConnectionData cd = this.createConnectionData(serverData);
			return cd;
		}
	}
	
	private List<ServerData> getServerDataList() {
		List<ServerData> result = VistACorePrefs.getServers();
		if ((result == null) || (result.size() == 0)) {
			String message = "No server is specified. Use VistA preferences to add.";			
			MessageDialogHelper.showError("Connection Manager", message);			
			return null;
		}
		return result;
	}
	
	private ServerData getServerData(String serverName) {
		List<ServerData> serverDataList = this.getServerDataList();
		if (serverDataList == null) {
			return null;
		}
		for (ServerData serverData : serverDataList) {
			if (serverData.getName().equals(serverName)) {
				return serverData;
			}		
		}

		String message = "No server named " + serverName + " is found. Use VistA preferences to add.";			
		MessageDialogHelper.showError("Connection Manager", message);
		return null;		
	}
	
	private boolean checkForProductionAccount(VistaLinkConnection myConnection) throws Exception {
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
}
