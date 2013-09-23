package gov.va.mumps.debug.ui.terminal;

import java.io.IOException;

public class Emulator {
	/** This is a character processing state: Initial state. */
	private static final int ANSISTATE_INITIAL = 0;

	/** This is a character processing state: We've seen an escape character. */
	private static final int ANSISTATE_ESCAPE = 1;

	/**
	 * This is a character processing state: We've seen a '[' after an escape
	 * character. Expecting a parameter character or a command character next.
	 */
	private static final int ANSISTATE_EXPECTING_PARAMETER_OR_COMMAND = 2;

	/**
	 * This is a character processing state: We've seen a ']' after an escape
	 * character. We are now expecting an operating system command that
	 * reprograms an intelligent terminal.
	 */
	private static final int ANSISTATE_EXPECTING_OS_COMMAND = 3;

	/**
	 * This field holds the current state of the Finite TerminalState Automaton (FSA)
	 * that recognizes ANSI escape sequences.
	 *
	 * @see #processNewText()
	 */
	private int ansiState = ANSISTATE_INITIAL;

	/**
	 * This field hold the saved absolute line number of the cursor when
	 * processing the "ESC 7" and "ESC 8" command sequences.
	 */
	private int savedCursorLine = 0;

	/**
	 * This field hold the saved column number of the cursor when processing the
	 * "ESC 7" and "ESC 8" command sequences.
	 */
	private int savedCursorColumn = 0;

	/**
	 * This field holds an array of StringBuffer objects, each of which is one
	 * parameter from the current ANSI escape sequence. For example, when
	 * parsing the escape sequence "\e[20;10H", this array holds the strings
	 * "20" and "10".
	 */
	private final StringBuffer[] ansiParameters = new StringBuffer[16];

	/**
	 * This field holds the OS-specific command found in an escape sequence of
	 * the form "\e]...\u0007".
	 */
	private final StringBuffer ansiOsCommand = new StringBuffer(128);

	/**
	 * This field holds the index of the next unused element of the array stored
	 * in field {@link #ansiParameters}.
	 */
	private int nextAnsiParameter = 0;

	boolean fCrAfterNewLine;
	/**
	 * The constructor.
	 */
	public Emulator() {
		for (int i = 0; i < ansiParameters.length; ++i) {
			ansiParameters[i] = new StringBuffer();
		}
	}

	/**
	 * This method scans the newly received text, processing ANSI control
	 * characters and escape sequences and displaying normal text.
	 * @throws IOException
	 */
	public String processText(String input) throws IOException {
		StringBuilder sb = new StringBuilder();

		int n = input.length();
		for (int index=0; index<n; ++index) {
			char character = input.charAt(index);

			switch (ansiState) {
			case ANSISTATE_INITIAL:
				switch (character) {
				case '\u0000':
					break; // NUL character. Ignore it.

				case '\u0007':
					processBEL(); // BEL (Control-G)
					break;

				case '\b':
					processBackspace(); // Backspace
					break;

				case '\t':
					processTab(); // Tab.
					break;

				case '\n':
					sb.append(character);
					break;

				case '\r':
					sb.append(character);
					break;

				case '\u001b':
					ansiState = ANSISTATE_ESCAPE; // Escape.
					break;

				default:
					sb.append(character);
					break;
				}
				break;

			case ANSISTATE_ESCAPE:
				// We've seen an escape character. Here, we process the character
				// immediately following the escape.

				switch (character) {
				case '[':
					ansiState = ANSISTATE_EXPECTING_PARAMETER_OR_COMMAND;
					nextAnsiParameter = 0;

					// Erase the parameter strings in preparation for optional
					// parameter characters.

					for (int i = 0; i < ansiParameters.length; ++i) {
						ansiParameters[i].delete(0, ansiParameters[i].length());
					}
					break;

				case ']':
					ansiState = ANSISTATE_EXPECTING_OS_COMMAND;
					ansiOsCommand.delete(0, ansiOsCommand.length());
					break;

				case '7':
					// Save cursor position and character attributes

					ansiState = ANSISTATE_INITIAL;
					break;

				case '8':
					// Restore cursor and attributes to previously saved
					// position

					ansiState = ANSISTATE_INITIAL;
					moveCursor(savedCursorLine, savedCursorColumn);
					break;

				case 'c':
					// Reset the terminal
					ansiState = ANSISTATE_INITIAL;
					resetTerminal();
					break;

				default:
					ansiState = ANSISTATE_INITIAL;
					break;
				}
				break;

			case ANSISTATE_EXPECTING_PARAMETER_OR_COMMAND:
				// Parameters can appear after the '[' in an escape sequence, but they
				// are optional.

				if (character == '@' || (character >= 'A' && character <= 'Z')
						|| (character >= 'a' && character <= 'z')) {
					ansiState = ANSISTATE_INITIAL;
					processAnsiCommandCharacter(character);
				} else {
					processAnsiParameterCharacter(character);
				}
				break;

			case ANSISTATE_EXPECTING_OS_COMMAND:
				// A BEL (\u0007) character marks the end of the OSC sequence.

				if (character == '\u0007') {
					ansiState = ANSISTATE_INITIAL;
					processAnsiOsCommand();
				} else {
					ansiOsCommand.append(character);
				}
				break;

			default:
				// This should never happen! If it does happen, it means there is a
				// bug in the FSA. For robustness, we return to the initial
				// state.

				ansiState = ANSISTATE_INITIAL;
				break;
			}
		}
		return sb.toString();
	}

	private void resetTerminal() {
	}
	/**
	 * This method is called when we have parsed an OS Command escape sequence.
	 * The only one we support is "\e]0;...\u0007", which sets the terminal
	 * title.
	 */
	private void processAnsiOsCommand() {
		if (ansiOsCommand.charAt(0) != '0' || ansiOsCommand.charAt(1) != ';') {
			return;
		}
	}

	/**
	 * This method dispatches control to various processing methods based on the
	 * command character found in the most recently received ANSI escape
	 * sequence. This method only handles command characters that follow the
	 * ANSI standard Control Sequence Introducer (CSI), which is "\e[...", where
	 * "..." is an optional ';'-separated sequence of numeric parameters.
	 * <p>
	 */
	private void processAnsiCommandCharacter(char ansiCommandCharacter) {
		// If the width or height of the terminal is ridiculously small (one line or
		// column or less), don't even try to process the escape sequence. This avoids
		// throwing an exception (SPR 107450). The display will be messed up, but what
		// did you user expect by making the terminal so small?

		switch (ansiCommandCharacter) {
		case '@':
			// Insert character(s).
			processAnsiCommand_atsign();
			break;

		case 'A':
			// Move cursor up N lines (default 1).
			processAnsiCommand_A();
			break;

		case 'B':
			// Move cursor down N lines (default 1).
			processAnsiCommand_B();
			break;

		case 'C':
			// Move cursor forward N columns (default 1).
			processAnsiCommand_C();
			break;

		case 'D':
			// Move cursor backward N columns (default 1).
			processAnsiCommand_D();
			break;

		case 'E':
			// Move cursor to first column of Nth next line (default 1).
			processAnsiCommand_E();
			break;

		case 'F':
			// Move cursor to first column of Nth previous line (default 1).
			processAnsiCommand_F();
			break;

		case 'G':
			// Move to column N of current line (default 1).
			processAnsiCommand_G();
			break;

		case 'H':
			// Set cursor Position.
			processAnsiCommand_H();
			break;

		case 'J':
			// Erase part or all of display. Cursor does not move.
			processAnsiCommand_J();
			break;

		case 'K':
			// Erase in line (cursor does not move).
			processAnsiCommand_K();
			break;

		case 'L':
			// Insert line(s) (current line moves down).
			processAnsiCommand_L();
			break;

		case 'M':
			// Delete line(s).
			processAnsiCommand_M();
			break;

		case 'm':
			// Set Graphics Rendition (SGR).
			processAnsiCommand_m();
			break;

		case 'n':
			// Device Status Report (DSR).
			processAnsiCommand_n();
			break;

		case 'P':
			// Delete character(s).
			processAnsiCommand_P();
			break;

		case 'S':
			// Scroll up.
			// Emacs, vi, and GNU readline don't seem to use this command, so we ignore
			// it for now.
			break;

		case 'T':
			// Scroll down.
			// Emacs, vi, and GNU readline don't seem to use this command, so we ignore
			// it for now.
			break;

		case 'X':
			// Erase character.
			// Emacs, vi, and GNU readline don't seem to use this command, so we ignore
			// it for now.
			break;

		case 'Z':
			// Cursor back tab.
			// Emacs, vi, and GNU readline don't seem to use this command, so we ignore
			// it for now.
			break;

		default:
			break;
		}
	}

	/**
	 * This method makes room for N characters on the current line at the cursor
	 * position. Text under the cursor moves right without wrapping at the end
	 * of the line.
	 */
	private void processAnsiCommand_atsign() {
		getAnsiParameter(0);
	}

	/**
	 * This method moves the cursor up by the number of lines specified by the
	 * escape sequence parameter (default 1).
	 */
	private void processAnsiCommand_A() {
		moveCursorUp(getAnsiParameter(0));
	}

	/**
	 * This method moves the cursor down by the number of lines specified by the
	 * escape sequence parameter (default 1).
	 */
	private void processAnsiCommand_B() {
		moveCursorDown(getAnsiParameter(0));
	}

	/**
	 * This method moves the cursor forward by the number of columns specified
	 * by the escape sequence parameter (default 1).
	 */
	private void processAnsiCommand_C() {
		moveCursorForward(getAnsiParameter(0));
	}

	/**
	 * This method moves the cursor backward by the number of columns specified
	 * by the escape sequence parameter (default 1).
	 */
	private void processAnsiCommand_D() {
		moveCursorBackward(getAnsiParameter(0));
	}

	/**
	 * This method moves the cursor to the first column of the Nth next line,
	 * where N is specified by the ANSI parameter (default 1).
	 */
	private void processAnsiCommand_E() {
	}

	/**
	 * This method moves the cursor to the first column of the Nth previous
	 * line, where N is specified by the ANSI parameter (default 1).
	 */
	private void processAnsiCommand_F() {
	}

	/**
	 * This method moves the cursor within the current line to the column
	 * specified by the ANSI parameter (default is column 1).
	 */
	private void processAnsiCommand_G() {
	}

	/**
	 * This method sets the cursor to a position specified by the escape
	 * sequence parameters (default is the upper left corner of the screen).
	 */
	private void processAnsiCommand_H() {
		moveCursor(getAnsiParameter(0) - 1, getAnsiParameter(1) - 1);
	}

	/**
	 * This method deletes some (or all) of the text on the screen without
	 * moving the cursor.
	 */
	private void processAnsiCommand_J() {
		int ansiParameter;

		if (ansiParameters[0].length() == 0)
			ansiParameter = 0;
		else
			ansiParameter = getAnsiParameter(0);

		switch (ansiParameter) {
		case 0:
			break;

		case 1:
			// Erase from beginning to current position (inclusive).
			break;

		case 2:
			// Erase entire display.

			break;

		default:
			break;
		}
	}

	/**
	 * This method deletes some (or all) of the text in the current line without
	 * moving the cursor.
	 */
	private void processAnsiCommand_K() {
		int ansiParameter = getAnsiParameter(0);

		switch (ansiParameter) {
		case 0:
			// Erase from beginning to current position (inclusive).
			break;

		case 1:
			// Erase from current position to end (inclusive).
			break;

		case 2:
			// Erase entire line.
			break;

		default:
			break;
		}
	}

	/**
	 * Insert one or more blank lines. The current line of text moves down. Text
	 * that falls off the bottom of the screen is deleted.
	 */
	private void processAnsiCommand_L() {
		getAnsiParameter(0);
	}

	/**
	 * Delete one or more lines of text. Any lines below the deleted lines move
	 * up, which we implement by appending newlines to the end of the text.
	 */
	private void processAnsiCommand_M() {
		getAnsiParameter(0);
	}

	/**
	 * This method sets a new graphics rendition mode, such as
	 * foreground/background color, bold/normal text, and reverse video.
	 */
	private void processAnsiCommand_m() {
		if (ansiParameters[0].length() == 0) {
			// This a special case: when no ANSI parameter is specified, act like a
			// single parameter equal to 0 was specified.

			ansiParameters[0].append('0');
		}
		// There are a non-zero number of ANSI parameters. Process each one in
		// order.

		int totalParameters = ansiParameters.length;
		int parameterIndex = 0;

		while (parameterIndex < totalParameters
				&& ansiParameters[parameterIndex].length() > 0) {
			int ansiParameter = getAnsiParameter(parameterIndex);

			switch (ansiParameter) {
			case 0:
				// Reset all graphics modes.
				break;

			case 1:
				break;

			case 4:
				break;

			case 5:
				break;

			case 7:
				break;

			case 10: // Set primary font. Ignored.
				break;

			case 21:
			case 22:
				break;

			case 24:
				break;

			case 25:
				break;

			case 27:
				break;

			case 30:
				break;

			case 31:
				break;

			case 32:
				break;

			case 33:
				break;

			case 34:
				break;

			case 35:
				break;

			case 36:
				break;

			case 37:
				break;

			case 40:
				break;

			case 41:
				break;

			case 42:
				break;

			case 43:
				break;

			case 44:
				break;

			case 45:
				break;

			case 46:
				break;

			case 47:
				break;

			default:
				break;
			}

			++parameterIndex;
		}
	}

	/**
	 * This method responds to an ANSI Device Status Report (DSR) command from
	 * the remote endpoint requesting the cursor position. Requests for other
	 * kinds of status are ignored.
	 */
	private void processAnsiCommand_n() {
	}

	/**
	 * Deletes one or more characters starting at the current cursor position.
	 * Characters on the same line and to the right of the deleted characters
	 * move left. If there are no characters on the current line at or to the
	 * right of the cursor column, no text is deleted.
	 */
	private void processAnsiCommand_P() {
	}

	/**
	 * This method returns one of the numeric ANSI parameters received in the
	 * most recent escape sequence.
	 *
	 * @return The <i>parameterIndex</i>th numeric ANSI parameter or -1 if the
	 *         index is out of range.
	 */
	private int getAnsiParameter(int parameterIndex) {
		if (parameterIndex < 0 || parameterIndex >= ansiParameters.length) {
			// This should never happen.
			return -1;
		}

		String parameter = ansiParameters[parameterIndex].toString();

		if (parameter.length() == 0)
			return 1;

		int parameterValue = 1;

		// Don't trust the remote endpoint to send well formed numeric
		// parameters.

		try {
			parameterValue = Integer.parseInt(parameter);
		} catch (NumberFormatException ex) {
			parameterValue = 1;
		}

		return parameterValue;
	}

	/**
	 * This method processes a single parameter character in an ANSI escape
	 * sequence. Parameters are the (optional) characters between the leading
	 * "\e[" and the command character in an escape sequence (e.g., in the
	 * escape sequence "\e[20;10H", the parameter characters are "20;10").
	 * Parameters are integers separated by one or more ';'s.
	 */
	private void processAnsiParameterCharacter(char ch) {
		if (ch == ';') {
			++nextAnsiParameter;
		} else {
			if (nextAnsiParameter < ansiParameters.length)
				ansiParameters[nextAnsiParameter].append(ch);
		}
	}

	/**
	 * Process a BEL (Control-G) character.
	 */
	private void processBEL() {
		// TODO
		//Display.getDefault().beep();
	}

	/**
	 * Process a backspace (Control-H) character.
	 */
	private void processBackspace() {
		moveCursorBackward(1);
	}

	/**
	 * Process a tab (Control-I) character. We don't insert a tab character into
	 * the StyledText widget. Instead, we move the cursor forward to the next
	 * tab stop, without altering any of the text. Tab stops are every 8
	 * columns. The cursor will never move past the rightmost column.
	 */
	private void processTab() {
	}

	/**
	 * This method moves the cursor to the specified line and column. Parameter
	 * <i>targetLine</i> is the line number of a screen line, so it has a
	 * minimum value of 0 (the topmost screen line) and a maximum value of
	 * heightInLines - 1 (the bottommost screen line). A line does not have to
	 * contain any text to move the cursor to any column in that line.
	 */
	private void moveCursor(int targetLine, int targetColumn) {
	}

	/**
	 * This method moves the cursor down <i>lines</i> lines, but won't move the
	 * cursor past the bottom of the screen. This method does not cause any
	 * scrolling.
	 */
	private void moveCursorDown(int lines) {
	}

	/**
	 * This method moves the cursor up <i>lines</i> lines, but won't move the
	 * cursor past the top of the screen. This method does not cause any
	 * scrolling.
	 */
	private void moveCursorUp(int lines) {
	}

	/**
	 * This method moves the cursor forward <i>columns</i> columns, but won't
	 * move the cursor past the right edge of the screen, nor will it move the
	 * cursor onto the next line. This method does not cause any scrolling.
	 */
	private void moveCursorForward(int columnsToMove) {
	}

	/**
	 * This method moves the cursor backward <i>columnsToMove</i> columns, but
	 * won't move the cursor past the left edge of the screen, nor will it move
	 * the cursor onto the previous line. This method does not cause any
	 * scrolling.
	 */
	private void moveCursorBackward(int columnsToMove) {
	}
	/**
	 * Resets the state of the terminal text (foreground color, background color,
	 * font style and other internal state). It essentially makes it ready for new input.
	 */
	public void resetState() {
		ansiState=ANSISTATE_INITIAL;
	}

}
