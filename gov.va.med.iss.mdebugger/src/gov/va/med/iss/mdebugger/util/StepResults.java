package gov.va.med.iss.mdebugger.util;

import gov.va.med.iss.mdebugger.views.AllValuesView;
import gov.va.med.iss.mdebugger.views.CurrentStackView;
import gov.va.med.iss.mdebugger.views.MDebuggerConsoleDisplay;
import gov.va.med.iss.mdebugger.views.MDebuggerReadCommand;
import gov.va.med.iss.mdebugger.views.WatchValuesView;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


public class StepResults {
	
	private static int numChars;
	private static int timeOut;
	private static boolean isStarRead;
	private static boolean isTypeAhead;
	private static boolean isReadCommand;
	private static boolean isWriteCommand;
	public static boolean doneFlag=true;
	
	public static boolean doneFlag() {
		return doneFlag;
	}

	public static void ProcessInput(String input) {
		isReadCommand = false;
		isWriteCommand = false;
		String currReason = "";
		String rouName = "";
		String routineName = "";
		int lineNumber = 0;
		char testChar = 'J';
		IMarker marker = null;
		IEditorPart editor = null;
		WatchValuesView.updateView(""); // clear Watch Values before updating
		PlatformUI.getWorkbench().
			getActiveWorkbenchWindow().
				getActivePage().activate(AllValuesView.getCurrentInstance());
		int loc = input.indexOf("SECTION=");
		while (loc > -1) {
			String currString;
			int nextval = input.indexOf("SECTION=",loc+1);
			if (nextval > -1) {
				currString = input.substring(loc,nextval);
				input = input.substring(nextval);
				loc = 0;
			}
			else {
				currString = input.substring(loc);
				input = "";
				loc = -1;
			}
			int newLine = currString.indexOf('\n');
			String sectionType = currString.substring(0,newLine);
			String sectionVal = currString.substring(newLine+1);
			sectionType = sectionType.substring(8);
			if (sectionType.compareTo("REASON") == 0) {
				currReason = sectionVal;
				if (sectionVal.indexOf("DONE") == 0) {
					MDebuggerSteps.clearRepeatLastDebug();
					doneFlag = true;
				}
				if (input.compareTo("") == 0)
					input = "SECTION=STACK\n";
			}
			else if (sectionType.compareTo("WRITE") == 0) {
				currString = writeLine(currString,true);
				currString = nextString(currString, "LINE: ");
				if (currReason.indexOf("WRITE") == 0)
					isWriteCommand = true;
					//MDebuggerSteps.setRepeatLastDebug();
			}
			else if (sectionType.compareTo("READ") == 0) {
				isReadCommand = true;
//				currString = writeLine(currString,false);
				String strNumChars = getLine(currString,"NUM CHARS: ");
				if (strNumChars.compareTo("") == 0)
					strNumChars = "0";
				numChars = Integer.parseInt(strNumChars);
				currString = nextString(currString, "NUM CHARS: ");
				String strTimeOut = getLine(currString,"TIMEOUT: ");
				if (strTimeOut.compareTo("") == 0)
					strTimeOut = "0";
				timeOut = Integer.parseInt(strTimeOut);
				currString = nextString(currString, "TIMEOUT: ");
				String starRead = getLine(currString,"STAR-READ: ");
				isStarRead = false;
				if (starRead.compareTo("1") == 0 )
					isStarRead = true;
				currString = nextString(currString, "STAR-READ: ");
				String typeAhead = getLine(currString,"TYPE-AHEAD: ");
				isTypeAhead = false;
				if (typeAhead.compareTo("1") == 0)
					isTypeAhead = true;
				currString = nextString(currString, "TYPE-AHEAD: ");
			}
			else if (sectionType.compareTo("VALUES") == 0) {
				currString = reconnect(currString);
				AllValuesView.updateView(currString);
			}
			else if (sectionType.compareTo("STACK") == 0 || doneFlag) {
				currString = currReason + "\n\n"+ sectionVal;
				CurrentStackView.updateView(currString);
			}
			else if (sectionType.compareTo("WATCH") == 0) {
				int loc1 = currString.indexOf("WATCH DATA");
				if (loc1 > -1) {
					currString = currString.substring(loc1+11);
					currString = currString.trim();
					WatchValuesView.updateView(currString+"\n");
					PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().
					getActivePage().activate(WatchValuesView.getCurrentInstance());
				}
			}
			else if (sectionType.compareTo("LOCATION") == 0) {
				int loc1 = currString.indexOf("ROUTINE: ");
				int loc2 = currString.indexOf('\n',loc1);
				routineName = currString.substring(loc1+9,loc2);
				loc1 = currString.indexOf("LINE: ");
				loc2 = currString.indexOf('\n',loc1);
				String strLineNum = currString.substring(loc1+6,loc2);
				if (strLineNum.length() == 0) {
					lineNumber = 0;
				}
				else {
					lineNumber = Integer.parseInt(strLineNum) - 3;
					if (lineNumber < 0) {
						lineNumber = 0;
					}
				}
				editor = getEditor(routineName);
			}
		}
//		if ((routineName.compareTo("") != 0) && (editor == null)) {
//			RoutineLoad rouLoad = new RoutineLoad();
//			rouLoad.loadRoutine(routineName,true, null);
//			editor = getEditor(routineName);
//		}
//		if (editor != null) {
//		MEditor meditor = (MEditor)editor;
////		loc = meditor.getCaretOffset();
////		IDocumentProvider docProvider = meditor.meditorDocumentProvider;
//		meditor.setTopIndex(lineNumber);
//		final IFile file = RoutineSave.getIFile(routineName);
//		try {
//			if (marker.exists())
//				marker.delete();
//			marker = file.createMarker(IMarker.PROBLEM);
//			marker.setAttribute("Line",lineNumber);
//			MarkerUtilities.setLineNumber(marker,lineNumber);
//		} catch (Exception e) {
//			
//		}
//		}
/*
		HashMap map = new HashMap();
		MarkerUtilities.setLineNumber(map, lineNumber);
		MarkerUtilities.createMarker(resource, map, IMarker.TEXT);
*/
		if (isReadCommand) {
			MDebuggerReadCommand.doRead(timeOut,isStarRead,numChars,isTypeAhead);
		}
		else if (isWriteCommand && (! doneFlag)) {
			MDebuggerSteps.setTimer();
		}
	}

	private static IEditorPart getEditor(String routineName) {
		int loc2;
		IEditorPart editor = null;
		IWorkbenchPage wbpEditor = null;
		routineName = routineName.trim();
		if (routineName.compareTo("") != 0) {
			routineName = routineName + ".m";
			IWorkbench wb = PlatformUI.getWorkbench();
			
			IWorkbenchWindow wbWindow = wb.getActiveWorkbenchWindow();
/*			
			IContributionItem[] contributionItem = new IContributionItem[200];
			if (wbWindow instanceof WorkbenchWindow) {
				WorkbenchWindow wbw = (WorkbenchWindow)wbWindow;
				contributionItem = wbw.getMenuManager().getItems();
			}
			for (int i=0; i<contributionItem.length; i++) {
				String str = contributionItem.toString();
			}
*/
			IWorkbenchPage[] wbpage = wbWindow.getPages();
			for (int iPageNum=0; iPageNum < wbpage.length; iPageNum++) {
				IEditorReference[] edRef = wbpage[iPageNum].getEditorReferences();
				for (int iEdRefNum = 0; iEdRefNum < edRef.length; iEdRefNum++) {
					String edName = edRef[iEdRefNum].getName();
					if (edName.compareTo(routineName) == 0) {
						editor = edRef[iEdRefNum].getEditor(true);
						editor.setFocus();
						wbpEditor = wbpage[iPageNum];
					}
				}
			}
			if ((wbpEditor != null) && (editor != null)) {
				wbpEditor.activate(editor);
			}
		}
		return editor;
	}
	
	/**
	 * method to display the current input line from an RPC associated
	 * with either a write command or a prompt in a read command to the user
	 * @param currString - the input line of text from the RPC, only the text 
	 * which begins with 'LINE: ' and is terminated by a newline is displayed.
	 * 
	 * @param newLine - indicates whether a newline should be added at the end
	 *  of the text displayed (generally false for Read requests)
	 *   
	 * @return - any remaining text in the input string beyond that which is displayed.
	 */
	private static String writeLine(String currString, boolean newLine) {
//		String strLineText = getLine(currString,"LINE: ");
		String textId = "LINE: ";
		int loc1 = currString.indexOf(textId);
		String strLineText = currString.substring(loc1+textId.length());
//		if (newLine)
//			strLineText = strLineText + '\n';
		strLineText = strLineText.substring(0,strLineText.length()-1);
		MDebuggerConsoleDisplay.updateView(strLineText, true);
//		MDebuggerConsoleDisplay.strval = MDebuggerConsoleDisplay.strval + strLineText;
		return nextString(currString,"LINE: ");
	}
	
	/**
	 * method to return the first instance of text which is preceeded by
	 * a specific identifier (textId) and terminated by a newline.  With 
	 * input of currString as  'some text\nLINE: desired text\nmore text',
	 * and textId as 'LINE: ', the returned value would be 'desired text'
	 * 
	 * @param currString - the input string
	 * 
	 * @param textId - the identifying text, e.g., 'LINE: ', which is to 
	 * preceed that string to be returned
	 * 
	 * @return - the string of input text which follows the identifying
	 * text and preceeds the next newline.
	 */
	private static String getLine(String currString, String textId) {
		int loc1 = currString.indexOf(textId);
		int loc2 = currString.indexOf('\n',loc1);
		return currString.substring(loc1+textId.length(),loc2);
	}
	
	/**
	 * method to return a substring of the input string (currString) which 
	 * follows a section of the input string terminated by the first newline
	 * after the occurence of the input identifier string textId.  With input
	 * of currString as 'some text\nLINE: desired text\nmore text',
	 * and textId as 'LINE: ', the returned value would be 'more text' 
	 * 
	 * @param currString - the input string of text to be operated on
	 * 
	 * @param textId - a text identifier which marks the beginning of the string
	 * which preceeds the text to be returned.
	 * 
	 * @return - the remainder of the text in the input currString following a 
	 * newline immediately preceeded by the text in textId.
	 */
	private static String nextString(String currString, String textId) {
		int loc1 = currString.indexOf(textId);
		int loc2 = currString.indexOf('\n');
		return currString.substring(loc2+1);
	}

	private static String reconnect(String input) {
		String output = "";
		int newLine1 = input.indexOf('\n');
		while (newLine1 > 0) {
			int newLine2 = input.indexOf('\n',newLine1+1);
			output = output + input.substring(0,newLine1)+'=';
			output = output + 
					DebuggerUtils.checkQuotes(input.substring(newLine1+1,newLine2))
					+ '\n';
			input = input.substring(newLine2+1);
			newLine1 = input.indexOf('\n');
		}
		return output;
	}
	
	public static void clearDoneFlag() {
		doneFlag = false;
	}
}
