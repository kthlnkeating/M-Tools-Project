//---------------------------------------------------------------------------
// Copyright 2013 PwC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//---------------------------------------------------------------------------

package com.pwc.us.rgi.m.struct;

import java.util.HashMap;
import java.util.Map;

public class MError {
	private static enum Severity {
		FATAL('F'),
		STANDARD('S'),
		WARNING('W'),
		INFO('I');
		
		private char abbreviation;
		
		private Severity(char abbreviation) {
			this.abbreviation = abbreviation;
		}
		
		public char getOneCharAbbr() {
			return this.abbreviation;
		}
	}
	
	public static final int ERR_UNDEFINED_COMMAND = 1;
	public static final int ERR_UNMATCHED_PARANTHESIS = 5;
	public static final int ERR_UNMATCHED_QUOTATION = 6;
	public static final int ERR_GENERAL_SYNTAX = 21;
	public static final int ERR_NULL_LINE = 42;
	public static final int ERR_BLOCK_STRUCTURE = 51;
	
	public static final int ERR_NO_LINES = 800;
	public static final int ERR_NO_DO_BLOCK = 801;
	public static final int ERR_ROUTINE_PATH = 802;
	public static final int ERR_UNKNOWN_INTRINSIC_VARIABLE = 1000;
	public static final int ERR_UNKNOWN_INTRINSIC_FUNCTION = 1001;
	public static final int ERR_WRONGARG_INTRINSIC_FUNCTION	= 1002;

	public static final int ERR_DEAD_CODE = 1100;
	
	private static class CodeDetail {
		public Severity severity;
		public String text;
		
		public CodeDetail(Severity severity, String text) {
			this.severity = severity;
			this.text = text;
		}
		
		public String getMessage() {
			return this.severity.getOneCharAbbr() + " - " + this.text;
		}
		
		public boolean isFatal() {
			return this.severity == Severity.FATAL;
		}
	}
	
	private static final Map<Integer, CodeDetail> CODES;
	static {
		CODES = new HashMap<Integer, CodeDetail>();
		CODES.put(1, new CodeDetail(Severity.FATAL, "UNDEFINED COMMAND (rest of line not checked)."));
		CODES.put(2, new CodeDetail(Severity.FATAL, "Non-standard (Undefined) 'Z' command."));
		CODES.put(3, new CodeDetail(Severity.FATAL, "Undefined Function."));
		CODES.put(4, new CodeDetail(Severity.FATAL, "Undefined Special Variable."));
		CODES.put(5, new CodeDetail(Severity.FATAL, "Unmatched Parenthesis."));
		CODES.put(6, new CodeDetail(Severity.FATAL, "Unmatched Quotation Marks."));
		CODES.put(7, new CodeDetail(Severity.FATAL, "ELSE Command followed by only one space."));
		CODES.put(8, new CodeDetail(Severity.FATAL, "FOR Command did not contain '='."));
		CODES.put(9, new CodeDetail(Severity.INFO, "QUIT Command followed by only one space."));
		CODES.put(10, new CodeDetail(Severity.FATAL, "Unrecognized argument in SET command."));
		CODES.put(11, new CodeDetail(Severity.WARNING, "Invalid local variable name."));
		CODES.put(12, new CodeDetail(Severity.WARNING, "Invalid global variable name."));
		CODES.put(13, new CodeDetail(Severity.WARNING, "Blank(s) at end of line."));
		CODES.put(14, new CodeDetail(Severity.FATAL, "Call to missing label '|' in this routine."));
		CODES.put(15, new CodeDetail(Severity.WARNING, "Duplicate label. (M57)"));
		CODES.put(16, new CodeDetail(Severity.FATAL, "Error in pattern code."));
		CODES.put(17, new CodeDetail(Severity.WARNING, "First line label NOT routine name."));
		CODES.put(18, new CodeDetail(Severity.WARNING, "Line contains a CONTROL (non-graphic) character."));
		CODES.put(19, new CodeDetail(Severity.STANDARD, "Line is longer than 245 bytes."));
		CODES.put(20, new CodeDetail(Severity.STANDARD, "View command used."));
		CODES.put(ERR_GENERAL_SYNTAX, new CodeDetail(Severity.FATAL, "General Syntax Error."));
		CODES.put(22, new CodeDetail(Severity.STANDARD, "Exclusive Kill."));
		CODES.put(23, new CodeDetail(Severity.STANDARD, "Unargumented Kill."));
		CODES.put(24, new CodeDetail(Severity.STANDARD, "Kill of an unsubscripted global."));
		CODES.put(25, new CodeDetail(Severity.STANDARD, "Break command used."));
		CODES.put(26, new CodeDetail(Severity.STANDARD, "Exclusive or Unargumented NEW command."));
		CODES.put(27, new CodeDetail(Severity.STANDARD, "$View function used."));
		CODES.put(28, new CodeDetail(Severity.STANDARD, "Non-standard $Z special variable used."));
		CODES.put(29, new CodeDetail(Severity.STANDARD, "'Close' command should be invoked through 'D ^%ZISC'."));
		CODES.put(30, new CodeDetail(Severity.STANDARD, "LABEL+OFFSET syntax."));
		CODES.put(31, new CodeDetail(Severity.STANDARD, "Non-standard $Z function used."));
		CODES.put(32, new CodeDetail(Severity.STANDARD, "'HALT' command should be invoked through 'G ^XUSCLEAN'."));
		CODES.put(33, new CodeDetail(Severity.STANDARD, "Read command doesn't have a timeout."));
		CODES.put(34, new CodeDetail(Severity.STANDARD, "'OPEN' command should be invoked through ^%ZIS."));
		CODES.put(35, new CodeDetail(Severity.STANDARD, "Routine exceeds SACC maximum size of 20000 (|)."));
		CODES.put(36, new CodeDetail(Severity.STANDARD, "Should use 'TASKMAN' instead of 'JOB' command."));
		CODES.put(37, new CodeDetail(Severity.FATAL, "Label is not valid."));
		CODES.put(38, new CodeDetail(Severity.FATAL, "Call to this |"));
		CODES.put(39, new CodeDetail(Severity.STANDARD, "Kill of a protected variable (|)."));
		CODES.put(40, new CodeDetail(Severity.STANDARD, "Space where a command should be."));
		CODES.put(41, new CodeDetail(Severity.INFO, "Star or pound READ used."));
		CODES.put(42, new CodeDetail(Severity.WARNING, "Null line (no commands or comment)."));
		CODES.put(43, new CodeDetail(Severity.FATAL, "Invalid or wrong number of arguments to a function."));
		CODES.put(44, new CodeDetail(Severity.STANDARD, "2nd line of routine violates the SAC."));
		CODES.put(45, new CodeDetail(Severity.STANDARD, "Set to a '%' global."));
		CODES.put(46, new CodeDetail(Severity.FATAL, "Quoted string not followed by a separator."));
		CODES.put(47, new CodeDetail(Severity.STANDARD, "Lowercase command(s) used in line."));
		CODES.put(48, new CodeDetail(Severity.FATAL, "Missing argument to a command post-conditional."));
		CODES.put(49, new CodeDetail(Severity.FATAL, "Command missing an argument."));
		CODES.put(50, new CodeDetail(Severity.STANDARD, "Extended reference."));
		CODES.put(ERR_BLOCK_STRUCTURE, new CodeDetail(Severity.FATAL, "Block structure mismatch."));
		CODES.put(52, new CodeDetail(Severity.FATAL, "Reference to routine '^|'. That isn't in this UCI."));
		CODES.put(53, new CodeDetail(Severity.FATAL, "Bad Number."));
		CODES.put(54, new CodeDetail(Severity.STANDARD, "Access to SSVN's restricted to Kernel."));
		CODES.put(55, new CodeDetail(Severity.STANDARD, "Violates VA programming standards."));
		CODES.put(56, new CodeDetail(Severity.STANDARD, "Patch number '|' missing from second line."));
		CODES.put(57, new CodeDetail(Severity.STANDARD, "Lower/Mixed case Variable name used."));
		CODES.put(58, new CodeDetail(Severity.STANDARD, "Routine code exceeds SACC maximum size of 15000 (|)."));
		CODES.put(59, new CodeDetail(Severity.FATAL, "Bad WRITE syntax."));
		CODES.put(60, new CodeDetail(Severity.STANDARD, "Lock missing Timeout."));
		CODES.put(61, new CodeDetail(Severity.STANDARD, "Non-Incremental Lock."));
		CODES.put(62, new CodeDetail(Severity.STANDARD, "First line of routine violates the SAC."));
		CODES.put(63, new CodeDetail(Severity.FATAL, "GO or DO mismatch from block structure (M45).	}"));

		CODES.put(ERR_NO_LINES, new CodeDetail(Severity.FATAL, "No lines in the routine."));
		CODES.put(ERR_NO_DO_BLOCK, new CodeDetail(Severity.WARNING, "Empty do block."));
		CODES.put(ERR_ROUTINE_PATH, new CodeDetail(Severity.FATAL, "Error reading routine from the specified path."));
		CODES.put(ERR_UNKNOWN_INTRINSIC_VARIABLE, new CodeDetail(Severity.FATAL, "Unknown intrinsic variable."));
		CODES.put(ERR_UNKNOWN_INTRINSIC_FUNCTION, new CodeDetail(Severity.FATAL, "Unknown intrinsic function."));
		CODES.put(ERR_WRONGARG_INTRINSIC_FUNCTION, new CodeDetail(Severity.FATAL, "Wrong number of arguments for intrinsic function."));
		CODES.put(ERR_DEAD_CODE, new CodeDetail(Severity.STANDARD, "Code is unreachable."));
	}
	
	private int code;

	public MError(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public String getTitle() {
		return "";
	}
	
	public String getText() {
		CodeDetail cd = CODES.get(this.code);
		String text = (cd == null ? "??" : cd.getMessage());
		String title = this.getTitle();
		return title + text;
	}
	
	public boolean isFatal() {
		CodeDetail cd = CODES.get(this.code);
		if (cd != null) {
			return cd.isFatal();
		}
		return false;
	}
}
