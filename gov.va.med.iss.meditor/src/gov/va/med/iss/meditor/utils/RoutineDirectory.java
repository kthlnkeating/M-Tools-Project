/*
 * Created on Aug 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.utils;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.iss.connection.actions.VistaConnection;

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IStorageEditorInput;
//import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
//import org.eclipse.ui.IWorkbenchWindow;
//import org.eclipse.ui.PlatformUI;

/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RoutineDirectory {

	/**
	 * getRoutineDirectory generates a list of the routine
	 * names which begin with the input argument routineName.
	 * This list is displayed in a window.
	 * 
	 * @param routineName
	 */
	public static void getRoutineDirectory(String routineName) {
		String str = getRoutineNames(routineName);
		setupRoutineDirWindow(routineName,str);
	}
	
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
	private static String getRoutineNames(String routineName) {
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
				} catch (Exception e) {
					MessageDialog.openInformation(
							MEditorUtilities.getIWorkbenchWindow().getShell(),
							"Meditor Plug-in",
							"Error encountered while executing RPC "+e.getMessage());
					str = "";
				}
			}
		}
		return str;
	}
	
	
	private static void setupRoutineDirWindow(String routineName,String routineData) {
		char bChar = routineName.charAt(routineName.length()-1);
		if (bChar != '*') {
			routineName = routineName + "*";
		}
		IStorage storage = new StringStorage("RD "+routineName, routineData);
		IStorageEditorInput input = new StringInput(storage);
		IWorkbenchPage page = MEditorUtilities.getIWorkbenchPage();
		try {
			if (page != null)
				page.openEditor(input, "org.eclipse.ui.DefaultTextEditor");
		} catch (Exception e) {
			
		}
	}
}
