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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.text.rules.WordPatternRule;
import org.eclipse.swt.SWT;

import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.utils.MColorProvider;
import gov.va.med.iss.meditor.utils.MFunctionDetector;
import gov.va.med.iss.meditor.utils.MOpDetector;
import gov.va.med.iss.meditor.utils.MTagDetector;
import gov.va.med.iss.meditor.utils.MVarDetector;
import gov.va.med.iss.meditor.utils.MWhiteSpaceDetector;
import gov.va.med.iss.meditor.utils.MWordRule;
//import com.ibm.lab.soln.sqleditor.utils.SQLWordDetector;

/**
 * The MCodeScanner is a RuleBaseScanner.This class finds M comments and
 * keywords, as the user edits the document. It is "programmed" with a sequence
 * of rules that evaluates and returns the offset and the length of the last
 * found token.
 */
public class MCodeScanner extends RuleBasedScanner implements IMSyntax{
	public static IToken keyword;
	public static IToken type;
	public static IToken string;
	public static IToken comment;
	public static IToken opers;
	public static IToken funcs;
	public static IToken tags;
	public static IToken other;
	public static IToken variables;
	public static IToken commands;

	/**
	 * Constructor for MCodeScanner.
	 * The MCodeScanner, is a RuleBaseScanner. The code scanner creates tokens 
	 * for keywords, types, and constants. The token is constructed with a 
	 * TextAttribute. The TextAttribute is constructed with a color and font. 
	 * A list of rules with the corresponding token are created. The method ends
	 * with setting the scanner’s set of rules
	 */
	public MCodeScanner() {
		setTokens();

		setDefaultReturnToken(other);

		List rules = new ArrayList();
		
		// check for actual commands on line
//		rules.add(new )

		// Add rule for single line comments.
		rules.add(new EndOfLineRule(";", comment));

		// Add rule for strings and character constants.
		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		

		MWordRule tagRule = new MWordRule(new MTagDetector(), other);
		tagRule.addWord("a", tags);
		tagRule.setColumnConstraint(0);
		rules.add(tagRule);

		MWordRule functionRule = new MWordRule(new MFunctionDetector(), other);
		functionRule.addWord("$", funcs);
		rules.add(functionRule);

		MWordRule opsRule = new MWordRule(new MOpDetector(), other);
		opsRule.addWord("a", opers);
		rules.add(opsRule);
		
		MWordRule variableRule = new MWordRule(new MVarDetector(), other);
		variableRule.addWord("a", variables);
		rules.add(variableRule);

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new MWhiteSpaceDetector()));

		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}

	public static void setTokens() {
		MColorProvider provider =
			MEditorPlugin.getDefault().getColorProvider();
		keyword =
			new Token(
				new TextAttribute(
					provider.getPreferenceColor(MEditorPlugin.P_KEYWORD_COLOR),
					provider.getPreferenceColor(MEditorPlugin.P_BACKGROUND_COLOR),
					SWT.BOLD));
		type =
			new Token(
				new TextAttribute(
					provider.getPreferenceColor(MEditorPlugin.P_TYPE_COLOR),
					provider.getPreferenceColor(MEditorPlugin.P_BACKGROUND_COLOR),
					SWT.BOLD));
		string =
			new Token(
				new TextAttribute(provider.getPreferenceColor(MEditorPlugin.P_STRING_COLOR)));
		comment =
			new Token(
				new TextAttribute(
					provider.getPreferenceColor(MEditorPlugin.P_COMMENT_COLOR)));
        opers = new Token(
                             new TextAttribute(
                                provider.getPreferenceColor(MEditorPlugin.P_OPS_COLOR),
                                provider.getPreferenceColor(MEditorPlugin.P_BACKGROUND_COLOR),
                                SWT.BOLD));
        funcs = new Token(
        						new TextAttribute(
        							provider.getPreferenceColor(MEditorPlugin.P_FUNCS_COLOR)));
        tags = new Token(
        						new TextAttribute(
        							provider.getPreferenceColor(MEditorPlugin.P_TAGS_COLOR)));
		variables = new Token(
								new TextAttribute(
									provider.getPreferenceColor(MEditorPlugin.P_VARS_COLOR)));
		commands = new Token(
								new TextAttribute(
									provider.getPreferenceColor(MEditorPlugin.P_COMMAND_COLOR)));
		other = new Token(
					new TextAttribute(provider.getPreferenceColor(MEditorPlugin.P_DEFAULT_COLOR)));
	}
}
