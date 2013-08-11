package us.pwc.vista.eclipse.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "us.pwc.vista.eclipse.core.messages"; //$NON-NLS-1$
	public static String PPP_BKUP_DIR_LABEL;
	public static String PPP_BKUP_DIR_MSG_1;
	public static String PPP_BKUP_DIR_MSG_2;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
