/*
 * Created on Mar 27, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gov.va.med.iss.meditor.utils;

import org.eclipse.jface.text.rules.WordRule;
// import java.util.HashMap;
// import java.util.Map;

import gov.va.med.iss.meditor.utils.MColorProvider;
// import gov.va.med.iss.meditor.m.MCodeScanner;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.TextAttribute;
import gov.va.med.iss.meditor.MEditorPlugin;


/**
 * @author VHAISFIVEYJ
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MWordRule extends WordRule {

    public static int fSyntaxType;
	private StringBuffer fBuffer = new StringBuffer();

	/**
	 * Creates a rule which, with the help of an word detector, will return the token
	 * associated with the detected word. If no token has been associated, the scanner 
	 * will be rolled back and an undefined token will be returned in order to allow 
	 * any subsequent rules to analyze the characters.
	 *
	 * @param detector the word detector to be used by this rule, may not be <code>null</code>
	 *
	 * @see #addWord
	 */
	public MWordRule(IWordDetector detector) {
		this(detector, Token.UNDEFINED);
	}

	/**
	 * Creates a rule which, with the help of an word detector, will return the token
	 * associated with the detected word. If no token has been associated, the
	 * specified default token will be returned.
	 *
	 * @param detector the word detector to be used by this rule, may not be <code>null</code>
	 * @param defaultToken the default token to be returned on success 
	 *		if nothing else is specified, may not be <code>null</code>
	 *
	 * @see #addWord
	 */
	public MWordRule(IWordDetector detector, IToken defaultToken) {
		super(detector, defaultToken);
	}

	/**
	 * Adds a word and the token to be returned if it is detected.
	 *
	 * @param word the word this rule will search for, may not be <code>null</code>
	 * @param token the token to be returned if the word has been found, may not be <code>null</code>
	 */
	public void addWord(String word, IToken token) {
		Assert.isNotNull(word);
		Assert.isNotNull(token);		
	
		fWords.put(word, token);
	}
	
	/**
	 * Sets a column constraint for this rule. If set, the rule's token
	 * will only be returned if the pattern is detected starting at the 
	 * specified column. If the column is smaller then 0, the column
	 * constraint is considered removed.
	 *
	 * @param column the column in which the pattern starts
	 */
	public void setColumnConstraint(int column) {
		if (column < 0)
			column= UNDEFINED;
		fColumn = column;
	}
	
	/*
	 * @see IRule#evaluate(ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner) {
		int c = scanner.read();
//		int iColumn = scanner.getColumn();
		boolean fSawEOL = false;
		if (scanner.getColumn() == 1) {
			fSyntaxType = IMTypes.TAG; 
		}
		if (c == 9) {
			fSyntaxType = IMTypes.COMMAND;
		}
		if (c == 32 && fSyntaxType == IMTypes.VARSEEN) {
			fSyntaxType = IMTypes.COMMAND;
		}
		if (fDetector.isWordStart((char) c)) {
			if ( (char)c == '$' ) {
				fSyntaxType = IMTypes.FUNCTION;
			}
			if (fColumn == UNDEFINED || (fColumn == scanner.getColumn() - 1)) {
				
				fBuffer.setLength(0);
				do {
					fBuffer.append((char) c);
					c= scanner.read();
				} while (c != ICharacterScanner.EOF && c != 10 && c != 13 && fDetector.isWordPart((char) c));
				scanner.unread();
				if ( c == 10 || c == 13)
					fSawEOL = true;
				
				if (fColumn == 0) {   // line TAG
					IToken token = (IToken) fWords.get(fBuffer.toString());
					if (token != null) {
						fWords.remove(fBuffer.toString());
					}
					MColorProvider provider =
					   MEditorPlugin.getDefault().getColorProvider();
					addWord(fBuffer.toString(), new Token(new TextAttribute(
						provider.getPreferenceColor(MEditorPlugin.P_TAGS_COLOR))));
					fSyntaxType = IMTypes.COMMAND;
				}
				
				else if (fSyntaxType == IMTypes.COMMAND) {
					IToken token = (IToken) fWords.get(fBuffer.toString());
					if (token != null) {
						fWords.remove(fBuffer.toString());
					}
					MColorProvider provider =
						MEditorPlugin.getDefault().getColorProvider();
					addWord(fBuffer.toString(), new Token(new TextAttribute(
						provider.getPreferenceColor(MEditorPlugin.P_COMMAND_COLOR)))); //P_OPS_COLOR))));
					fSyntaxType = IMTypes.VARIABLE;
					//  check for commands possibly followed by two spaces 
					if (fBuffer.charAt(0) == 'F' || fBuffer.charAt(0) == 'D' || fBuffer.charAt(0) == 'E') {
						c = scanner.read();
						if ((char)c == ' ') {
							c = scanner.read();
							if ((char)c == ' ') {
								fSyntaxType = IMTypes.COMMAND; 
							}
							scanner.unread();
						}
						scanner.unread();
					}
				}
				else {
					IToken token = (IToken) fWords.get(fBuffer.toString());
					if (token != null) {
						fWords.remove(fBuffer.toString());
					}
					if (fSyntaxType == IMTypes.FUNCTION) {
						MColorProvider provider =
							MEditorPlugin.getDefault().getColorProvider();
						addWord(fBuffer.toString(), new Token(new TextAttribute(
							provider.getPreferenceColor(MEditorPlugin.P_FUNCS_COLOR))));
					}
					else {
						MColorProvider provider =
							MEditorPlugin.getDefault().getColorProvider();
						addWord(fBuffer.toString(), new Token(new TextAttribute(
							provider.getPreferenceColor(MEditorPlugin.P_VARS_COLOR))));
					}
					fSyntaxType = IMTypes.VARSEEN;
				}
				
				IToken token= (IToken) fWords.get(fBuffer.toString());
				if (token != null) {
					if (fSawEOL)
					  fSyntaxType = IMTypes.COMMAND;
					return token;
				}
					
				if (fDefaultToken.isUndefined())
					unreadBuffer(scanner);
					
				return fDefaultToken;
			}
		}
		
		scanner.unread();
		return Token.UNDEFINED;
	}
	
	/**
	 * Returns the characters in the buffer to the scanner.
	 *
	 * @param scanner the scanner to be used
	 */
	protected void unreadBuffer(ICharacterScanner scanner) {
		for (int i= fBuffer.length() - 1; i >= 0; i--)
			scanner.unread();
	}

}
