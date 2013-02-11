package gov.va.med.iss.meditor.m;
/*
 * "The Java Developer's Guide to Eclipse"
 *   by Shavor, D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */
import java.util.StringTokenizer;

import org.eclipse.jface.text.formatter.IFormattingStrategy;

/**
 * The formatting strategy that transforms SQL keywords to upper case
 */
public class MWordStrategy implements IFormattingStrategy, IMSyntax {

	/**
	 * @see org.eclipse.jface.text.formatter.IFormattingStrategy#formatterStarts(String)
	 */
	public void formatterStarts(String initialIndentation) {
	}

	/**
	 * @see org.eclipse.jface.text.formatter.IFormattingStrategy#format(String, boolean, String, int[])
	 */
	public String format(
		String content,
		boolean isLineStart,
		String indentation,
		int[] positions) {

		return keyWordsToUpper(content);
	}

	/**
	 * Method keyWordsToUpper.
	 * @param content
	 * @return String
	 */
	private String keyWordsToUpper(String content) {
		StringTokenizer st = new StringTokenizer(content, " \n", true);
		String token = "";
		String newContent = "";
		boolean done;
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			done = false;
			for (int i = 0; i < IMSyntax.allWords.length; i++) {
				String[] sqlWords = (String[]) IMSyntax.allWords[i];
				for (int j = 0; j < sqlWords.length; j++) {
					if (token.equals(" ") | token.equals("\n"))
						break;
					if (token.toUpperCase().equals(sqlWords[j])) {
						token = token.toUpperCase();
						done = true;
						break;
					}
				}
				if (done = true)
					break;
			}
			newContent = newContent + token;
		}
		return newContent;
		
	} /**
							 * @see org.eclipse.jface.text.formatter.IFormattingStrategy#formatterStops()
							 */
	public void formatterStops() {
	}

}
