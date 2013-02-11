/*
 * Created on Aug 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.utils;

import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
/*
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.eclipse.core.resources.IResource;
*/
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.core.resources.IStorage;

/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GlobalDirectory {

	public static void getGlobalDirectory(String globalName) {
		if ( ! (globalName.compareTo("") == 0)) {
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
					vReq.getParams().setParam(1, "string", "GD");  // RD  RL  GD  GL  RS
					vReq.getParams().setParam(2, "string", "notused");
					vReq.getParams().setParam(3, "string", globalName);
					RpcResponse vResp = myConnection.executeRPC(vReq);
					int value2 = vResp.getResults().indexOf("\n");
/*
					int value1 = vResp.getResults().indexOf("1");
					int value = vResp.getResults().length();
					int location = 0;
*/					
					String str = vResp.getResults().substring(value2+1);
					if (str.length() == 0)
						str = "<no matches found>\n";
					str = "Globals beginning with "+globalName+"\n\n"+ str;
/*
					String result = "";
					while (str.length() > 0) {
						value2 = str.indexOf("\n");
						result = result + str.substring(0,value2)+" = \"";
						str = str.substring(value2+1);
						value2 = str.indexOf("\n");
						if (value2 > 0) {
							result = result + str.substring(0,value2)+"\"";
						} else {
							result = result + "\"";
						}
						str = str.substring(value2+1);
					}
*/
					setupGlobalDirWindow(globalName,str);

				} catch (Exception e) {
					MessageDialog.openInformation(
							getWindow().getShell(),
							"Meditor Plug-in",
							"Error encountered while executing RPC "+e.getMessage());
				}
			}
			
		}
	}
	
	private static void setupGlobalDirWindow(String globalName, String globalData) {
//		IWorkbenchWindow win = Utilities.getIWorkbenchWindow();
		char firstChar = globalName.charAt(0);
		char lastChar = globalName.charAt(globalName.length()-1);
		if (firstChar != '^') {
			globalName = "^"+globalName;
		}
		if (lastChar != '*') {
			globalName = globalName + "*";
		}
		IStorage storage = new StringStorage("GD "+globalName, globalData);
		IStorageEditorInput input = new StringInput(storage);
		IWorkbenchPage page = MEditorUtilities.getIWorkbenchPage()
;		try {
			if (page != null)
				page.openEditor(input, "org.eclipse.ui.DefaultTextEditor");
		} catch (Exception e) {
			
		}
	}
		
	static IWorkbenchWindow getWindow() {
		IWorkbench wb = PlatformUI.getWorkbench();
		return wb.getActiveWorkbenchWindow();
	}

 
}
