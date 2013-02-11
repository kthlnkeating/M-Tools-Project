/**
 * 
 */
package gov.va.med.iss.connection.preferences;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.jface.preference.FieldEditor;
/**
 * @author vhaisfiveyj
 *
 */
public class ServerListFieldEditor extends ListFieldEditor {
	private String nameBase = "";

	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public ServerListFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		nameBase = name;
		// TODO Auto-generated constructor stub
	}

	protected void doStore() {
		super.doStore();
		if (nameBase.compareTo(ConnectionPreferencePage.P_SERVER_NUM) == 0) {
			String str1 = getPreferenceStore().getString(nameBase+1);
			if (! (str1.compareTo("") == 0)) {
				String str = str1.substring(0,str1.indexOf(";"));
				str1 = str1.substring(str1.indexOf(";")+1);
				getPreferenceStore().setValue(ConnectionPreferencePage.P_SERVER_NAME,str);
				str = str1.substring(0,str1.indexOf(";"));
				str1 = str1.substring(str1.indexOf(";")+1);
				getPreferenceStore().setValue(ConnectionPreferencePage.P_SERVER,str);
				getPreferenceStore().setValue(ConnectionPreferencePage.P_PORT,str1);
			}
		}
	}
}
