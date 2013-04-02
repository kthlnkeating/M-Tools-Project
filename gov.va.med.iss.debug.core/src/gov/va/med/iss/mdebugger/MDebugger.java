package gov.va.med.iss.mdebugger;

import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.foundations.adapter.cci.VistaLinkConnection;

public class MDebuggerSteps {
	
	private static VistaLinkConnection myConnection;
	private static String rpcName = "";
	private static boolean updatePages = true;
	private static boolean repeatLastDebug = false;
	private static String lastCommand = "";

	static public String doDebug(String dbCommand) {
		lastCommand = dbCommand;
		myConnection = VistaConnection.getConnection();
		
		if (myConnection == null)
			return null; //TODO: prefer to throw exception

		try {
			RpcRequest vReq = RpcRequestFactory.getRpcRequest("",rpcName);
//				if (this.xmlRadioButton.isSelected()) {
				vReq.setUseProprietaryMessageFormat(false);
//				} else {
//					vReq.setUseProprietaryMessageFormat(true);
//				}
			vReq.getParams().setParam(1, "string", dbCommand);  // RD  RL  GD  GL  RS
			RpcResponse vResp = myConnection.executeRPC(vReq);
			System.out.println("RESPONSE VALUE:");
			System.out.println(vResp.getResults());
			if (updatePages) {
				repeatLastDebug = false;
				StepResults.ProcessInput(vResp.getResults()); //comment out, no longer pass resulting to this, but isntead returning them--jspivey
			}
			return vResp.getResults();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
//			if (rpcName.equals("XTDEBUG NEXT")) { //prior to this String == String always returns false --jspivey
//				if (e.getMessage() == "") {
//					repeatLastDebug = false;
//				}
//				else {
//					e.printStackTrace();
////						MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
////								"M Debugger",
////								"Error with RPC '" + rpcName + "': "+e.getMessage());
//				}
//			}
//			else {
//				e.printStackTrace();
////					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
////						"M Debugger",
////						"Error with RPC '" + rpcName + "': "+e.getMessage());
//			}
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
	public static String stepDebug(String dbCommand) {
		rpcName = "XTDEBUG NEXT";
		updatePages = true;
		return doDebug(dbCommand);
//		while (repeatLastDebug) { // commented out because repeatLastDebug has no chance to become true--jspivey
//			doDebug(dbCommand);
//		}
	}
	
	/**
	 * method to start a debugging session
	 * @param dbCommand - contains the line of code to be executed.
	 */
	public static String startDebug(String dbCommand) {
		rpcName = "XTDEBUG START";
		updatePages = true;
		//MDebuggerConsoleDisplay.clearConsole();
		StepResults.clearDoneFlag();
		return doDebug(dbCommand);
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
		
	public static String addWatchpoint(String watchPoint) {
		rpcName = "XTDEBUG ADD WATCH";
		updatePages = false;
		return doDebug(watchPoint);
	}
	
	public static String removeWatchpoint(String watchPoint) {
		rpcName = "XTDEBUG DELETE WATCH";
		updatePages = false;
		return doDebug(watchPoint);
	}
	
	public static String addBreakpoint(String breakPoint) {
		rpcName = "XTDEBUG ADD BREAKPOINT";
		updatePages = false;
		return doDebug(breakPoint);
	}
	
	public static String removeBreakpoint(String breakPoint) {
		rpcName = "XTDEBUG DELETE BREAKPOINT";
		updatePages = false;
		return doDebug(breakPoint);
	}
	
	
	public static void setTimer() {
		   new Thread(new Runnable() {
			      public void run() {
			            try { Thread.sleep(50); } catch (Exception e) { }
//			            MDebuggerConsoleDisplay.text.getDisplay().getDefault().asyncExec(new Runnable() {
//			               public void run() {
//			                  stepDebug(lastCommand);
//			               }
//			            });
			            stepDebug(lastCommand);
			         }
			   }).start();
	}

}
