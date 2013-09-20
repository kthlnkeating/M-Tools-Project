package gov.va.mumps.debug.ui.terminal;

public enum OutputStreamState {
	NOT_CONNECTED,
	COMMAND_EXECUTE,
	RESUMED,
	BREAK_SEARCH,
	BREAK_FOUND,
	VARIABLE_QUERY
}
