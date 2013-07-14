/*
 * Created on Aug 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.preferences;

import gov.va.med.iss.meditor.MEditorPlugin;

import org.eclipse.core.runtime.Platform;

/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MEditorPrefs {
	
	private static MEditorPreferencesPage mepp = null;
	
	public static String getPrefs(String prefName) {
		if (mepp == null) {
			mepp = new MEditorPreferencesPage();
		}
		String prefValue = Platform.getPreferencesService().getString(MEditorPlugin.PLUGIN_ID, prefName, "", null);
		return prefValue;
	}
}
