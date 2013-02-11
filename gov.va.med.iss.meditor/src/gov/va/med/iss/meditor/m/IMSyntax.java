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
/**
 * SQL Syntax words (upper and lower cases).
 */
public interface IMSyntax {

	public static final String[] predicates =
		{
			"=",
			"<",
			">",
			"<=",
			">=",
			"!",
			"&",
			"+",
			"-",
			"*",
			"/",
			"%",
			"|",
			":",
			".",
			"[ ]",
			"::",
			 };
			 
	public static final String[] functions =
		{
            "$",
			"$A",
			"$ASCII",
			"$B",
			"$BREAK",
			"$D",
			"$DATA",
			"$E",
			"$EXTRACT",
			"$J",
			"$JOB",
			"$H",
			"$HOROLOG"
			};

	public static final String[] commandwords = {
		/*  
		"A",
		"B",
		*/
		"C",
		"CLOSE",
		"D",
		"DO",
		"E",
		"ELSE",
		"F",
		"FOR",
		"G",
		"GO",
		"H",
		"HANG",
		"I",
		"IF",
		"J",
		"JOB",
		"K",
		"KILL",
		"L",
		"L0CK",
		"M",
		"MERGE",
		"N",
		"NEW",
		"O",
		"OPEN",
		/*
		"P"
		*/
		"Q",
		"QUIT",
		"R",
		"READ",
		"S",
		"SET",
		/*
		"T",
		 */
		"U",
		"USE",
		"W",
		"WRITE",
		"XECUTE",
		"X"
		/*
		"Y",
		"Z"
		*/			
	};
	public static final String[] tagwords = {
		"a"
	};
	Object[] allWords =
		{ predicates, functions, commandwords, tagwords };

}
