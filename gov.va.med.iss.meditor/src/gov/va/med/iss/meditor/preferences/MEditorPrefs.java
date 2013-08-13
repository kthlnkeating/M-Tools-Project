package gov.va.med.iss.meditor.preferences;

import gov.va.med.iss.meditor.MEditorPlugin;

import org.eclipse.core.runtime.Platform;

public class MEditorPrefs {
	private static MEditorPreferencesPage mepp = null;
	
	public static String getPrefs(String prefName) {
		if (mepp == null) {
			mepp = new MEditorPreferencesPage();
		}
		String prefValue = Platform.getPreferencesService().getString(MEditorPlugin.PLUGIN_ID, prefName, "", null);
		return prefValue;
	}

	public static boolean getAutoSaveToServer() {
		String p = MEditorPrefs.getPrefs(MEditorPlugin.P_AUTO_SAVE_TO_SERVER);		
		return Boolean.valueOf(p);		
	}
}
