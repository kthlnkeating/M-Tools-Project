package gov.va.mumps.debug.xtdebug;

import gov.va.med.foundations.adapter.record.VistaLinkFaultException;
import gov.va.med.foundations.utilities.FoundationsException;
import gov.va.med.iss.connection.ConnectionData;
import gov.va.mumps.debug.xtdebug.vo.StackVO;
import gov.va.mumps.debug.xtdebug.vo.StepResultsVO;
import gov.va.mumps.debug.xtdebug.vo.WatchVO;

import java.util.Iterator;

/**
 * This class handles all communication (synchronously) between eclipse and the
 * RPC. There should only be one instance of this class created for a given
 * server. Multiple async threads may call that instance. It is mostly
 * stateless as value are pushed onto the stack instead of loaded into instance
 * variables.
 * 
 * TODO: A factory should be created to handle this creational concern.
 * 
 */
public class XtdebugHandler {
	
	private static final String STEP = "STEP";
	private static final String RUN = "RUN";
	//RPC Entry points
	public static final String XTDEBUG_NEXT = "XTDEBUG NEXT";
	public static final String XTDEBUG_START = "XTDEBUG START";
	public static final String XTDEBUG_ADD_WATCH = "XTDEBUG ADD WATCH";
	public static final String XTDEBUG_DELETE_WATCH = "XTDEBUG DELETE WATCH";
	public static final String XTDEBUG_ADD_BREAKPOINT = "XTDEBUG ADD BREAKPOINT";
	public static final String XTDEBUG_DELETE_BREAK = "XTDEBUG DELETE BREAK";
	public static final String XTDEBUG_READ_INPUT = "XTDEBUG READ INPUT";


	private volatile boolean readTimeout; //TODO: what are the benefits again of using volatile here?
	private volatile String lastDebugCommand;
	
	private ConnectionData connectionData;
	private StepResultsParser parser;
	
	
	public XtdebugHandler(ConnectionData connectionData) {
		this.connectionData = connectionData;
		parser = new StepResultsParser();
	}
	
	/**
	 * method to start a debugging session
	 * @param mCode - contains the line of code to be executed.
	 */
	public StepResultsVO startDebug(String mCode) {
		return fetchResults(XTDEBUG_START, mCode);
	}

	public StepResultsVO resume() {
		return stepDebug(RUN);
	}
	
	public StepResultsVO stepInto() {
		return stepDebug(STEP); //currently STEP behaves like stepinto
	}
		
	public void addWatchpoint(String watchPoint) {
		fetchResults(XTDEBUG_ADD_WATCH, watchPoint);
	}
	
	public void removeWatchpoint(String watchPoint) {
		fetchResults(XTDEBUG_DELETE_WATCH, watchPoint);
	}
	
	public void addBreakpoint(String breakPoint) {
		fetchResults(XTDEBUG_ADD_BREAKPOINT, breakPoint);
	}
	
	public void removeBreakpoint(String breakPoint) {
		fetchResults(XTDEBUG_DELETE_BREAK, breakPoint);
	}
	
	public StepResultsVO sendReadInput(String input) {
		return fetchResults(XTDEBUG_READ_INPUT, input);
	}
	
	private StepResultsVO stepDebug(String debugCommand) {
		return fetchResults(XTDEBUG_NEXT, debugCommand);
//		while (repeatLastDebug) { // commented out because repeatLastDebug has no chance to become true--jspivey
//			doDebug(dbCommand);
//		}
	}
	
	public synchronized StepResultsVO fetchResults(String rpcName, String debugCommand) { //should this by synchronized? if so I cannot stay in here for too long. which shouldn't be the case, this attempts to get the results as soon as possible and returns them
		try {
			return handleRPC(rpcName, debugCommand);
		} finally {
			if (!rpcName.equals(XTDEBUG_NEXT)) //what was the last command. may also want to cover this in write commands too? another design choice would be to refer to the interactive GUI thread of MDebugger and see what its last command is, as that will never be READ/WRITE
				lastDebugCommand = debugCommand;
		}
	}
	
	private StepResultsVO handleRPC(String rpcName, String debugCommand) {
		boolean handleResults = 
				rpcName.equals(XTDEBUG_START)	 ||
				rpcName.equals(XTDEBUG_NEXT)	 ||
				rpcName.equals(XTDEBUG_READ_INPUT);
		
		if (this.connectionData == null) {
			throw new RuntimeException("Not connected to VistaServer");
		}

		String strResults = "";
		try {
			strResults = callRPC(rpcName, debugCommand);
			
			//this helps it to skip over line labels, which appear to return an empty string from the server
			for (int i = 1; handleResults && strResults.trim().equals("") && i <= 24; i++) {
				//TODO: is this even possible now to have blank strings aside from removing breakpoints?
				System.out.println("Response was empty, sending "+ debugCommand +" again: "+ i);
				strResults = callRPC(rpcName, debugCommand);
			}
		} catch (VistaLinkFaultException e) {
			e.printStackTrace();
			throw new RuntimeException(e); //TODO: use checked ex?
		} catch (FoundationsException e) {
			e.printStackTrace();
			throw new RuntimeException(e); //TODO: use checked ex?
		}
		
		if (strResults.trim().equals("") && handleResults)
			throw new RuntimeException("Unable to fetch any results from Debugger after 25 requests"); //TODO: this should definetly be a a custom exception to indicate to the caller to terminate the IProcess
		
		StepResultsVO results = null;		
		if (handleResults) {
			results = parser.parse(strResults);
			
			/*
			 * XTDEBUG API fix for suspending on linelabels:
			 * Whenever a linelabel is hit by the server side debugger it will
			 * suspend, and furthermore it returns only the stack and 
			 * variables, while leaving location and next command null. If
			 * this situation is encountered, the client side fix is to resend
			 * the request again.
			 */
			if (handleResults) {
				if (
						!results.isComplete() && 
						results.getLineLocation() == -1 &&
						results.getNextCommnd() == null) {
					return handleRPC(rpcName, debugCommand); //TODO: this could potentially endless loop, maybe do a for command with a limit //TODO: how does syncrhonize work here with reentry locks?
				}
				printResults(debugCommand, results);
			}
		}
		
		
		return results;
	}

	private String callRPC(String rpcName, String debugCommand)
			throws FoundationsException, VistaLinkFaultException {
		//RpcRequest vReq = RpcRequestFactory.getRpcRequest("", rpcName);
		//vReq.setUseProprietaryMessageFormat(false);
		//vReq.getParams().setParam(1, "string", debugCommand); // RD RL GD GL RS

		if (rpcName.equals(XTDEBUG_READ_INPUT)) {
			//vReq.getParams().setParam(2, "string", readTimeout ? "1" : "0");
			//vReq.getParams().setParam(3, "string", lastDebugCommand);
			return this.connectionData.rpcXML(rpcName, debugCommand, readTimeout ? "1" : "0", lastDebugCommand);
		} else {
			return this.connectionData.rpcXML(rpcName, debugCommand);
		}

		//RpcResponse vResp = connection.executeRPC(vReq);
//		System.out.println("response from server: ");
//		System.out.println(vResp.getResults());
		//return vResp.getResults();
	}
	
	private void printResults(String debugCommand, StepResultsVO results) {
		System.out.println("RESPONSE for: " +debugCommand);
		System.out.println("reason: " + results.getResultReason());
		System.out.println("complete: "+ results.isComplete());
		System.out.println("nextCommand: "+ results.getNextCommnd());
		System.out.println("lastCommand: "+ results.getLastCommand());
		System.out.println("LineLocation: "+ results.getLineLocation());
		System.out.println("TagLocation: "+ results.getLocationAsTag());
		System.out.println("Routine:" + results.getRoutineName());
		System.out.println("Has variables: "+ results.getVariables().hasNext());
		System.out.println("STACK:");
		Iterator<StackVO> stackItr = results.getStack();
		while (stackItr.hasNext()) {
			StackVO stack = stackItr.next();
			System.out.println(stack.getStackName() +" called by: "+ stack.getCaller());
		}
		Iterator<WatchVO> watchItr = results.getWatchedVars();
		if (watchItr.hasNext()) {
			System.out.println("WATCHED VARS:");
		
			while (watchItr.hasNext()) {
				System.out.println(watchItr.next().getVariableName());
			}
		}
		if (results.getWriteLine() != null) {
			System.out.println("WRITE LINE: ");
			System.out.println(results.getWriteLine());
		}
	}
	
}
