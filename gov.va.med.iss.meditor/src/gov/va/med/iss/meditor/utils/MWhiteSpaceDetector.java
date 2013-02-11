package gov.va.med.iss.meditor.utils;
/*
 * "The Java Developer's Guide to Eclipse"
 *   by Shavor, D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */
import org.eclipse.jface.text.rules.IWhitespaceDetector;

/**
 * A class that determines if a character is an SQL whitespace character
 */
public class MWhiteSpaceDetector implements IWhitespaceDetector {

	/**
	 * Whitespace test method.
	 * @see org.eclipse.jface.text.rules.IWhitespaceDetector#isWhitespace(char)
	 */
	public boolean isWhitespace(char c) {
		if (c == 9)
			MWordRule.fSyntaxType = IMTypes.COMMAND;
		return Character.isWhitespace(c);
	}

}
