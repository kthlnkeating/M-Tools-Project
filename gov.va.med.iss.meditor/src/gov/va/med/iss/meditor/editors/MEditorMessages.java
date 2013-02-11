/*
 * Created on Aug 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.editors;


import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MEditorMessages {

	private static final String RESOURCE_BUNDLE= "gov.va.med.iss.meditor.editors.MEditorMessages";//$NON-NLS-1$

	private static ResourceBundle fgResourceBundle= ResourceBundle.getBundle(RESOURCE_BUNDLE);

	private MEditorMessages() {
	}

	public static String getString(String key) {
		try {
			return fgResourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return "!" + key + "!";//$NON-NLS-2$ //$NON-NLS-1$
		}
	}
	
	public static ResourceBundle getResourceBundle() {
		return fgResourceBundle;
	}
}
