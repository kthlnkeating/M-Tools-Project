package gov.va.med.iss.mdebugger.views;

import gov.va.med.iss.mdebugger.util.MPiece;
import gov.va.med.iss.mdebugger.util.TextToArray;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/*
 *   070507 JLI Added variables beginning with % to list recognized during filtering
 *   
 *   071027 JLI Added ability to exclude variables from All Variables view by entering
 *              -XX where XX are the beginning characters of the variable names to be
 *              excluded from the listing (e.g., -XOB -XWB to exclude variables related
 *              to GUI connections).  This combined with variable names filters to only 
 *              show those desired should produce the view desired.
 *              A filter entry of    X -XOB -XWB %   would show only the variables which
 *              begin with % and X characters, but not those beginning with XOB and
 *              XWB.  The order of entry is not important.
 */
public class MDebuggerContentProvider implements IStructuredContentProvider {
	
	private String currDocument = "";
	protected String strFilter = "";
	protected String minusFilter = "";
	protected String filterList = "";
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	public void dispose() {
	}
	public Object[] getElements(Object parent) {
		List myList = TextToArray.convert(currDocument);
		filterList = "";
		minusFilter = "";
    	if (! strFilter.equalsIgnoreCase("")) {
    		String[] arrayFilter = filterArray(strFilter,false);
    		if ((arrayFilter.length > 0) && (!(arrayFilter[0].equalsIgnoreCase("")))) {
        		for (int i=0; i<myList.size(); i++) {
    				String str = (String)myList.get(i);
    				boolean isFound = false;				
        			for (int j=0; j<arrayFilter.length; j++) {
        				if (str.indexOf(arrayFilter[j]) == 0) {
            					isFound = true;
            					break;
            				}
        				}
        			if (!isFound){
        				myList.remove(i);
        				i--;
        			}
        		}
    		}
    	}

    	if (! strFilter.equalsIgnoreCase("")) {
        	String[] arrayFilter = filterArray(strFilter,true);
        	if ((arrayFilter.length > 0) && (!(arrayFilter[0].equalsIgnoreCase("")))) {
        		for (int i=0; i<myList.size(); i++) {
    				String str = (String)myList.get(i);
    				boolean isFound = false;				
        			for (int j=0; j<arrayFilter.length; j++) {
        				if (str.indexOf(arrayFilter[j]) == 0) {
            					isFound = true;
            					break;
            			}
        			}
        			if (isFound){
        				myList.remove(i);
        				i--;
        			}
        		}
        	}
    	}
		return myList.toArray();
	}
	
	public void setDocument(String input) {
		currDocument = input;
	}
	public void setFilter(String input) {
		strFilter = input;
	}
	
	/**
	 * filterArray extracts the filter items from an input String and
	 * returns them in a String array.  The input String may contain 
	 * items which are preceeded by a dash or minus sign ('-'), these
	 * values will be returned if the minusValues argument is true.  
	 * Other values in the string will be returned if the minusValues 
	 * argument is false.
	 * 
	 * @param input - a string of characters which are to be used 
	 * to filter a list of variables.  Variable names must begin
	 * with an alphabetic character or a percent sign ('%').  Subsequent
	 * characters must be either alphabetic or numeric.  If a variable
	 * name is preceeded by a negative sign it is returned only if the
	 * minusValues argument is true.  Variable names which are not
	 * preceeded by a minus sign are returned only if the minusValues
	 * argument is false.
	 * 
	 * @param minusValues - a boolean value which indicates whether 
	 * variable names preceeded by a minus sign ('-') are to be 
	 * returned (true) or not (false).  Variable names which are not
	 * preceeded by a minus sign ('-') will only be returned if the 
	 * value is false.
	 *  
	 * @return - A String array containing the variable names 
	 * extracted from the input string.
	 */
    private String[] filterArray(String input, boolean minusValues) {
    	int lengthMin = 1;
    	int length = filterLength(input,minusValues);
    	if (length > 0)
    		lengthMin = length;
    	String[] arrayFilter = new String[lengthMin];
    	for (int i=0; i<length; i++) {
    		if (minusValues) 
        		arrayFilter[i] = MPiece.getPiece(minusFilter,"^",i+1);
    		else
    			arrayFilter[i] = MPiece.getPiece(filterList,"^",i+1);
    	}
    	if (length == 0)
    		arrayFilter[0] = "";
    	return arrayFilter;
    }
    

   
	String alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	String alphanum = alpha + "0123456789";

    /**
     * Separates a string of input characters into valid filter characters,
     * where a valid filter is either a variable name or a variable name
     * preceeded by a minus character (to remove it from filtered variables)
     * 
     * A variable name can begin with an alphabetic character or a % character
     * Following the initial character, the variable name may contain alphabetic
     * and numeric characters.
     * 
     * @param input - string of input characters containing filter
     * @return 0 if no filters identified, number of filters otherwise
     */
    protected int filterLength(String input, boolean minusValues) {
    	input = input + " "; // seemed to need an ending space
    	int length = 0;
    	if (minusValues)
        	minusFilter = "";
    	else
    		filterList = "";
    	while (input.length() > 0) {
    		String percentStr = "";
    		String seenString = "";
    		boolean seenMinus = false;
			if (input.charAt(0) == '-') {
				seenMinus = true;
				seenString = "";
				input = deleteChar(input);
			}
			if (input.charAt(0) == '%')  {
				percentStr = "%";
				input = deleteChar(input);
			}
			if (alpha.indexOf(input.charAt(0)) > -1) {
				String returnVal = extractFilterString(input);
				seenString = MPiece.getPiece(returnVal,"^");
				input = MPiece.getPiece(returnVal,"^",2,400);
			}
			else
				input = deleteChar(input);
			if (percentStr.length()>0 || seenString.length() > 0) {
				if (seenMinus & minusValues) {
					minusFilter = addSeparator(minusFilter) + percentStr + seenString;
					length = length + 1;
				}
				else if (!(seenMinus)) {
					filterList = addSeparator(filterList) + percentStr + seenString;
					length = length + 1;
				}
			}
    	}
    	return length;
    }
    
    /**
     * adds a '^' separator to the end of the input string if it is not null
     * 
     * @param input - String
     * @return the input string with a concatenated '^' if the string was not null
     */
    protected String addSeparator(String input) {
    	if (!(input.length() == 0)) {
    		input = input + "^";
    	}
    	return input;
    }
    
    /**
     * 
     * @return
     */
    protected String extractFilterString(String input) {
    	String filter = "";
    	while (input.length() > 0) {
    		if (alphanum.indexOf(input.charAt(0)) == -1) {
    			input = deleteChar(input);
    			break;
    		}
    		filter = filter + input.charAt(0);
    		input = deleteChar(input);
    	}
    	return filter + "^" + input;
    }
    
    protected String deleteChar(String input) {
		if (input.length() > 1) {
			input = input.substring(1);
		}
		else
			input = "";
		return input;
    }
}
