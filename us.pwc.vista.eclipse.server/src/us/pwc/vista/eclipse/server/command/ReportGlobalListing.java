//---------------------------------------------------------------------------
// Copyright 2013 PwC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//---------------------------------------------------------------------------

package us.pwc.vista.eclipse.server.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.adapter.record.VistaLinkFaultException;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.foundations.utilities.FoundationsException;
import gov.va.med.iss.connection.ConnectionData;
import gov.va.med.iss.connection.VLConnectionPlugin;
import gov.va.med.iss.connection.preferences.ServerData;
import gov.va.med.iss.connection.utilities.MPiece;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import us.pwc.vista.eclipse.core.helper.MessageConsoleHelper;
import us.pwc.vista.eclipse.core.helper.MessageDialogHelper;
import us.pwc.vista.eclipse.server.Messages;
import us.pwc.vista.eclipse.server.VistAServerPlugin;
import us.pwc.vista.eclipse.server.core.CommandResult;
import us.pwc.vista.eclipse.server.dialog.GlobalListingData;
import us.pwc.vista.eclipse.server.dialog.GlobalListingDialog;

public class ReportGlobalListing extends AbstractHandler {
	private static class GlobalListingRPCResult {
		public String str;
		public String more;
		public String currCount;
		public String lastLine;
	}
	
	private static class GlobalListResult {
		public String consoleOutput;
		public String currCount;
	}
	
	private static GlobalListingRPCResult doGlobalListingRPC(VistaLinkConnection connection, GlobalListingData data, String lastLine) throws FoundationsException, VistaLinkFaultException {
		RpcRequest vReq = RpcRequestFactory.getRpcRequest("", "XT ECLIPSE M EDITOR");
		vReq.setUseProprietaryMessageFormat(false);
		String searchVal = data.searchText;
		String globalName = data.globalName;
		if (! (globalName.indexOf('^') == 0))
			globalName = "^"+globalName;
		if (! (searchVal.compareTo("") == 0)) {
			searchVal = (data.isCaseSensitive ? "1^" : "0^") + searchVal;
			searchVal = (data.isSearchDataOnly ? "1^" : "0^") + searchVal;
			// indicator for new style Global Search to make 
			// Server side backward compatible
		}
		searchVal = "1^" + searchVal;
		// JLI 101028 modified to set flag for NEWVERSION which does not
		// accumulate all results before returning initially
		// changed next line 
		lastLine = lastLine+"^1";
		vReq.getParams().setParam(1, "string", "GL");  // RD  RL  GD  GL  RS
		vReq.getParams().setParam(2, "string", "notused");
		vReq.getParams().setParam(3, "string", globalName);
		vReq.getParams().setParam(4, "string", searchVal);
		vReq.getParams().setParam(5, "string", lastLine);
		RpcResponse vResp = connection.executeRPC(vReq);
		int value2 = vResp.getResults().indexOf("\n");

		GlobalListingRPCResult result = new GlobalListingRPCResult();
		String topStr = vResp.getResults().substring(0,value2);
		result.str = vResp.getResults().substring(value2);
		result.more = MPiece.getPiece(topStr,"~^~",5);
		result.currCount = MPiece.getPiece(topStr,"~^~",4);
		result.lastLine = lastLine;
		return result;
	}
	
	private static void killTemporaryGlobal(VistaLinkConnection connection) throws FoundationsException, VistaLinkFaultException {
		GlobalListingData data = GlobalListingData.getEmptyInstance();	
		doGlobalListingRPC(connection, data, "");
	}
	
	private static GlobalListResult getGlobalListing(VistaLinkConnection connection, GlobalListingData data, String lastLine, boolean continuation)  throws FoundationsException, VistaLinkFaultException {
		String globalName = data.globalName;
		
		if (! (globalName.indexOf('^') == 0)) globalName = "^" + globalName;
		GlobalListingRPCResult rpcResult = doGlobalListingRPC(connection, data, lastLine);

		String str = rpcResult.str;
		String more =  rpcResult.more;
		String currCount =  rpcResult.currCount;
		lastLine =  rpcResult.lastLine;

		String result = "\n" + str;
		String result1 = "";
		Pattern p;
		Matcher m;
		if (data.dataOnlySelected) {
			p = Pattern.compile("(\\n)[^JUNK\\n]{4}[^\\n]*\\n",Pattern.MULTILINE);
			m = p.matcher(result);
			result = m.replaceAll("$1");
			p = Pattern.compile("(\\n)JUNK",Pattern.MULTILINE);
			m = p.matcher(result);
			result = m.replaceAll("$1");
		}
		else {
			// replace quotes with  double quotes in values
			while (! (result.compareTo(result1) == 0) ) {
				result1 = result;
				p = Pattern.compile("(\\nJUNK[^\\\"\\n]*)\\\"",Pattern.MULTILINE);
				m = p.matcher(result);
				result = m.replaceAll("$1~ECLIPSEQUOTE~");
			}
			result1 = "";
			while (! (result.compareTo(result1) == 0) ) {
				result1 = result;
				p = Pattern.compile("(\\nJUNK[^\\n]*)\\~ECLIPSEQUOTE\\~",Pattern.MULTILINE);
				m = p.matcher(result);
				result = m.replaceAll("$1\\\"\\\"");
			}

			// put equal and quote at end of global reference and remove newline
			p = Pattern.compile("(\\n[^\\n].*)\\nJUNK",Pattern.MULTILINE);

			m = p.matcher(result);
			result = m.replaceAll("$1=\\\"");
			// put end quote on line
			p = Pattern.compile("(\\n[^\\n]+)(?=\\n)");
			m = p.matcher(result);
			result = m.replaceAll("$1\\\"");
			
			if (data.setupCopySelected) {
				p = Pattern.compile("(\\n)\\^",Pattern.MULTILINE);
				m = p.matcher(result);
				result = m.replaceAll("$1S \\^");
			}
		}
		p = Pattern.compile("\\n(?=\\n)",Pattern.MULTILINE);
		m = p.matcher(result);
		result = m.replaceAll("");

		while (result.charAt(0) == '\n') {
			result = result.substring(1);
			if (result.compareTo("") == 0)
				break;
		}
		if (result.length() == 0)
			result = "<no matching nodes with data found>\n";

		if (! continuation) {
			String header = "Global Listing for "+globalName;
			String searchText = data.searchText;
			if (searchText.length() > 0) {
				String casetype = (data.isCaseSensitive ? "" : "in")+"sensitive";
				header = header + "\nwith matches to '"+searchText+"' (case "+casetype+")";
			}
			result = header + "\n\n" + result;
		}
		
		GlobalListResult glr = new GlobalListResult();
		glr.consoleOutput = result;		
		if (more.compareTo("1") == 0) glr.currCount = currCount;	
		return glr;
	}
	
	private static void killTemporaryLastLocationGlobal(VistaLinkConnection connection) {
		try {
			killTemporaryGlobal(connection);
		} catch (Throwable t) {
			String message = Messages.bind2(Messages.GLOBAL_LISTING_UNEXPECTED, t.getMessage());
			MessageDialogHelper.logAndShow(VistAServerPlugin.PLUGIN_ID, message, t);
		}		
	}
	
	private static CommandResult<GlobalListResult> getGlobals(VistaLinkConnection connection, GlobalListingData data, String lastLine, boolean continuation) {
		try {
			GlobalListResult result = getGlobalListing(connection, data, lastLine, continuation);
			return new CommandResult<GlobalListResult>(result, Status.OK_STATUS);
		} catch (Throwable t) {
			String message = Messages.bind(Messages.GLOBAL_LISTING_UNEXPECTED, t.getMessage());
			IStatus status = new Status(IStatus.ERROR, VistAServerPlugin.PLUGIN_ID, message, t);
			return new CommandResult<GlobalListResult>(null, status);
		}
	}
	
	private void handleGlobalListing(final VistaLinkConnection connection, final GlobalListingData data, final String currentCount, final int page) {
		Job job = new Job(Messages.GLOBAL_LISTING) {			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Loading globals from server", IProgressMonitor.UNKNOWN);
				final CommandResult<GlobalListResult> result = getGlobals(connection, data, currentCount, page > 0);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						IStatus status = result.getStatus();
						if (status.getSeverity() != IStatus.OK) {
							MessageDialogHelper.logAndShow(Messages.GLOBAL_LISTING, status);
						} else {
							GlobalListResult glResult =  result.getResultObject();
							MessageConsoleHelper.writeToConsole(data.globalName, glResult.consoleOutput, page == 0);
							boolean killGlobal = page > 0;
							if (glResult.currCount != null) {
								boolean moreWanted = MessageDialogHelper.question(Messages.GLOBAL_LISTING, Messages.GLOBAL_LISTING_MORE, glResult.currCount);
								if (moreWanted) {
									handleGlobalListing(connection, data, glResult.currCount, page+1);
									killGlobal = false;
								} else {
									killGlobal = true;
								}
							}
							if (killGlobal) {
								killTemporaryLastLocationGlobal(connection);	
							}								
						}
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}
	
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ConnectionData connectionData = VLConnectionPlugin.getConnectionManager().selectConnectionData(false);
		if (connectionData == null) {
			return null;
		}
		
		Shell shell = HandlerUtil.getActiveShell(event);
		ServerData datax = connectionData.getServerData();
		String title = Messages.bind(Messages.DLG_GLOBAL_LISTING_TITLE, datax.getAddress(), datax.getPort());
		GlobalListingDialog dialog = new GlobalListingDialog(shell, title);
		int result = dialog.open();
		if (result == GlobalListingDialog.OK) {
			GlobalListingData data = dialog.getData();
			MessageConsoleHelper.writeToConsole(data.globalName, "", true);
			this.handleGlobalListing(connectionData.getConnection(), data, "", 0);
		}
		return null;
	}
}
