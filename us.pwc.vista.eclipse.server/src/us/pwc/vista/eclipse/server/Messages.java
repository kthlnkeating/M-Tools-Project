package us.pwc.vista.eclipse.server;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "us.pwc.vista.eclipse.server.messages"; //$NON-NLS-1$
	public static String LOAD_MSG_TITLE;
	public static String SAVE_MSG_TITLE;
	public static String FILE_SELECT_TITLE;
	public static String GLOBAL_LISTING;
	public static String EDITOR_FILE_WRONG_PROJECT;
	public static String ERRORS_FOUND_FOR_FILES;
	public static String INPUT_DLG_INPUT_MSG;
	public static String INPUT_DLG_INVALID_MSG;
	public static String INPUT_DLG_REQUIRED_MSG;	
	public static String FILE_NOT_SAVED;
	public static String FILE_SAVED;
	public static String LOAD_M_RTN_DLG_TITLE;
	public static String LOAD_M_RTNS_DLG_TITLE;
	public static String LOAD_RTNDIR_DLG_TITLE;
	public static String LOAD_BACKUP_SYNCH_ERROR;
	public static String NO_FILES;
	public static String NO_FILES_IN_NAMESPACE;
	public static String NOT_SUPPORTED_MFILE_CONTENT;
	public static String NOT_SUPPORTED_SELECTION_LIST;
	public static String NOT_SUPPORTED_SELECTION;
	public static String NOT_SUPPORTED_RESOURCES;
	
	public static String RLP_ASK;
	public static String RLP_INVALID_NUMBER;
	public static String RLP_NAMESPACE_SPECIFIED;
	public static String RLP_NAMESPACE_SUBFOLDER;
	public static String RLP_NUMBER_RANGE;
	public static String RLP_NUMBER_REQUIRED;
	public static String RLP_PROJECT_ROOT;
	public static String RLP_SERVER_NAME_SUBFOLDER;
	public static String RLP_TITLE_SCHEME;
	public static String ROUTINE_NAME;
	public static String ROUTINE_NAMESPACE;	
	public static String GLOBAL_NAMESPACE;	
		
	public static String ROUTINE_NOT_ON_SERVER;
	public static String ROUTINE_CREATED_IN_PROJECT;
	public static String ROUTINE_UPDATED_IN_PROJECT;
	public static String ROUTINE_IDENTICAL_IN_PROJECT;
	public static String ROUTINE_SAVED_W_WARNINGS;
	public static String SAVE_BACKUP_PRE_SERVER;
	public static String SAVE_BACKUP_SYNCH_ERROR;
	public static String SAVE_M_RTNS_DLG_TITLE;
	public static String SERVER_BACKUP_CONFLICT;
	public static String SERVER_CLIENT_EQUAL;
	public static String SERVER_FIRST_SAVE;
	public static String SERVER_DELETED;
	public static String UNABLE_BKUP_LOAD;
	public static String UNABLE_GET_PROJECT;
	public static String UNABLE_OPEN_EDITOR;
	public static String UNABLE_RTN_LOAD;
	public static String UNABLE_RTN_SAVE;
	public static String UNABLE_GET_HANDLE;
	public static String UNEXPECTED_EDITOR_FILE_NULL;
	public static String UNEXPECTED_INTERNAL;
	public static String WRITE_TO_CONSOLE_ERROR;
	public static String XINDEX_IN_CONSOLE;
	
	public static String DLG_GLOBAL_DIR_TITLE;
	public static String DLG_GLOBAL_DIR_UNEXPECTED;
	
	public static String DLG_GLOBAL_LISTING_TITLE;
	public static String DLG_GLOBAL_LISTING_GLNAME;
	public static String DLG_GLOBAL_LISTING_RTNTYP;	
	public static String DLG_GLOBAL_LISTING_RTNTYP_0;
	public static String DLG_GLOBAL_LISTING_RTNTYP_1;
	public static String DLG_GLOBAL_LISTING_RTNTYP_2;
	public static String DLG_GLOBAL_LISTING_RTNTYP_0_TT;
	public static String DLG_GLOBAL_LISTING_RTNTYP_1_TT;
	public static String DLG_GLOBAL_LISTING_RTNTYP_2_TT;
	public static String DLG_GLOBAL_LISTING_TXTSRC;
	public static String DLG_GLOBAL_LISTING_TXTSRC_TT;
	public static String DLG_GLOBAL_LISTING_TXTSRC_T;
	public static String DLG_GLOBAL_LISTING_TXTSRC_0;
	public static String DLG_GLOBAL_LISTING_TXTSRC_1;
	public static String DLG_GLOBAL_LISTING_TXTSRC_2;

	public static String GLOBAL_LISTING_UNEXPECTED;
	public static String GLOBAL_LISTING_MORE;
	
	public static String MULTI_LOAD_RTN_ERRR;
	public static String MULTI_LOAD_RTN_WARN;
	public static String MULTI_LOAD_RTN_INFO;
	
	public static String MULTI_SAVE_RTN_ERRR;
	public static String MULTI_SAVE_RTN_WARN;
	public static String MULTI_SAVE_RTN_INFO;
	
	public static String MULTI_LOAD_RTN_FOLDER_SINGLE;
	public static String MULTI_LOAD_RTN_FOLDER_ONLY;
	public static String MULTI_LOAD_RTN_NONE_IN_NAMESPC;

	public static String CONNECTION_INVALID_PROJECT;
	public static String PROJECT_INVALID_FILE;
	
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
