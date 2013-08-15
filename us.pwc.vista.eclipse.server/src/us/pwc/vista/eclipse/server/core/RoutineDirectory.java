/*
 * Created on Aug 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package us.pwc.vista.eclipse.server.core;

import us.pwc.vista.eclipse.server.dialog.MessageDialogHelper;
import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.iss.connection.actions.VistaConnection;

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
	public static String getRoutineList(String routineName) {
		String str = getRoutineNames(routineName);
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
	public static String getRoutineNames(String routineName) {
		str = "";
		if ( ! (routineName.compareTo("") == 0)) {
			VistaLinkConnection myConnection = VistaConnection.getConnection();
			if (! (myConnection == null)) {
				try {
//					int startLineCount = 0;
					RpcRequest vReq = RpcRequestFactory.getRpcRequest("", "XT ECLIPSE M EDITOR");
//					if (this.xmlRadioButton.isSelected()) {
						vReq.setUseProprietaryMessageFormat(false);
//					} else {
//						vReq.setUseProprietaryMessageFormat(true);
//					}
					vReq.getParams().setParam(1, "string", "RD");  // RD  RL  GD  GL  RS
					vReq.getParams().setParam(2, "string", "notused");
					vReq.getParams().setParam(3, "string", routineName);
					RpcResponse vResp = myConnection.executeRPC(vReq);
					int value2 = vResp.getResults().indexOf("\n");
/*					
					int value1 = vResp.getResults().indexOf("1");
					int value = vResp.getResults().length();
					int location = 0;
*/
					str = vResp.getResults().substring(value2+1);
					if (str.length() == 0)
						str = "<no matches found>\n";
					str = "Routines beginning with "+routineName+"\n\n"+ str;
				} catch (Throwable t) {
					String message = "Error encountered while executing RPC " + t.getMessage();
					MessageDialogHelper.logAndShow(message, t);
					str = "";
				}
			}
		}
		return str;
	}
}
