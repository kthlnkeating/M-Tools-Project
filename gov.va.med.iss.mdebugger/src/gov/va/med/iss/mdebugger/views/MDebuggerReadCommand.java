package gov.va.med.iss.mdebugger.views;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.mdebugger.util.MDebuggerReadWait;
import gov.va.med.iss.mdebugger.util.MDebuggerSteps;
import gov.va.med.iss.mdebugger.util.StepResults;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

/**
 * This class handles the Read for a read command while
 * debugging.
 * 
 * @author vhaisfiveyj
 *
 */
public class MDebuggerReadCommand {
	
	private static String outString = "";
	private static char lastChar = '\0';
	private static String str1 = "";
	private static boolean isDone = false;
	// text input from console but not yet "READ"
	public static String newText = "";
	// line of text currently being "READ"
	public static String currentText = "";
	// text containing previous lines displayed
	public static String strval = "";
	public static boolean isReadCompleted = false;
	public static boolean isTimerFired = false;
	// token to prevent collision 
	public static int threadToken = 0;
	private static long dTimeMilliseconds = 300000;
	private static boolean isStarVal = false;
	private static int maxCharsToRead = 0;
	private static MDebuggerReadWait timer;
	private static boolean isActive = true;
	public static String xxxx = "";
	public static boolean isReadInProcess = false;
	private static boolean repeatLastDebug = false;
	private static boolean crSeen = false;
	
	public static void doRead(int dTime, boolean isStar, int numCharsToRead, boolean isTypeAhead) {
		MDebuggerConsoleDisplay.setToFocus();
		isReadInProcess = true;
		lastChar = '\0';
		crSeen = false;
		if (timer != null)
			timer.cancel();
		repeatLastDebug = MDebuggerSteps.getRepeatLastDebug();
		MDebuggerSteps.clearRepeatLastDebug();
		if ( !(isTypeAhead)) {
			clearTypeAhead();
		}
		isReadCompleted = false;
		isTimerFired = false;
		isDone = false;
		isActive = true;
		if (dTime == 0) {
			dTime = 300; // seconds, 5 minutes
		}
		dTimeMilliseconds = dTime * 1000;
		isStarVal = isStar;
		maxCharsToRead = numCharsToRead;
		setTimer();
		checkChars();
	}
	
	public static void checkChars() {
		if (isReadInProcess) {
			try {
				checkText();
					if  (isStarVal && ( currentText.length() > 0 )) {
						isReadCompleted = true;
						isDone = true;
						timer.cancel();
					}
					else if (lastChar == '\r') {  // CR
						// on a CR update view and add text to the base text
						isReadCompleted = true;
						isDone = true;
						timer.cancel();
					}
					else if (maxCharsToRead > 0) {
						if (maxCharsToRead <= currentText.length()) {
							isReadCompleted = true;
							isDone = true;
							timer.cancel();
						}
					}
				} catch (Exception e) {
				
				}
		
				if (isReadCompleted || isTimerFired) {
					finishIt();
				}
		}
	}
	
	private static void finishIt() {
		if (isTimerFired) {
			outString = "";
			if (isStarVal) {
				outString = "-1";
			}
			else if (currentText.length() > 0) {
				outString = currentText;
				currentText = "";
			}
		}
		else if (isStarVal) {
			outString = "" + currentText.charAt(0);
			if (currentText.length() > 1) {
				currentText = currentText.substring(1,currentText.length());
			}
			else
				currentText = "";
		}
		else if ((maxCharsToRead > 0) && (!crSeen)) {
			outString = currentText.substring(0,maxCharsToRead);
			if (currentText.length() > maxCharsToRead) {
				currentText = currentText.substring(maxCharsToRead+1,currentText.length()); 
			}
			else
				currentText = "";
		}
		else {
			outString = currentText;
			currentText = "";
		}
		if ( ! (isStarVal && isTimerFired) ) {
			if (crSeen)
				MDebuggerConsoleDisplay.updateView(outString+'\n', true);
			else
				MDebuggerConsoleDisplay.updateView(outString, true);
		}
		if (timer != null)
			timer.cancel();
		// call to send read data rpc
		// isActive = false;
		isReadInProcess = false;
		//return outString;
		sendReadInput(outString,isTimerFired);
/*
		if (repeatLastDebug) {
			MDebuggerSteps.setRepeatLastDebug();
		}
		MDebuggerSteps.setTimer();
*/
	}
	
	private static void checkText() {
		if ( !(isTimerFired)) {
			if ( newText.compareTo("") != 0 ) {
				int threadval = threadToken;
				while ( threadToken != 2 ) {
					threadval = threadToken;
					if ( threadval == 0 ) {
						threadToken = 2;
						try {
							Thread.sleep(20);
						} catch (Exception e) {
							
						}
					}
				}
				try {
					getNewChars();
				} catch (Exception e) {
					
				}
				threadToken = 0;
				// Display text, but keep currentText flexible - don't add to base text
				MDebuggerConsoleDisplay.updateView(currentText, false);
			}
		}
	}
	
	private static void getNewChars() {
		while ( newText.compareTo("") != 0) {
			lastChar = newText.charAt(0);
			if ((lastChar > 0x00)) {
				if ( lastChar != '\r') {
					if (lastChar != '\b') {
						outString = outString + lastChar;
						currentText = currentText + lastChar;
					}
					else {
						currentText = currentText.substring(0,currentText.length()-1);
						outString = outString.substring(0,outString.length()-1);
					}
				}
				if (newText.length() > 1) {
					newText = newText.substring(1,newText.length());
				}
				else {
					newText = "";
				}
			}
			if (lastChar == '\r') {
				crSeen = true;
				break;
			}
		}
	}
	
	private static void clearTypeAhead() {
		int threadval = threadToken;
		while ( !(threadToken == 2)) {
			threadval = threadToken;
			if (threadval == 0) {
			//if (threadToken == 0) {
				threadToken = 2;
				try {
					Thread.sleep(20);
				} catch (Exception e) {
					
				}
			}
		}
		newText = "";
		threadToken = 0;
	}
	
	private static void setTimer() {
/*
		timer = new Timer();
		timer.schedule(new TimerTask(){
			public void run() {
				if ( !(isReadCompleted)) {
					try {
						isTimerFired = true;
						checkChars();
					} catch (Exception e) {
						
					}
				}
			}
		},
		dTimeMilliseconds);
*/
/*
		new Thread(new Runnable() {
			      public void run() {
			            try { Thread.sleep(dTimeMilliseconds); } catch (Exception e) { }
			            MDebuggerConsoleDisplay.text.getDisplay().getDefault().asyncExec(new Runnable() {
			               public void run() {
			            	   if ( !(MDebuggerReadCommand.isReadCompleted)) {
			            		   try {
			            			   MDebuggerReadCommand.isTimerFired = true;
			            			   MDebuggerReadCommand.checkChars();
			            		   } catch (Exception e) {
			            		   }
			            	   }
			               }});
			      }
			   }).start();
*/
		timer = new MDebuggerReadWait();
		timer.setTimeOut(dTimeMilliseconds);
		new Thread(timer).start();
	}
	
	private static void sendReadInput(String outString, boolean isTimerFired) {
		String rpcName = "XTDEBUG READ INPUT";
		VistaLinkConnection myConnection = VistaConnection.getConnection();
		String str = "";
		if (! (myConnection == null)) {
			try {
				RpcRequest vReq = RpcRequestFactory.getRpcRequest("",rpcName);
				vReq.setUseProprietaryMessageFormat(false);
				vReq.getParams().setParam(1, "string", outString);  //
				String strIsTimerFired = "0";
				if (isTimerFired) {
					strIsTimerFired = "1";
				}
				vReq.getParams().setParam(2,"string",strIsTimerFired);
				String lastCommand = MDebuggerSteps.getLastCommand();
				vReq.getParams().setParam(3, "string", lastCommand);
				RpcResponse vResp = myConnection.executeRPC(vReq);
				str = vResp.getResults();
				MDebuggerSteps.clearRepeatLastDebug();
				StepResults.ProcessInput(str);
			} catch (Exception e) {
				if (e.getMessage() == "") {
						MDebuggerSteps.clearRepeatLastDebug();
				}
				else {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"M Debugger",
						"Error with RPC '" + rpcName + "': "+e.getMessage());
				}
			}
		}
	}
}

