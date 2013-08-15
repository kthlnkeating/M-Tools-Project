package us.pwc.vista.eclipse.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "us.pwc.vista.eclipse.core.messages"; //$NON-NLS-1$
	public static String PPP_BKUP_DIR_LABEL;
	public static String PPP_BKUP_DIR_MSG_1;
	public static String PPP_BKUP_DIR_MSG_2;
	public static String MCH_WRITE_ERROR;
	public static String UNEXPECTED_FILE_EXT;
	public static String UNEXPECTED_OBJECT;

	public static String SEL_FILE_PROJECT_MISMATCH;
	public static String SEL_FILE_UNEXPECTED_OBJECT;
	public static String SEL_FILE_NOT_M;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
