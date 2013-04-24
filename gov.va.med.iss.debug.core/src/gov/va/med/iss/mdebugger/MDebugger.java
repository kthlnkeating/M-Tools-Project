package gov.va.med.iss.mdebugger;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.adapter.record.VistaLinkFaultException;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.foundations.utilities.FoundationsException;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.mdebugger.vo.StackVO;
import gov.va.med.iss.mdebugger.vo.StepResultsVO;
import gov.va.med.iss.mdebugger.vo.WatchVO;

import java.util.Iterator;

public class MDebugger {
//TODO: implement FutureTask<StepResultsVO> for async.
	
	/*
	 * The main debug process needs to async call this and not block. it is a wrapper not something which executes and does work.
	 * the console will need its own thread of this object too. So there will be at least 2 threads, possibly running at the same
	 * time. They may need their own state?
	 */

	//RPC Entry points
	public static final String XTDEBUG_NEXT = "XTDEBUG NEXT";
	public static final String XTDEBUG_START = "XTDEBUG START";
	public static final String XTDEBUG_ADD_WATCH = "XTDEBUG ADD WATCH";
	public static final String XTDEBUG_DELETE_WATCH = "XTDEBUG DELETE WATCH";
	public static final String XTDEBUG_ADD_BREAKPOINT = "XTDEBUG ADD BREAKPOINT";
	public static final String XTDEBUG_DELETE_BREAK = "XTDEBUG DELETE BREAK";
	public static final String XTDEBUG_READ_INPUT = "XTDEBUG READ INPUT";

	//dependencies
	RPCHandler rpcHandler;
	
	//thread state
	private String rpcName;
	private String debugCommand;

	public MDebugger(RPCHandler rpcHandler) {
		this.rpcHandler = rpcHandler;
	}

	public StepResultsVO resume() {
		return stepDebug("RUN");
	}
	
	//does not step over, acts more like step in
//	public StepResultsVO stepOver() {
//		return stepDebug("STEP"); 
//	}
	
	public StepResultsVO stepInto() {
		return stepDebug("STEP"); //currently STEP behaves like stepinto
	}
	
	//not implemented
//	public StepResultsVO stepOut() {
//		return stepDebug("STEPOUT");
//	}
	
	//not supported, not sure why the original mdebug assumes this is a valid rpc parm
//	public StepResultsVO terminate() {
//		return stepDebug("TERMINATE");
//	}

	private StepResultsVO stepDebug(String command) {
		rpcName = XTDEBUG_NEXT;
		debugCommand = command;
		return rpcHandler.fetchResults(rpcName, debugCommand);
//		while (repeatLastDebug) { // commented out because repeatLastDebug has no chance to become true--jspivey
//			doDebug(dbCommand);
//		}
	}
	
	/**
	 * method to start a debugging session
	 * @param mCode - contains the line of code to be executed.
	 */
	public StepResultsVO startDebug(String mCode) {
		rpcName = XTDEBUG_START;
		debugCommand = mCode;
		return rpcHandler.fetchResults(rpcName, debugCommand);
	}
		
	public void addWatchpoint(String watchPoint) {
		rpcName = XTDEBUG_ADD_WATCH;
		debugCommand = watchPoint;
		rpcHandler.fetchResults(rpcName, debugCommand);
	}
	
	public void removeWatchpoint(String watchPoint) {
		rpcName = XTDEBUG_DELETE_WATCH;
		debugCommand = watchPoint;
		rpcHandler.fetchResults(rpcName, debugCommand);
	}
	
	public void addBreakpoint(String breakPoint) {
		rpcName = XTDEBUG_ADD_BREAKPOINT;
		debugCommand = breakPoint;
		rpcHandler.fetchResults(rpcName, debugCommand);
	}
	
	public void removeBreakpoint(String breakPoint) {
		rpcName = XTDEBUG_DELETE_BREAK;
		debugCommand = breakPoint;
		rpcHandler.fetchResults(rpcName, debugCommand);
	}
	
	public void sendReadInput(String input) {
		rpcName = XTDEBUG_READ_INPUT;
		debugCommand = input;
		rpcHandler.fetchResults(rpcName, debugCommand);
		//TODO: see if this returns results in the API
	}
	
//	public void setTimer() {
//		   new Thread(new Runnable() {
//			      public void run() {
//			            try { Thread.sleep(50); } catch (Exception e) { }
////			            MDebuggerConsoleDisplay.text.getDisplay().getDefault().asyncExec(new Runnable() {
////			               public void run() {
////			                  stepDebug(lastCommand);
////			               }
////			            });
//			            stepDebug(lastCommand);
//			         }
//			   }).start();
//	}

}
