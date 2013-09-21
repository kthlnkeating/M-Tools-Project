package gov.va.mumps.debug.ui.terminal;

public enum OutputStreamState {
	NOT_CONNECTED,
	WAITING_PROMPT,
	COMMAND_EXECUTE,
	RESUMED,
	BREAK_SEARCH,
	BREAK_FOUND,
	VARIABLE_QUERY
}
