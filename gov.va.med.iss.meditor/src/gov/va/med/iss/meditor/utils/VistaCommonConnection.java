/*
 * Created on Aug 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.utils;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;

/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VistaCommonConnection {

	private static VistaLinkConnection myConnection = null;

	public static VistaLinkConnection getConnection() {
		if (myConnection == null) {
			myConnection = VistaConnection.getConnection();
		}
		return myConnection;
	}
}
