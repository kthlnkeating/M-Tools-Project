package gov.va.med.iss.meditor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "gov.va.med.iss.meditor.messages"; //$NON-NLS-1$
	public static String DEFAULT_MSG_TITLE;
	public static String EDITOR_FILE_WRONG_PROJECT;
	public static String ERRORS_FOUND_FOR_FILES;
	public static String INPUT_DLG_INPUT_MSG;
	public static String INPUT_DLG_INVALID_MSG;
	public static String INPUT_DLG_REQUIRED_MSG;	
	public static String FILE_NOT_SAVED;
	public static String FILE_SAVED;
	public static String LOAD_M_RTN_DLG_TITLE;
	public static String LOAD_M_RTNS_DLG_TITLE;
	public static String LOAD_BACKUP_SYNCH_ERROR;
	public static String NO_FILES;
	public static String NOT_SUPPORTED_MFILE_CONTENT;
	public static String NOT_SUPPORTED_SELECTION_LIST;
	public static String NOT_SUPPORTED_SELECTION;
	public static String NOT_SUPPORTED_RESOURCES;
	public static String ROUTINE_NAME;
	public static String ROUTINE_NAMESPACE;	
	public static String ROUTINE_NOT_ON_SERVER;
	public static String ROUTINE_CREATED_IN_PROJECT;
	public static String ROUTINE_UPDATED_IN_PROJECT;
	public static String ROUTINE_IDENTICAL_IN_PROJECT;
	public static String ROUTINE_SAVED_W_WARNINGS;
	public static String SAVE_BACKUP_PRE_SERVER;
	public static String SAVE_BACKUP_SYNCH_ERROR;
	public static String SERVER_BACKUP_CONFLICT;
	public static String SERVER_CLIENT_EQUAL;
	public static String SERVER_FIRST_SAVE;
	public static String SERVER_DELETED;
	public static String UNABLE_BKUP_LOAD;
	public static String UNABLE_GET_PROJECT;
	public static String UNABLE_OPEN_EDITOR;
	public static String UNABLE_RTN_LOAD;
	public static String UNABLE_RTN_SAVE;
	public static String UNEXPECTED_EDITOR_FILE_NULL;
	public static String UNEXPECTED_EDITOR_FILE_NOTM;
	public static String UNEXPECTED_INTERNAL;
	public static String UNEXPECTED_OBJECT;
	public static String WRITE_TO_CONSOLE_ERROR;
	public static String XINDEX_IN_CONSOLE;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

	public static String bind2(String message, Object... bindings) {
		return bind(message, bindings);
	}
}
