package gov.va.med.foundations.adapter.heartbeat;

import gov.va.med.foundations.adapter.record.VistaLinkRequestVO;
import gov.va.med.foundations.adapter.record.VistaLinkResponseFactoryImpl;
import gov.va.med.foundations.adapter.record.VistaLinkResponseVO;
import gov.va.med.foundations.utilities.ExceptionUtils;
import gov.va.med.foundations.utilities.FoundationsException;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

// import x.gov.va.med.iss.log4j.*;

/**
 * This class represents the Response Factory used to create the
 * <br> VistaHeartBeatTimerResponse value object
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaHeartBeatTimerResponseFactory
	extends VistaLinkResponseFactoryImpl {

	/**
	 * The logger used for this class
	 */
	private static final Logger logger =
		Logger.getLogger(VistaHeartBeatTimerResponseFactory.class);

	/**
	 * Constructor for VistaHeartBeatTimerResponseFactory.
	 */
	public VistaHeartBeatTimerResponseFactory() {
		super();
	}

	/**
	 * <br>Evaluates the response from M and determines if the 
	 * <br>heart beat was successful and gets the heart beat rate 
	 * <br>in millis. Constructs a new VistaHeartBeatTimerResponse 
	 * <br>object.
	 * @see gov.va.med.foundations.adapter.record.VistaLinkResponseFactoryImpl#parseMessageBody(java.lang.String, java.lang.String, org.w3c.dom.Document, java.lang.String, gov.va.med.foundations.adapter.record.VistaLinkRequestVO)
	 */
	protected VistaLinkResponseVO parseMessageBody(
		String rawXml,
		String filteredXml,
		Document doc,
		String messageType,
		VistaLinkRequestVO requestVO)
		throws FoundationsException {

		try {


			boolean heartBeatSuccessful;
			long heartBeatRateMillis = 0;

			XPath xpath = new DOMXPath("/VistaLink/Response/.");
			Node resultsNode = (Node) xpath.selectSingleNode(doc);

			//			// get result type
			NamedNodeMap attrs = resultsNode.getAttributes();

			if(logger.isDebugEnabled()){
				logger.debug("got response attributes");
				
			}


			heartBeatSuccessful =
				(((Attr) attrs.getNamedItem("type"))
					.getValue()
					.equals("heartbeat"))
					&& (((Attr) attrs.getNamedItem("status"))
						.getValue()
						.equals("success"));

			if(logger.isDebugEnabled()){
				logger.debug("got heart beat success");
				
			}


			heartBeatRateMillis =
				1000
					* Long.parseLong(
						((Attr) attrs.getNamedItem("rate")).getValue());

			if(logger.isDebugEnabled()){
				logger.debug("got heart beat rate millis");
				
			}

			return new VistaHeartBeatTimerResponse(
				rawXml,
				filteredXml,
				doc,
				messageType,
				heartBeatSuccessful,
				heartBeatRateMillis);

		} catch (JaxenException e) {

			String errStr = "could not parse xml";

			if(logger.isEnabledFor(Priority.ERROR)){

				String errMsg = (new StringBuffer())
					.append(errStr)
					.append("\n\t")
					.append(ExceptionUtils
							.getFullStackTrace(e))
					.toString();
						
				logger.error(errMsg);
			}

			throw new FoundationsException(errStr, e);
		}

	}

}
