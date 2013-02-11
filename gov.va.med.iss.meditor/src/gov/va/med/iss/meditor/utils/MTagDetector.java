/*
 * Created on Mar 27, 2004
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
public class MTagDetector implements IWordDetector {

	
	public boolean isWordStart(char c) {
		if ( MWordRule.fSyntaxType == IMTypes.TAG && Character.isLetter(c) )
		  return true;
		return false;
	}
	
	public boolean isWordPart(char c) {
		if ( MWordRule.fSyntaxType == IMTypes.TAG && !Character.isWhitespace(c) )
		  return true;
		return false;
	}

}
