package gov.va.med.iss.connection.preferences;

import gov.va.med.iss.connection.VLConnectionPlugin;

import java.util.ArrayList;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */


public class ConnectionPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	//public static final String P_SERVER_NAME = "stringPreference";
	//public static final String P_SERVER = "serverPreference";
	//public static final String P_PORT = "portPreference";
	public static final String P_SERVER_NUM = "Server_Preference_";
	//public static final String P_PROJECT = "projectPreference"; // JLI 090908 added for Source Code Version Control
	public static final int SERVER_MAX_NUM = 10;

	public ConnectionPreferencePage() {
		super(GRID);
		setPreferenceStore(VLConnectionPlugin.getDefault().getPreferenceStore());
		setDescription("VistALink Connection Page - \n"+
				"Use the 'Add' button to add a new server.\n"+
				"Enter a *BRIEF* name for the server, the port number and IP address or URL.\n"+
				"The top or first server in the list will be the primary server for "+
				"connections, loading and saving routines and global, routine lists, etc.\n"+
				"Other servers may be chosen to copy saved routines to as well.");
		initializeDefaults();
	}
/**
 * Sets the default values of the preferences.
 */
	private void initializeDefaults() {
		IPreferenceStore store = getPreferenceStore();
		for (int i=1; i<=SERVER_MAX_NUM; i++) {
			store.setDefault(P_SERVER_NUM+i,"");
		}
	}
	
/**
 * Creates the field editors. Field editors are abstractions of
 * the common GUI blocks needed to manipulate various types
 * of preferences. Each field editor knows how to save and
 * restore itself.
 */

	public void createFieldEditors() {
		addField(new ServerListFieldEditor(P_SERVER_NUM,"Working Servers",getFieldEditorParent()));
	}
	
	public void init(IWorkbench workbench) {
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	/***
	 * 
	 * @return ArrayList containing strings indicating 
	 *         name;port;server_address;project_name
	 *          for defined servers
	 *         The first entry is the primary server
	 */
	public static ArrayList<String> getServerList() {
		IPreferenceStore store = VLConnectionPlugin.getDefault().getPreferenceStore();
		ArrayList array = new ArrayList(SERVER_MAX_NUM);
		String str = " ";
		int i = 1;
		while (! (str.compareTo("") == 0) ) {
			str = store.getString(P_SERVER_NUM+i);
			if (! (str.compareTo("") == 0)) {
				if (! (str.charAt(str.length()-1) == ';')) {
					str = str + ";";
				}
				array.add(str);
			}
			i++;
		}
		return array;
	}
}