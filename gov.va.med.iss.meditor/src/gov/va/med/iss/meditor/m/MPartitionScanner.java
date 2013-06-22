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
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;

/**
 * The MPartitionScanner is a RulesBasedPartitionScanner.  The M document 
 * partitions are computed dynamically as events signal that the document has 
 * changed. The document partitions are based on tokens that represent comments
 * and M code sections.
 */
public class MPartitionScanner extends RuleBasedPartitionScanner {
	public final static String M_COMMENT= "m_comment"; 
	public final static String M_MULTILINE_COMMENT= "m_multiline_comment"; 
	public final static String M_CODE= "m_code"; 

	/**
	 * Constructor for MPartitionScanner. Creates rules to parse comment 
	 * partitions in an M document. In the constructor, is defined the entire 
	 * set of rules used to parse the M document, in an instance of an 
	 * IPredicateRule. The coonstructor calls setPredicateRules method which
	 * associates the rules to the scanner and makes the document ready for 
	 * parsing.
	 */
	public MPartitionScanner() {
		super();
	}

}
