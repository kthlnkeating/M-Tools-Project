package us.pwc.vista.eclipse.server.command;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.iss.connection.ConnectionData;
import gov.va.med.iss.connection.VLConnectionPlugin;
import gov.va.med.iss.connection.preferences.ServerData;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import us.pwc.vista.eclipse.core.helper.MessageConsoleHelper;
import us.pwc.vista.eclipse.core.helper.MessageDialogHelper;
import us.pwc.vista.eclipse.server.Messages;
import us.pwc.vista.eclipse.server.VistAServerPlugin;
import us.pwc.vista.eclipse.server.dialog.InputDialogHelper;

public class ReportGlobalDirectory extends AbstractHandler {
	public static void writeGlobalDirectory(VistaLinkConnection connection, String globalName) {
		try {
			RpcRequest vReq = RpcRequestFactory.getRpcRequest("", "XT ECLIPSE M EDITOR");
			vReq.setUseProprietaryMessageFormat(false);
			vReq.getParams().setParam(1, "string", "GD");  // RD  RL  GD  GL  RS
			vReq.getParams().setParam(2, "string", "notused");
			vReq.getParams().setParam(3, "string", globalName);
			RpcResponse vResp = connection.executeRPC(vReq);
			int value2 = vResp.getResults().indexOf("\n");
			String str = vResp.getResults().substring(value2+1);
			if (str.length() == 0) {
				str = "<no matches found>\n";
			}
			str = "Globals beginning with "+globalName+"\n\n"+ str;
			writeToConsole(globalName,str);
		} catch (Throwable t) {
			MessageDialogHelper.logAndShow(VistAServerPlugin.PLUGIN_ID, Messages.DLG_GLOBAL_DIR_UNEXPECTED, t);
		}
	}
	
	private static void writeToConsole(String globalName, String globalData) {
		char firstChar = globalName.charAt(0);
		char lastChar = globalName.charAt(globalName.length()-1);
		if (firstChar != '^') {
			globalName = "^"+globalName;
		}
		if (lastChar != '*') {
			globalName = globalName + "*";
		}
		MessageConsoleHelper.writeToConsole("GD "+globalName, globalData, true);
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ConnectionData connectionData = VLConnectionPlugin.getConnectionManager().selectConnectionData(false);
		if (connectionData == null) {
			return null;
		}
		
		ServerData data = connectionData.getServerData();
		String title = Messages.bind(Messages.DLG_GLOBAL_DIR_TITLE, data.getAddress(), data.getPort());
		String namespace = InputDialogHelper.getGlobalNamespace(title);
		if (namespace == null) {
			return null;
		}

		writeGlobalDirectory(connectionData.getConnection(), namespace);
		return null;
	}
}
