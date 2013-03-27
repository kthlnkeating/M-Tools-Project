package gov.va.med.iss.mdebugger;

public class DebuggerUtils {
	
	public static String checkQuotes(String str) {
			String numstr = "0123456789.";
			int sawPeriod = 0;
			if (str.length() == 0) {
				return "\"\"";
			}
			char char1 = str.charAt(0);
			if ((char1 == '"') && (! (str.length() == 1)))
				return str;  // if leading quote user handled as text
			if (char1 == '0')
				return doQuotes(str);
			char1 = str.charAt(str.length()-1);
			if (char1 == '.')
				return doQuotes(str);
			for (int i=0; i<str.length(); i++) {
				if (numstr.indexOf(str.charAt(i)) < 0) {
					return doQuotes(str);
				}
				if (str.charAt(i) == '.') {
					sawPeriod += 1;
					if (sawPeriod > 1) {
						return doQuotes(str);
					}
				}
			}
			return str;
		}
		
		private static String doQuotes(String str) {
			String returnStr = "\"";
			for (int i=0; i<str.length(); i++) {
				returnStr = returnStr + str.substring(i,i+1);
				if (str.charAt(i) == '"')
					returnStr = returnStr + "\"";
			}
			returnStr = returnStr + "\"";
			return returnStr;
		}
}
