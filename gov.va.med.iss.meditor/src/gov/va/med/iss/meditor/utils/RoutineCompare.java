package gov.va.med.iss.meditor.utils;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import gov.va.med.iss.connection.utilities.MPiece;
import gov.va.med.iss.meditor.utils.RoutineLoad;
import gov.va.med.iss.connection.actions.VistaConnection;

public class RoutineCompare {
	
	private static String routineName;
	
	public static void main(String args[]) {
		String file1 = "A\r\nB\r\nC\r\nD\r\nF\r\nG\r\nH\r\nJ\r\nQ\r\nZ\r\n";
		String file2 = "A\r\nB\r\nC\r\nD\r\nE\r\nF\r\nG\r\nI\r\nJ\r\nK\r\nR\r\nX\r\nY\r\nZ";
		//file1 = MEditorUtilities.fileToString("c:\\@development\\100323 Wally Problem\\XUDHGUI KRN.m");
		//file2 = MEditorUtilities.fileToString("c:\\@development\\100323 Wally Problem\\XUDHGUI 100323_last.m");
		try {
			compareRoutines(file1,file2,"the PREVIOUS version loaded from the server","routineName",false);
		} catch (Exception e) {
			
		}
	}
	
	public static void compareRoutines(String file1, String file2, String comparedToString, String rouName, boolean isSave) throws Exception {
		routineName = rouName;
		file1 = file1.replaceAll("<","&lt;");
		file1 = file1.replaceAll(">","&gt;");
		file2 = file2.replaceAll("<","&lt;");
		file2 = file2.replaceAll(">","&gt;");
		String[] sequence = getSequence(file1, file2);
		String[] outLines = lineList(file1,file2,sequence);
		String strVal = MPiece.getPiece(VistaConnection.getCurrentServer(),";");
		String strProject = MPiece.getPiece(VistaConnection.getCurrentServer(),";",4);
		if (! (strProject.compareTo("") == 0)) {
			strVal = strVal + " (Project "+strProject+")";
		}
		String outFile = buildPage(outLines, comparedToString, strVal);
		try {
//			String location = RoutineLoad.getFullFileLocation("fileComparison.htm");
//			if (! (new File(location).exists())) {
//				new File(location).mkdirs();
//			}
// jli 101115 some users don't have this directory
//			String location = "c:\\Program Files\\Vista\\fileComparison.html";
			// try and use current location.
/*
// JLI 110717 thought about saving file with routine name and date-time in comparison html file - went back to original 
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd_hhmmss");
			Date date = new Date();
			String fileName = "";
			String dtString1;
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(date);
			dtString1 = dateFormat.format(gc.getTime());
			String location = routineName+" fileComparison "+dtString1+".html";
// end of 110717 addition and commented out
*/
// JLI 110913 put html files in separate directory within workspace
// JLI 110913			String location = "fileComparison.html";
			SimpleDateFormat dateFormat = new SimpleDateFormat(" yyMMdd_HHmmss");
			Date date = new Date();
			String fileName = "";
			String dtString1;
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(date);
			dtString1 = dateFormat.format(gc.getTime());
			if (! (new File("diff_htmls").exists())) {
				new File("diff_htmls").mkdirs();
			}
			String location = "";
			location = "on saving to ";
			if (! isSave) {
				location = "on loading from ";
			}
			location = "diff_htmls\\"+routineName+" fileComparison "+location+strVal+dtString1+".html";
//			String location = routineName+" fileComparison "+strVal+dtString1+".html";
			FileWriter fw;
			fw = new FileWriter(location);
			fw.write(outFile);
			fw.flush();
			fw.close();
			RunCommand.runCommand(location);
		} catch (Exception e) {
			throw new Exception(e.getMessage()+" Routine Compare Error 001");
		}
	}
	
	public static String[] getSequence(String file1, String file2) {
		String[] working1 = new String[1000];
		String[] working2 = new String[1000];
		String[] file1List = getList(file1);
		String[] file2List = getList(file2);
		
		int last1 = 0;
		int last2bas = 0;
		int count1 = 0;
		while (last1 < file1List.length) {
			String str1 = file1List[last1++];
			int last2 = last2bas;
			while (last2 < file2List.length) {
				String str2 = file2List[last2++];
				if (str1.compareTo(str2) == 0) {
					working1[count1++] = last1+"^"+last2;
					last2bas = last2;
					break;
				}
			}
		}
		int last2 = 0;
		int last1bas = 0;
		int count2 = 0;
		while (last2 < file2List.length) {
			String str2 = file2List[last2++];
			last1 = last1bas;
			while (last1 < file1List.length) {
				String str1 = file1List[last1++];
				if (str2.compareTo(str1) == 0) {
					working2[count2++] = last1+"^"+last2;
					last1bas = last1;
					break;
				}
			}
		}
		int count;
		if (count1 > count2) {
			count = count1;
		}
		else {
			count = count2;
		}
		if (count == 0) {
			count1 = 1;
			working1[0] = "0^0";
			count = 1;
		}
		String[] result = new String[count];
		if (count1 > count2) {
			for (int i=0; i<count1; i++) {
				result[i] = working1[i];
			}
		}
		else {
			for (int i=0; i<count2; i++) {
				result[i] = working2[i];
			}
		}
		return result;
	}
	
	static String[] getList(String input) {
		String[] result = new String[1000];
		int count = 0;
		if (input.charAt(input.length()-1) != '\n') {
			input = input + "\r\n";
		}
		while (input.contains("\n")) {
			int loc = input.indexOf("\n");
			String str = input.substring(0,loc);
			input = input.substring(loc+1);
			result[count++] = str;
		}
		String[] finalString = new String[count];
		for (int i=0; i<count; i++) {
			finalString[i] = result[i];
		}
		return finalString;
	}

	static String startRemoved = "<span class=\"removed\" alt=\"removed line\">";
	static String startNew = "<span class=\"new\" alt=\"new line\">";
	static String startSame = "<span class=\"same\">";
	static String endSet = "</span>";

	static String buildPage(String[] text, String comparedToString, String serverName) {
		String outString ="<HTML>\n<HEAD>\n<title>"+routineName+" Routine Comparison for M-Editor in Eclipse from server '"+serverName+"'</title>\n<STYLE type=text/css>\n.removed {\n color: red;\n}\n.new {\n color: green;\n}\n.same {\n	color: navy;\n}\n.unexpected {\n	color: purple;\n}\n</STYLE>\n</HEAD>\n<BODY style=\"font-family:courier new\" style=\"font-size:small\">\n\n" +
				"Lines are indicated as <span class=\"removed\">NOT PRESENT</span>, <span class=\"new\">PRESENT IN</span> or <span class=\"same\">UNCHANGED</span> in the version of "+routineName+" CURRENTLY on the server '"+serverName+"' when compared to "+comparedToString+".<pre><code>\n";
		// finish any lines not already processed
		boolean found = false;
		String type = "";
		String line = "";
		for (int i=0; i<text.length; i++) {
			if (text[i].indexOf("< ") == 0) {
				if (type.compareTo("<") != 0){
					if (type.compareTo("") != 0) {
						outString = outString + endSet;
					}
					outString = outString + startRemoved;
					type = "<";
				}
				line = text[i].substring(2);
				line = markBadCharsOnLine(line,false);
//				line = setspaces(line);
				outString = outString + line +"\n";
			}
			if (text[i].indexOf("> ") == 0) {
				if (type.compareTo(">") != 0){
					if (type.compareTo("") != 0) {
						outString = outString + endSet;
					}
					outString = outString + startNew;
					type = ">";
				}
				line = text[i].substring(2);
				line = markBadCharsOnLine(line,false);
//				line = setspaces(line);
				outString = outString + line +"\n";
			}
			if (text[i].indexOf("  ") == 0) {
				if (type.compareTo("  ") != 0){
					if (type.compareTo("") != 0) {
						outString = outString + endSet;
					}
					outString = outString + startSame;
					type = "  ";
				}
				line = text[i].substring(2);
				line = markBadCharsOnLine(line,true);
//				line = setspaces(line);
				outString = outString + line +"\n";
			}
		}
		outString = outString + endSet;
		outString = outString + "</code></pre></BODY>\n";
		return outString;
	}
	
	
	static String[] lineList(String file1, String file2, String[] linNums) {
		String[] file1List = getList(file1);
		String[] file2List = getList(file2);
		String[] outFileList = new String[20000];
		int last1 = -1;
		int last2 = -1;
		int L1 = -1;
		int L2 = -1;
		int count = 0;
		for (int i=0; i<linNums.length; i++) {
			L1 = Integer.parseInt(MPiece.getPiece(linNums[i],"^"))-1;
			L2 = Integer.parseInt(MPiece.getPiece(linNums[i],"^",2))-1;
			count = listLine(last1,last2,L1,L2,file1List,file2List,outFileList);
			last1 = L1;
			last2 = L2;
		}
		// finish any lines not already processed
		for (L1++ ; L1<file1List.length; L1++) {
			outFileList[count++] = "< "+file1List[L1];	
		}
		for (L2++ ; L2<file2List.length; L2++) {
			outFileList[count++] = "> "+file2List[L2];
		}

		String[] resultString = new String[count];
		for (int i=0; i<count; i++) {
			resultString[i] = outFileList[i];
		}
		return resultString;
	}
	
	static int listLine(int oldL1, int oldL2, int currL1, int currL2, 
			String[] file1, String[] file2, String[] outFileList) {

		int outCount = 0;
		while (outFileList[outCount] != null) {
			outCount++;
		}
		for (int lval = oldL1+1; lval<currL1; lval++) {
			outFileList[outCount++]="< "+file1[lval];
		}
		for (int lval = oldL2+1; lval<currL2; lval++) {
			outFileList[outCount++] = "> "+file2[lval];
		}
		if (currL1 > oldL1) {
			if (currL2 > oldL2) {
				outFileList[outCount++] = "  "+ file1[currL1];
			}
		}
		return outCount;
	}
	
	static String setspaces(String input) {
		String output = "";
		while (input.length() > 0) {
			char charval = input.charAt(0);
			if (charval == '\t') {
				for (int i=0; i<4; i++) {
					output = output + "&nbsp;";
				}
			}
			else if (charval == ' ') {
				output = output + "&nbsp;";
			}
			else {
				output = output + charval;
			}
			input = input.substring(1);
		}
		return output;
	}
	
	public static String markBadChars(String codeToCheck) {
		String result = "";
		int loc = 0;
		while (codeToCheck.length()>0) {
			int loc1 = codeToCheck.indexOf("\r\n",loc);
			String str1 = codeToCheck.substring(loc,loc1+2);
			if (codeToCheck.length() > loc1+2)
				codeToCheck = codeToCheck.substring(loc1+2);
			else
				codeToCheck = "";
			result = result + markBadCharsOnLine(str1,true);
		}
		return result;
	}
	
	private static String markBadCharsOnLine(String inputLine, boolean markUnexpected) {
		// show spaces and/or tabs from end of lines
		int loc=inputLine.length()-2; // comes in with only a \r on end
		int count=0;
		char charVal = inputLine.charAt(loc);
		while (charVal <= ' ') {
			count++;
			loc--;
			if (loc < 0)
				break;
			charVal = inputLine.charAt(loc);
		}
		if (count > 0) {
			String endStr = inputLine.substring(loc+1);
			inputLine = inputLine.substring(0,loc+1);
			for (int i=0; i<count; i++) {
				charVal = endStr.charAt(i);
				if (charVal == ' ') {
					inputLine += markChar(charVal,markUnexpected);
				}
				else if (charVal == '\t') {
					inputLine += markChar(charVal,markUnexpected);
				}
				else if (charVal == 0) {
					inputLine += markChar(charVal,markUnexpected);
				}
				else {
					char controlChar = (char)(charVal+64);
					inputLine += markChar(controlChar,markUnexpected);
				}
			}
			inputLine = inputLine + "\r";
		}
		boolean tabFound = false;
		String outputLine = "";
		for (int i=0; i<inputLine.length()-1; i++) {
			charVal = inputLine.charAt(i);
			if (charVal < ' ') {
				if (charVal == '\t') {
					if (! tabFound) {
						tabFound = true;
						outputLine += "\t";
					}
					else {
						outputLine += markChar(charVal,markUnexpected);
					}
				}
				else if (charVal == 0) {
					outputLine += markChar(charVal,markUnexpected);
				}
				else {
					outputLine += markChar(((char)(charVal+64)),markUnexpected);
				}
			}
			else {
				outputLine = outputLine + charVal;
			}
		}
		return outputLine + "\r";
	}
	
	private static String markChar(char inputChar, boolean markUnexpected) {
		String result = "<span ";
		if (markUnexpected) {
			result = result + "class=\"unexpected\" ";
		}
		result = result + "alt=\"unexpected character\">&lt;";
		if (inputChar > ' ') {
			result = result + "ctrl-";
		}
		if (inputChar == ' ') {
			result = result + "space";
		}
		else if (inputChar == '\t') {
			result = result + "tab";
		}
		else {
			result = result + inputChar; 
		}
		result = result + "&gt;</span>";
		return result;
	}

}
