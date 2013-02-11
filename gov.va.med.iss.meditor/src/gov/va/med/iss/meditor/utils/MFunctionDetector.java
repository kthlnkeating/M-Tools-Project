/*
 * Created on Mar 25, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gov.va.med.iss.meditor.utils;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * @author VHAISFIVEYJ
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MFunctionDetector implements IWordDetector {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	public boolean isWordStart(char c) {
		if ( c == '$' )
		  return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	public boolean isWordPart(char c) {
		if ( (c != ' ') && (c != '(') && (c != ',') && (c != ')') )
		  return true;
		return false;
	}

	public static void main(String[] args) {
	}
}
