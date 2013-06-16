package org.mumps.meditor;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.foundations.utilities.FoundationsException;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.connection.utilities.MPiece;
import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.preferences.MEditorPrefs;
import gov.va.med.iss.meditor.utils.MEditorUtilities;

import java.util.HashMap;

import org.eclipse.jface.dialogs.MessageDialog;

public class MEditorRPC {

	private VistaLinkConnection connection;

	public MEditorRPC(VistaLinkConnection connection) {
		this.connection = connection;
	}

	public String getRoutineFromServer(String routineName) throws RoutineNotFoundException {

		try {
			RpcRequest vReq = RpcRequestFactory.getRpcRequest("",
					"XT ECLIPSE M EDITOR");
			vReq.setUseProprietaryMessageFormat(false);
			vReq.getParams().setParam(1, "string", "RL"); // RD RL GD GL RS
			vReq.getParams().setParam(2, "string", "notused");
			vReq.getParams().setParam(3, "string", routineName);
			RpcResponse vResp = connection.executeRPC(vReq);
			String result = vResp.getResults();
			if (result.startsWith("-1^Error Processing load request")) {
				throw new RoutineNotFoundException();
			} else {
				return result.substring(result.indexOf('\n')+1);
			}
		} catch (FoundationsException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public String saveRoutineToServer(String routineName, String contents, boolean isCopy) throws FoundationsException {
		String saveResults = "";
		String doc = "";
		int nlines = 0;
		
		contents = MEditorUtils.cleanSource(contents);
		
		HashMap hm = new HashMap();
		// convert to array by line
		String stra= "";
		while (!(contents.compareTo("") == 0)) {
			int fromIndex1 = contents.indexOf('\n');
			if ( (fromIndex1 == -1))
				fromIndex1 = contents.length();
			if (fromIndex1 > -1) {
				stra = contents.substring(0,fromIndex1+1);
				if (contents.length() > fromIndex1)
					contents = contents.substring(fromIndex1+1);
				else
					contents = "";
				// remove line terminator
				stra = stra.replaceAll("\r\n","");
				// convert initial tab character to a space
				int loc = stra.indexOf("\t");
				if (loc > -1) {
					stra = stra.substring(0,loc)+" "+stra.substring(loc+1);
				}
				// skip blank lines
				if (! (stra.compareTo("") == 0)) {
					nlines = nlines + 1;
					hm.put(Integer.toString(nlines),stra);
				}
			}
		}
		
		RpcRequest vReq = RpcRequestFactory.getRpcRequest("", "XT ECLIPSE M EDITOR");
//			if (this.xmlRadioButton.isSelected()) {
//				vReq.setUseProprietaryMessageFormat(false);
//			} else {
			vReq.setUseProprietaryMessageFormat(true);
//			}
		vReq.getParams().setParam(1, "string", "RS");  // RD  RL  GD  GL  RS
		vReq.getParams().setParam(2, "array", hm);
		vReq.getParams().setParam(3, "string", routineName);
		String updateEntryInRoutineFile = MEditorPrefs.getPrefs(MEditorPlugin.P_DEFAULT_UPDATE);
		//String unitTestName = getUnitTestName(routineName+".m"); //--jspivey not supported beause it is loading this value into the eclipse persistence store when the routine is loaded in. This won't work for routines imported from the filesystem
		String unitTestName = "";
		String updateFirstLine = isCopy ? "1" : "0";
		//String updateFirstLine = "0"; //fixed because it makes syncing and comparing files difficult when it changes on the server but not locally --jspivey
		updateEntryInRoutineFile = (updateEntryInRoutineFile=="true") ? "1" : "0";
		vReq.getParams().setParam(4, "string",updateEntryInRoutineFile +"^"+unitTestName+"^"+updateFirstLine);
		RpcResponse vResp = connection.executeRPC(vReq);
		saveResults = setFullDocHeader(routineName);

		int index1 = vResp.getResults().indexOf('\n');
		if (index1 > -1) {
			String line1 = vResp.getResults().substring(0,index1); //vResp.getResults().indexOf('\n'));
			if (line1.indexOf("-1") == 0) {
				MessageDialog.openWarning(
						MEditorUtilities.getIWorkbenchWindow().getShell(),
						"Meditor Plug-in Routine Save",
						MPiece.getPiece(line1,"^",2));
			}
			doc = vResp.getResults().substring(vResp.getResults().indexOf('\n'));
		}
		else
			doc = "";
		boolean isErrorsOrWarnings = false;
		int n = 0;
		if (doc.contains("no tags with variables to list")) {
			doc = doc.replace("Variables which are neither NEWed or arguments","");
			doc = doc.replace("no tags with variables to list","");
		}
		while (doc.contains("\n\n")) {
			doc = doc.replaceAll("\n\n","\n");
		}
		while (n < doc.length()) {
			int n1 = doc.indexOf('\n',n);
			String str = doc.substring(n,n1);
			int nbase = n;
			n = n1+1;
			if (str.indexOf("Compiled list of Errors and Warnings") == 0) {
				n1 = doc.indexOf('\n',n);
				String str1 = doc.substring(n,n1);
				if (str1.compareTo("No errors or warnings to report") == 0) {
					String str2 = "";
					if (nbase > 0) {
						str2 = doc.substring(0,nbase);
					}
					doc = str2 + doc.substring(n1+1,doc.length());
					n = nbase;
				}
				else
					isErrorsOrWarnings = true;
			}
			if (str.compareTo("Variables which are neither NEWed or arguments") == 0) {
				n = n + 1;  // skip blank line
				n1 = doc.indexOf('\n',n);
				String str1 = doc.substring(n,n1);
				if (str1.compareTo("no tags with variables to list") == 0) {
					String str2 = "";
					if (nbase > 0) {
						str2 = doc.substring(0,nbase);
					}
					doc = str2 + doc.substring(n1,doc.length());
					n = nbase;
				}
			}
		}
		while (doc.indexOf('\n') == 0) {
			if (doc.length() > 1)
				doc = doc.substring(1);
			else
				doc = "";
		}
		if (isErrorsOrWarnings) {
			MessageDialog.openWarning(
				MEditorUtilities.getIWorkbenchWindow().getShell(),
				"Meditor Plug-in Routine Save",
				"Routine saved, but XINDEX has reported errors or warnings in the M code. Refer to the Console for details.");
		}

		return saveResults + doc;
	}

	private String setFullDocHeader(String routineName) {
		String currentServer = VistaConnection.getCurrentServer();
		String currentServerName = MPiece.getPiece(currentServer,";");
		String currentServerAddress = MPiece.getPiece(currentServer,";",2);
		String currentServerPort = MPiece.getPiece(currentServer,";",3);
		String currentServerProject = MPiece.getPiece(currentServer,";",4);
		return routineName +" saved to: "
			+ currentServerName + " ("+currentServerAddress+", "+currentServerPort+")\n\n";
	}

}
