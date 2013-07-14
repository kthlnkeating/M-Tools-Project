package gov.va.med.foundations.adapter.record;

import gov.va.med.foundations.utilities.ExceptionUtils;
import gov.va.med.foundations.utilities.FoundationsException;
import gov.va.med.foundations.xml.XmlUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

// import x.gov.va.med.iss.log4j.*;

/**
 * 
 * Base response factory implementation. Performs system level response parsing tasks.
 * Allows subclasses to plugin more specific parsing implementations. 
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 *
 */
public class VistaLinkResponseFactoryImpl implements VistaLinkResponseFactory {

	/**
	* The logger used by this class
	*/
	private static final Logger logger =
		Logger.getLogger(VistaLinkResponseFactoryImpl.class);

	// constants used to extract results string from returned XML stream
	
	/**
	* Fault message type name.
	*/
	protected static final String GOV_VA_MED_FOUNDATIONS_FAULT =
		"gov.va.med.foundations.vistalink.system.fault";

	/**
	 * Defines the message type for a security errors response
	 */
	protected static final String ERROR_MSG_GEN =
		XmlUtilities.XML_HEADER
			+ "<VistaLink messageType=\""
			+ GOV_VA_MED_FOUNDATIONS_FAULT;

	/**
	 * Response message suffix - the last two XML tags.
	 */
	protected static final String SUFFIX = "</Response></VistaLink>";

	/**
	 * Response message root element name.
	 */
	protected static final String VISTALINK_ROOT_ELEMENT = "VistaLink";

	/**
	 * Constructor for VistaLinkResponseFactoryImpl.
	 */
	public VistaLinkResponseFactoryImpl() {
		super();
	}

	/**
	 * Method called by the managedConnection to parse the response String and return
	 * VistaLinkResponseVO implementation.
	 * 
	 * @see gov.va.med.foundations.adapter.record.VistaLinkResponseFactory#handleResponse(java.lang.String, gov.va.med.foundations.adapter.record.VistaLinkRequestVO)
	 */
	public final VistaLinkResponseVO handleResponse(
		String response,
		VistaLinkRequestVO requestVO)
		throws FoundationsException {

		try {
			return parseMessageHeader(response, requestVO);
		} catch (VistaLinkFaultException e) {
			// do not log these as this application level exception
			throw e;
		} catch (FoundationsException e) {

			if (logger.isEnabledFor(Level.ERROR)) {

				String errMsg =
					(new StringBuffer())
						.append("Exception occured parsing response.")
						.append("\n\t")
						.append(ExceptionUtils.getFullStackTrace(e))
						.toString();

	 			logger.error(errMsg);
			}
			throw e;
		}
	}

	/**
	 * Method parseMessageHeader.
	 * <br> Parses message header and performs other common response
	 * parsing tasks. Can not be subclassed. 
	 * <br> This method delegates more specific parsing step calls to other methods of this class.
	 * 
	 * @param rawXml
	 * @param requestVO
	 * @return VistaLinkResponseVO
	 * @throws FoundationsException
	 */
	private VistaLinkResponseVO parseMessageHeader(
		String rawXml,
		VistaLinkRequestVO requestVO)
		throws VistaLinkFaultException, FoundationsException {

		// has a fault been returned, allow subclasses to indicate their own fault types
		boolean isFault =
			doesResponseIndicateFaultCommon(rawXml)
				|| doesResponseIndicateFault(rawXml);

		VistaLinkResponseVO resp = null;
		Document doc = null;

		// allow subclasses to perform addional XML String filtering,
		// such as removing CDATA before parsing the XML String into XML Document
		String filteredXml = filterResponseXmlString(rawXml, isFault);

		// parse XML String into the XML Document
		doc = XmlUtilities.getDocumentForXmlString(filteredXml);

		// Check if the root element name is correct
		if (!doc
			.getDocumentElement()
			.getNodeName()
			.equals(VISTALINK_ROOT_ELEMENT)) {
			throw new FoundationsException("Root element of response is not VistaLink.");
		}
		
		@SuppressWarnings("unused")
		String version =
			((Attr) (doc.getDocumentElement().getAttributes())
				.getNamedItem("version"))
				.getValue();

/*  JLI  071012 see if corrects VL 1.5 to 1.0 backward compatiblity problem
		if (!version.equals(VistaLinkManagedConnectionFactory.ADAPTER_VERSION)){
			
			throw new FoundationsException(
				(new StringBuffer()).append("The response version [")
				.append(version)
				.append("] is incompatible with this version of the adapter [")
				.append(VistaLinkManagedConnectionFactory.ADAPTER_VERSION)
				.append("]").toString());
					
		}
*/  // JLI 071012 end of commented section

		// Get messageType
		String messageType =
			((Attr) (doc.getDocumentElement().getAttributes())
				.getNamedItem("messageType"))
				.getValue();

		if (isFault) {
			// if fault, handle it
			handleFault(doc, messageType);
		} else {
			// if no fault, parse message body, create response object
			resp =
				parseMessageBody(rawXml, filteredXml, doc, messageType, requestVO);
		}

		return resp;
	}

	/**
	 * Method doesResponseIndicateFaultCommon.
	 * Private and can not be overrided.
	 * <br>Returns true if response indicates common fault condition.
	 * 
	 * @param rawXml
	 * @return boolean
	 */
	private boolean doesResponseIndicateFaultCommon(String rawXml) {
		return rawXml.startsWith(ERROR_MSG_GEN);
	}

	/**
	 * Method doesResponseIndicateFault.
	 * <br>Should be overriden by the child classes to discover additional
	 * fault indication.
	 * <br>Returns true if response indicates specific fault condition.
	 * 
	 * @param rawXml
	 * @return boolean
	 */
	protected boolean doesResponseIndicateFault(String rawXml) {
		return false;
	}

	/**
	 * Method filterResponseXmlString.
	 * <br>Allows child classes to implement their own response XML filtering
	 * before any other work is done on the response String - such as removing 
	 * CDATA from the response XML.
	 * 
	 * @param rawXml
	 * @return String
	 */
	protected String filterResponseXmlString(String rawXml, boolean isFault) {
		return rawXml;
	}

	/**
	 * Method parseMessageBody.
	 * 
	 * <br>Parses message body. Child classes need to implement this
	 * method to be able to create their specific response objects.
	 * 
	 * @param response
	 * @param doc
	 * @param messageType
	 * @param requestVO
	 * @return VistaLinkResponseVO
	 * @throws FoundationsException
	 */
	protected VistaLinkResponseVO parseMessageBody(
		String rawXml,
		String filteredXml,
		Document doc,
		String messageType,
		VistaLinkRequestVO requestVO)
		throws FoundationsException {

		return new VistaLinkResponseVOImpl(rawXml, filteredXml, doc, messageType);

	}

	/**
	 * Method handleFault. 
	 * 
	 * <br>Initiates VLJ Fault handling.
	 * <br> Parses Fault message and stores fault data in the VistaLinkFaultException.
	 * <br> Calls handleAndDelegateSpecificFault to perform more specific fault handling tasks. 
	 * 
	 * <br>Handles generic VLJ faults.
	 * 
	 * @param xdoc
	 * @param messageType
	 * @throws VistaLinkFaultException
	 * @throws FoundationsException
	 */
	protected final void handleFault(Document xdoc, String messageType)
		throws VistaLinkFaultException, FoundationsException {

		String faultCode = "";
		String faultString = "";
		String faultActor = "";
		String errorCode = "";
		String errorType = "";
		String errorMessage = "";

		try {
			XPath xpath = new DOMXPath("VistaLink/Fault/FaultCode/text()");
			Node faultCodeNode = (Node) xpath.selectSingleNode(xdoc);
			if (faultCodeNode != null) {
				if (faultCodeNode.getNodeType() == Node.TEXT_NODE) {
					faultCode = ((Text) faultCodeNode).getData();
				}
			}

			xpath = new DOMXPath("VistaLink/Fault/FaultString/text()");
			Node faultStringNode = (Node) xpath.selectSingleNode(xdoc);
			if (faultStringNode != null) {
				if (faultStringNode.getNodeType() == Node.TEXT_NODE) {
					faultString = ((Text) faultStringNode).getData();
				}
			}

			xpath = new DOMXPath("VistaLink/Fault/FaultActor/text()");
			Node faultActorNode = (Node) xpath.selectSingleNode(xdoc);
			if (faultActorNode != null) {
				if (faultActorNode.getNodeType() == Node.TEXT_NODE) {
					faultActor = ((Text) faultActorNode).getData();
				}
			}

			xpath = new DOMXPath("/VistaLink/Fault/Detail/Error/.");
			Node errorNode = (Node) xpath.selectSingleNode(xdoc);
			errorCode =
				((Attr) (errorNode.getAttributes()).getNamedItem("code"))
					.getValue();

			Attr errorTypeAttr =
				(Attr) errorNode.getAttributes().getNamedItem("type");
			if (errorTypeAttr != null) {
				errorType = errorTypeAttr.getValue();
			}

			xpath = new DOMXPath("/VistaLink/Fault/Detail/Error/Message/text()");
			Node msgNode = (Node) xpath.selectSingleNode(xdoc);
			if (msgNode != null) {
				errorMessage = msgNode.getNodeValue();
			}

			// Create new VistaLinkFaultException using info we just collected.
			// VistaLinkFaultException creates exception representation message. 
			VistaLinkFaultException faultException =
				new VistaLinkFaultException(
					errorCode,
					errorMessage,
					errorType,
					faultActor,
					faultCode,
					faultString);

			//
			// If fault parsing structure above is GENERIC 
			// for all of the VistaLink - an assumption for now, 
			// then we should only need to   
			// perform additional call below to allow subclasses 
			// to perform it's part of exception handling
			// and be able to return it's own VistaLinkFaultException subclass implementation.
			//

			// Allow subclasses to provide their own fault exception implementation
			VistaLinkFaultException specificFaultException =
				handleAndDelegateSpecificFault(xdoc, messageType, faultException);

			// we could just throw an exception in handleSpecificFault()
			// but it is easier to read the code this way 
			throw specificFaultException;

		} catch (JaxenException e) {
			throw new FoundationsException("Exception parsing XML.", e);
		}

	}

	/**
	 * Method handleAndDelegateSpecificFault.
	 * 
	 * <br>Private method that analizes fault and returns more specific 
	 * VLJ exception. 
	 * <br> If more specific fault exception is not faund, delegates
	 * fault processing to the subclasses by calling protected 
	 * handleSpecificFault() method that subclasses can override.
	 * 
	 * @param xdoc
	 * @param messageType
	 * @param faultException
	 * @return VistaLinkFaultException
	 * @throws VistaLinkFaultException
	 * @throws FoundationsException
	 */
	private VistaLinkFaultException handleAndDelegateSpecificFault(
		Document xdoc,
		String messageType,
		VistaLinkFaultException faultException)
		throws VistaLinkFaultException, FoundationsException {

		if ("181003".equals(faultException.getErrorCode())) {
			return new NoJobSlotsAvailableFaultException(faultException);
		} else if ("181004".endsWith(faultException.getErrorCode())) {
			return new LoginsDisabledFaultException(faultException);
		} else {
			// if non of the above, perform delegation so that
			// child classes can do their part of work and be able
			// to provide more specific faul exception
			VistaLinkFaultException returnFaultException =
				handleSpecificFault(xdoc, messageType, faultException);
			if (returnFaultException == null) {
				returnFaultException = faultException;
			}
			return returnFaultException;
		}
	}

	/**
	 * Method handleSpecificFault.
	 * 
	 * This method can be overriden by the subclasses to perform additional
	 * fault handling and return a subclass of VistaLinkFaultException.
	 * <br> Implementation in this class returns faultException that is passed
	 * into the class.
	 * 
	 * <br> Overriden method can either return a:
	 * <br>  1. More specific VistaLinkFaultException implementation.
	 * <br>  2. The same faultException that is passed into the method.
	 * <br>  3. null. Will have the same result as option 2 - returning the same
	 * faultException that is passed into the method.
	 * 
	 * @param xdoc
	 * @param messageType
	 * @param faultException
	 * @return VistaLinkFaultException
	 * @throws VistaLinkFaultException
	 * @throws FoundationsException
	 */
	protected VistaLinkFaultException handleSpecificFault(
		Document xdoc,
		String messageType,
		VistaLinkFaultException faultException)
		throws VistaLinkFaultException, FoundationsException {

		return null;
	}

}
