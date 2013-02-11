package gov.va.med.iss.mdebugger.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.mdebugger.views.MDebuggerConsoleDisplay;
import gov.va.med.iss.mdebugger.views.MDebuggerReadCommand;

public class MDebuggerSteps {
	
	private static VistaLinkConnection myConnection;
	private static String rpcName = "";
	private static boolean updatePages = true;
	private static boolean repeatLastDebug = false;
	private static String lastCommand = "";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	static public void doDebug(String dbCommand) {
		lastCommand = dbCommand;
		myConnection = VistaConnection.getConnection();
		String str = "";
		if (! (myConnection == null)) {
			try {
				RpcRequest vReq = RpcRequestFactory.getRpcRequest("",rpcName);
//				if (this.xmlRadioButton.isSelected()) {
					vReq.setUseProprietaryMessageFormat(false);
//				} else {
//					vReq.setUseProprietaryMessageFormat(true);
//				}
				vReq.getParams().setParam(1, "string", dbCommand);  // RD  RL  GD  GL  RS
				RpcResponse vResp = myConnection.executeRPC(vReq);
				str = vResp.getResults();
				if (updatePages) {
					repeatLastDebug = false;
					StepResults.ProcessInput(str);
				}
			} catch (Exception e) {
				if (rpcName == "XTDEBUG NEXT") {
					if (e.getMessage() == "") {
						repeatLastDebug = false;
					}
					else {
						MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
								"M Debugger",
								"Error with RPC '" + rpcName + "': "+e.getMessage());
					}
				}
				else {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"M Debugger",
						"Error with RPC '" + rpcName + "': "+e.getMessage());
				}
			}
		}
	}
	
	/**
	 * method used to indicate to the server to process more
	 * of the code.  The range of code to be covered is
	 * indicated by the value of dbCommand.
	 * @param dbCommand - contains a text value indicating
	 * the amount of code to be processed:
	 *    "STEP" the next command should be processed 
	 *    "STEPLINE" commands on the current line should be 
	 *             processed
	 *    "RUN" commands are executed until a specified 
	 *             reason to pause (e.g., breakpoint, watched
	 *             value change, etc.) is reached.
	 *    "STEPOUT" commands are processed until the processing
	 *             exits the current stack level for an earlier
	 *             one
	 *    "STEPIN" the processing is traced into the next higher
	 *             stack level
	 */
	public static void stepDebug(String dbCommand) {
		rpcName = "XTDEBUG NEXT";
		updatePages = true;
		doDebug(dbCommand);
		while (repeatLastDebug) {
			doDebug(dbCommand);
		}
	}
	
	/**
	 * method to start a debugging session
	 * @param dbCommand - contains the line of code to be executed.
	 */
	public static void startDebug(String dbCommand,boolean clearInits, ArrayList initList) {
		rpcName = "XTDEBUG START";
		updatePages = true;
		MDebuggerConsoleDisplay.clearConsole();
		StepResults.clearDoneFlag();
		doDebug(dbCommand);
	}
	
	public static void setRepeatLastDebug() {
		repeatLastDebug = true;
	}
	
	public static void clearRepeatLastDebug() {
		repeatLastDebug = false;
	}
	
	public static void doLastCommand() {
		stepDebug(lastCommand);
	}
	
	public static String getLastCommand() {
		return lastCommand;
	}
	
	
	
	public static boolean getRepeatLastDebug() {
		return repeatLastDebug;
	}
		
	public static void addWatchpoint(String watchPoint) {
		rpcName = "XTDEBUG ADD WATCH";
		updatePages = false;
		doDebug(watchPoint);
	}
	
	public static void removeWatchpoint(String watchPoint) {
		rpcName = "XTDEBUG DELETE WATCH";
		updatePages = false;
		doDebug(watchPoint);
	}
	
	public static void addBreakpoint(String breakPoint) {
		rpcName = "XTDEBUG ADD BREAKPOINT";
		updatePages = false;
		doDebug(breakPoint);
	}
	
	public static void removeBreakpoint(String breakPoint) {
		rpcName = "XTDEBUG DELETE BREAKPOINT";
		updatePages = false;
		doDebug(breakPoint);
	}
	
	static Timer timer;
	
	
	public static void setTimer() {
		   new Thread(new Runnable() {
			      public void run() {
			            try { Thread.sleep(50); } catch (Exception e) { }
			            MDebuggerConsoleDisplay.text.getDisplay().getDefault().asyncExec(new Runnable() {
			               public void run() {
			                  stepDebug(lastCommand);
			               }
			            });
			         }
			   }).start();
	}

}
