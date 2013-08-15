package gov.va.med.iss.meditor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "gov.va.med.iss.meditor.messages"; //$NON-NLS-1$
	public static String UNEXPECTED_EDITOR_FILE_NULL;
	public static String UNEXPECTED_INTERNAL;
		
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
