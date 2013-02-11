/*
 * Created on Aug 24, 2005
 *
 * 050830 - Added code to convert double quotes to pairs of double quotes in
 *  quoted global data.
 *        - Added code to permit generating a copy and paste version if "isForCopy"
 *  is true.
 */
package gov.va.med.iss.meditor.utils;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.connection.utilities.MPiece;

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPage;
/*
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
*/
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GlobalListing {
	
	static private String totalListing = "";
	static private boolean killTempGlobal = false;
//	static private boolean gettingMore = false;
	static private IEditorPart mainEditor = null;
	
	public static void getGlobalListing(String globalName) {
		getGlobalListing(globalName, false, false, "", true, true);
	}
	
	public static void getGlobalListing(String globalName, boolean isForCopy, boolean isDataOnly, String searchText, boolean isSearchDataOnly, boolean isSearchCaseSensitive) {
//		gettingMore = false;
		killTempGlobal = false;
		mainEditor = null;
		getGlobalListing(globalName, isForCopy, isDataOnly, "", searchText, isSearchDataOnly, isSearchCaseSensitive);
	}
	
	public static void getGlobalListing(String globalName, boolean isForCopy, boolean isDataOnly, String lastLine, String searchText, boolean isSearchDataOnly, boolean isSearchCaseSensitive) {
		if (lastLine.compareTo("") == 0) {
			totalListing = "";
		}
		if ( ! (globalName.indexOf('(') == 0)) {
			if (! (globalName.indexOf('^') == 0))
				globalName = "^"+globalName;
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
					String searchVal = searchText;
					if (! (searchVal.compareTo("") == 0)) {
						searchVal = (isSearchCaseSensitive ? "1^" : "0^") + searchVal;
						searchVal = (isSearchDataOnly ? "1^" : "0^") + searchVal;
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
					RpcResponse vResp = myConnection.executeRPC(vReq);
					int value2 = vResp.getResults().indexOf("\n");
/*
					int value1 = vResp.getResults().indexOf("1");
					int value = vResp.getResults().length();
					int location = 0;
*/
					String topStr = vResp.getResults().substring(0,value2);
					String str = vResp.getResults().substring(value2);
					String more = MPiece.getPiece(topStr,"~^~",5);
					String currCount = MPiece.getPiece(topStr,"~^~",4);
					String totalCount = MPiece.getPiece(topStr,"~^~");

					String result = "\n" + str;
					String result1 = "";
					Pattern p;
					Matcher m;
					if (! killTempGlobal) {
						if (isDataOnly ) {
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
							
							if (isForCopy) {
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
						totalListing = totalListing + result;

						String header = "Global Listing for "+globalName;
						if (searchText.length() > 0) {
							String casetype = (isSearchCaseSensitive ? "" : "in")+"sensitive";
							header = header + "\nwith matches to '"+searchText+"' (case "+casetype+")";
							
						}
						setupGlobalDirWindow(globalName, header+"\n\n"+totalListing);
						
						if (more.compareTo("1") == 0) {
							YesNoDialog ynDialog = new YesNoDialog();
							// next line replaced by following to handle new version of global lists which does not know total number available
//							if (ynDialog.open("More Global to List","So far "+currCount+" of "+totalCount+" nodes have been brought in.  Continue download?")) {
							if (ynDialog.open("More Global to List","So far "+currCount+" nodes have been brought in.  Additional data remains.  Continue download?")) {
//								gettingMore = true;
								getGlobalListing(globalName,isForCopy,isDataOnly,currCount,searchText,isSearchDataOnly,isSearchCaseSensitive);
								// now kill off remaining temp global
								killTempGlobal = true;
								getGlobalListing("",isForCopy,isDataOnly,"",searchText,isSearchDataOnly,isSearchCaseSensitive);
								killTempGlobal = false;
							}
						}
					} // if ! killTempGlobal

				} catch (Exception e) {
					MessageDialog.openInformation(
							MEditorUtilities.getIWorkbenchWindow().getShell(),
							"Meditor Plug-in",
							"Error encountered while executing RPC "+e.getMessage());
				}
			}
			
		}
	}
	/*
	 * returns the input string with each double quote character
	 * expanded to be two double quotes.
	 * 
	 * @param	str  the input string
	 * @return	the string with two double quote characters for 
	 * 			each double quote character in the input string.
	 */
/*
	private static String doubleEachQuote(String str) {
		String str1 = "";
		while (str.indexOf('"') > -1) {
			str1 = str1 + str.substring(0,str.indexOf('"')+1)+"\"";
			str = str.substring(str.indexOf('"')+1);
		}
		str1 = str1 + str;
		return str1;
	}
*/	
	private static void setupGlobalDirWindow(String name, String globalData) {
		IStorage storage = new StringStorage(name, globalData);
		IStorageEditorInput input = new StringInput(storage);
		try {
			if (mainEditor == null) {
				IWorkbenchPage page = MEditorUtilities.getIWorkbenchPage();
					if (page != null)
						mainEditor = page.openEditor(input, "org.eclipse.ui.DefaultTextEditor");
			}
			else {  // update current page
				IEditorSite editorSite = mainEditor.getEditorSite();
				mainEditor.init(editorSite, input);
			}
		} catch (Exception e) {
			
		}
	}

 
}
