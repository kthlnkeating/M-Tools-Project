/*
 * Created on Aug 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.preferences;

import gov.va.med.iss.meditor.MEditorPlugin;

import org.eclipse.core.runtime.Preferences;

/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MEditorPrefs {
	
	private static MEditorPreferencesPage mepp = null;
	
	public static String getPrefs(String PrefName) {
		if (mepp == null) {
			mepp = new MEditorPreferencesPage();
		}
		Preferences prefs = MEditorPlugin.getDefault().getPluginPreferences();
		String prefValue = prefs.getString(PrefName);

		return prefValue;
	}


}
