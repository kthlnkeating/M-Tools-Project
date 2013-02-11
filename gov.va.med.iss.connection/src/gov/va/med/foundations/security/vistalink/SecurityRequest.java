package gov.va.med.foundations.security.vistalink;

import gov.va.med.foundations.adapter.record.VistaLinkRequestVOImpl;

import org.w3c.dom.Document;

/**
 * Represents a security request, to be sent to an M system for processing and response.
 * @see SecurityRequestFactory
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class SecurityRequest extends VistaLinkRequestVOImpl {

	/**
	 * Constructor for RequestVO.
	 * @param requestDoc Document representing the request
	 */
	SecurityRequest(Document requestDoc) {
		super(requestDoc);
	}

}
