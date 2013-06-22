package gov.va.med.foundations.adapter.record;

import gov.va.med.foundations.utilities.ExceptionUtils;
import gov.va.med.foundations.utilities.FoundationsException;
import gov.va.med.foundations.xml.XmlUtilities;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.w3c.dom.Document;

// import x.gov.va.med.iss.log4j.*;

/**
 * Base request implementation.
 * <br> Users usually use a specific subclass of this implementation. 
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 *
 */
public class VistaLinkRequestVOImpl implements VistaLinkRequestVO {

	/**
	* The logger used by this class
	*/
	private static final Logger logger =
		Logger.getLogger(VistaLinkRequestVOImpl.class);

	/**
	 * Request data DOM document - original data
	 */
	protected Document requestDoc = null;

	/**
	 * Request data as XML String - computed once per object lifecycle
	 */
	protected String xmlString = null;

	/**
	 * Constructor for VistaLinkRequestVOImpl.
	 * 
	 * @see java.lang.Object#Object()
	 */
	public VistaLinkRequestVOImpl() {
		this.requestDoc = null;
	}

	/**
	 * Constructor for VistaLinkRequestVOImpl.
	 * 
	 * @param requestDoc
	 */
	public VistaLinkRequestVOImpl(Document requestDoc) {
		this.requestDoc = requestDoc;
	}

	/**
	 * Returns request XML data as a String.
	 * 
	 * @see gov.va.med.foundations.adapter.record.VistaLinkRequestVO#getRequestString()
	 */
	public String getRequestString() throws FoundationsException {
		if (requestDoc == null) {
			String errStr =
				"Can not return request String as requestDoc == null. Make sure to initialize Request appropriately.";
			FoundationsException e = new FoundationsException(errStr);

			if (logger.isEnabledFor(Priority.ERROR)) {

				String errMsg = ExceptionUtils.getFullStackTrace(e);

				logger.error(errMsg);
	 		}

			throw e;
		}
		try {
			xmlString = XmlUtilities.convertXmlToStr(requestDoc);
		} catch (FoundationsException e) {

			if (logger.isEnabledFor(Priority.ERROR)) {

				String errMsg =
					(new StringBuffer())
						.append("Converting requestDoc to XML String failed.")
						.append("\n\t")
						.append(ExceptionUtils.getFullStackTrace(e))
						.toString();

				logger.error(errMsg);
			}

			throw e;
		}
		return xmlString;
	}

}
