/*
 * Created on Aug 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package us.pwc.vista.eclipse.server.core;

import us.pwc.vista.eclipse.core.helper.MessageDialogHelper;
import us.pwc.vista.eclipse.server.VistAServerPlugin;
import gov.va.med.iss.connection.ConnectionData;

/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RoutineDirectory {
	/**
	 * 
	 * @param routineName - the routine name for which a list of 
	 * routines which begin with the specified string are returned.
	 * 
	 * @return string of routine names which begin with routineName. 
	 */
	public static String getRoutineList(ConnectionData connectionData, String routineName) {
		String str = getRoutineNames(connectionData, routineName);
		// comes back with a header followed by \n\n
		int loc = str.indexOf("\n\n");
		// remove header and \n\n 
		return str.substring(loc+2);
	}
	
	static String str;
	
	/**
	 * 
	 * @param routineName
	 * 
	 * @return
	 */
	public static String getRoutineNames(ConnectionData connectionData, String routineName) {
		str = "";
		if ( ! (routineName.compareTo("") == 0)) {
			try {
				String result = connectionData.rpcXML("XT ECLIPSE M EDITOR", "RD", "notused", routineName);				
				int value2 = result.indexOf("\n");
				str = result.substring(value2+1);
				if (str.length() == 0)
					str = "<no matches found>\n";
				str = "Routines beginning with "+routineName+"\n\n"+ str;
			} catch (Throwable t) {
				String message = "Error encountered while executing RPC " + t.getMessage();
				MessageDialogHelper.logAndShow(VistAServerPlugin.PLUGIN_ID, message, t);
				str = "";
			}
		}
		return str;
	}
}
