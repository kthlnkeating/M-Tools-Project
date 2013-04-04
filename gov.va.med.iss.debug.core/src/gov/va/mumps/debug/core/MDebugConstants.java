package gov.va.mumps.debug.core;

/**
 * Constants for the M debugger.
 */
public interface MDebugConstants {
	
	/**
	 * Unique identifier for the M debug model (value 
	 * <code>gov.va.mumps.debug</code>).
	 */
	public static final String M_DEBUG_MODEL = "gov.va.mumps.debug";// org.eclipse.debug.examples.pda";
	
	/**
	 * Name of the string substitution variable that resolves to the
	 * location of a local Perl executable (value <code>perlExecutable</code>).
	 */
	public static final String ID_PERL_EXECUTABLE = "perlExecutable";
	/**
	 * Launch configuration key. Value is a path to a perl
	 * program. The path is a string representing a full path
	 * to a perl program in the workspace. 
	 */
	public static final String ATTR_PDA_PROGRAM = M_DEBUG_MODEL + ".ATTR_PDA_PROGRAM"; //TODO: comment out
	
	public static final String ATTR_M_ENTRY_TAG = M_DEBUG_MODEL + ".ATTR_M_ROUTINE";
}
