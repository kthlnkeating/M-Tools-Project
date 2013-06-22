package gov.va.med.iss.connection.utilities;

import gov.va.med.iss.connection.VLConnectionPlugin;
import gov.va.med.iss.connection.preferences.ConnectionPreferencePage;

/**
 * Utilities related to connections in Eclipse
 * @author VHAISFIVEYJ
 *
 */
public class ConnectionUtilities {
	
	/**
	 * private constructor to prevent generation of the class.
	 */
	private ConnectionUtilities() {
		
	}
	
	/*
	 * Method getServer
	 * 
	 * @param none
	 * @return String related to the Vista Server specified in the Preferences.
	 */
	public static String getServer() {
		return VLConnectionPlugin.getDefault().getPluginPreferences().getString(ConnectionPreferencePage.P_SERVER);
	}
	
	/**
	 *  Method getPort
	 *  
	 *  @param none
	 *  @return String indicating the Server Port specified in the Preferences.
	 */
    public static String getPort() {
//      return VLConnectionPlugin.getDefault().getPluginPreferences().getString(ConnectionPreferencePage.P_PORT);
        return MPiece.getPiece(VLConnectionPlugin.getDefault().getPluginPreferences().getString(ConnectionPreferencePage.P_SERVER_NUM+1),";",3);  // JLI 090908 added for Source Code Version Control
    }
    
    public static String getProject() {
        return MPiece.getPiece(VLConnectionPlugin.getDefault().getPluginPreferences().getString(ConnectionPreferencePage.P_SERVER_NUM+1),";",4); // JLI 090915 added for Source Code Version Control
    }
}
