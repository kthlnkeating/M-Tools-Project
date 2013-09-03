package us.pwc.vista.eclipse.server.command;

import gov.va.med.iss.connection.VistAConnection;
import gov.va.med.iss.connection.VLConnectionPlugin;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import us.pwc.vista.eclipse.core.ServerData;
import us.pwc.vista.eclipse.core.helper.MessageConsoleHelper;
import us.pwc.vista.eclipse.core.helper.MessageDialogHelper;
import us.pwc.vista.eclipse.server.Messages;
import us.pwc.vista.eclipse.server.VistAServerPlugin;
import us.pwc.vista.eclipse.server.dialog.InputDialogHelper;

public class ReportGlobalDirectory extends AbstractHandler {
	private static void writeGlobalDirectory(VistAConnection vistaConnection, String globalName) {
		try {
			String rpcResult = vistaConnection.rpcXML("XT ECLIPSE M EDITOR", "GD", "notused", globalName);
			int value2 = rpcResult.indexOf("\n");
			String str = rpcResult.substring(value2+1);
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
		VistAConnection vistaConnection = VLConnectionPlugin.getConnectionManager().selectConnection(false);
		if (vistaConnection == null) {
			return null;
		}
		
		ServerData data = vistaConnection.getServerData();
		String title = Messages.bind(Messages.DLG_GLOBAL_DIR_TITLE, data.getAddress(), data.getPort());
		String namespace = InputDialogHelper.getGlobalNamespace(title);
		if (namespace == null) {
			return null;
		}

		writeGlobalDirectory(vistaConnection, namespace);
		return null;
	}
}
