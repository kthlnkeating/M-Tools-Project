package gov.va.med.foundations.adapter.heartbeat;

import gov.va.med.foundations.adapter.record.VistaLinkResponseVOImpl;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;

//import x.gov.va.med.iss.log4j.*;

/**
 * This class is a value object that represents a response 
 * <br>from the M heart beat.
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaHeartBeatTimerResponse 
	extends VistaLinkResponseVOImpl {


	/**
	 * The logger used for this class
	 */
	private static final Logger logger =
		Logger.getLogger(VistaHeartBeatTimerResponse.class);


	/**
	 * Indicates whether or not the heart beat was successful
	 */
	private boolean heartBeatSuccessful;

	/**
	 * The rate the heart beat will scheduled at in millis
	 */
	private long heartBeatRateMillis;

	/**
	 * Constructor for VistaHeartBeatTimerResponse.
	 * @param rawXml
	 * @param filteredXml
	 * @param doc
	 * @param messageType
	 * @param heartBeatSuccessful
	 * @param heartBeatRate
	 */
	public VistaHeartBeatTimerResponse(
		String rawXml,
		String filteredXml,
		Document doc,
		String messageType,
		boolean heartBeatSuccessful,
		long heartBeatRate) {

		super(rawXml, filteredXml, doc, messageType);

		setHeartBeatRateMillis(heartBeatRate);
		setHeartBeatSuccessful(heartBeatSuccessful);

		if(logger.isDebugEnabled()){

			logger.debug((new StringBuffer())
				.append(("heart beat timer response VO constructed"))
				.append(isHeartBeatSuccessful())
				.append("[]")
				.append(getHeartBeatRateMillis()).toString());

		}

	}

	/**
	 * Sets the heartBeatRateMillis.
	 * @param heartBeatRateMillis The heartBeatRateMillis to set
	 */
	private void setHeartBeatRateMillis(long heartBeatRateMillis) {
		this.heartBeatRateMillis = heartBeatRateMillis;
	}

	/**
	 * Sets the heartBeatSuccessful.
	 * @param heartBeatSuccessful The heartBeatSuccessful to set
	 */
	private void setHeartBeatSuccessful(boolean heartBeatSuccessful) {
		this.heartBeatSuccessful = heartBeatSuccessful;
	}

	/**
	 * Returns the heartBeatRateMillis.
	 * @return long
	 */
	public long getHeartBeatRateMillis() {
		return heartBeatRateMillis;
	}

	/**
	 * Returns the heartBeatSuccessful.
	 * @return boolean
	 */
	public boolean isHeartBeatSuccessful() {
		return heartBeatSuccessful;
	}

}
