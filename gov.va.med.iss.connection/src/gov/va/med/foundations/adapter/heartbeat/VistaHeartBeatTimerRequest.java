package gov.va.med.foundations.adapter.heartbeat;

// import x.gov.va.med.iss.log4j.*;
import gov.va.med.foundations.adapter.record.VistaLinkRequestVOImpl;
import gov.va.med.foundations.utilities.ExceptionUtils;
import gov.va.med.foundations.utilities.FoundationsException;
import gov.va.med.foundations.xml.XmlUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class represents a heart beat request.
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaHeartBeatTimerRequest extends VistaLinkRequestVOImpl {

	/**
	 * The logger used for this class
	 */
	private static final Logger logger =
		Logger.getLogger(VistaHeartBeatTimerRequest.class);

	/**
	 * The xml request used for a heart beat
	 */
	private static final String HEARTBEAT_REQUEST =
		"<?xml version='1.0' encoding='utf-8' ?><VistaLink messageType='gov.va.med.foundations.vistalink.system.request' version='1.0' mode='singleton' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:noNamespaceSchemaLocation='vlSimpleRequest.xsd' xmlns='http://med.va.gov/Foundations'><Request type='heartbeat' /></VistaLink>";

	/**
	 * Constructor for VistaHeartBeatTimerRequest.
	 * @param requestDoc
	 */

	public VistaHeartBeatTimerRequest() throws FoundationsException {
		try {
			this.requestDoc =
				XmlUtilities.getDocumentForXmlString(HEARTBEAT_REQUEST);
		} catch (FoundationsException e) {

			if(logger.isEnabledFor(Level.ERROR)){
				
				String errMsg = (new StringBuffer())
					.append(
					"Could not construct xml document")
					.append("\n\t")
					.append(ExceptionUtils
							.getFullStackTrace(e))
					.toString();
						
				logger.error(errMsg);
			}
			throw e;
		}
	}

}
