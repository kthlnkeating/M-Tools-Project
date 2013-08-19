package gov.va.med.iss.connection;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.security.vistalink.EclipseConnection;
import gov.va.med.foundations.security.vistalink.VistaKernelPrincipalImpl;
import gov.va.med.foundations.utilities.FoundationsException;
import gov.va.med.iss.connection.actions.ConnectionData;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.connection.preferences.ServerData;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

public class ConnectionManager {
	private void checkProductionConnection(VistaLinkConnection connection, String serverAddress, String portNumber) {
		try {
			if (VistaConnection.checkForProductionAccount(connection)) {
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
	
	public ConnectionData getConnectionData(ServerData serverData) {
		try {
			EclipseConnection eclipseConnection = new EclipseConnection();
			VistaKernelPrincipalImpl principal = eclipseConnection.getConnection(serverData.serverAddress, serverData.port);
			if (principal != null) {
				VistaLinkConnection connection = principal.getAuthenticatedConnection();
				ConnectionData result = new ConnectionData(serverData, connection, eclipseConnection);
				checkProductionConnection(result.getConnection(), serverData.serverAddress, serverData.port);
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
		
	}
}
