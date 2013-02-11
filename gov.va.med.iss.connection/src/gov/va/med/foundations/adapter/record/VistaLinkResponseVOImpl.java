package gov.va.med.foundations.adapter.record;

import org.w3c.dom.Document;

/**
 * Base ResponseVO interface implementation. Contains common fields.
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 * 
 */
public class VistaLinkResponseVOImpl implements VistaLinkResponseVO {

	/**
	 * Stores raw XML that came from the socket
	 */
	protected String rawXml = null;

	/**
	 * Stores filtered response XML String. In case of RPC - no CDATA. 
	 */
	protected String filteredXml = null;
	
	/**
	 * DOM object representing filtered XML
	 */
	protected Document xdoc = null;
	
	/**
	 * Message type that was returned from M.
	 */
	protected String msgType = null;

	/**
	 * Default constructor
	 * @see java.lang.Object#Object()
	 */
	public VistaLinkResponseVOImpl() {
		super();
	}


	/**
	 * Constructor.
	 * 
	 * @param rawXml
	 * @param filteredXml
	 * @param doc
	 * @param messageType
	 */
	public VistaLinkResponseVOImpl(
		String rawXml,
		String filteredXml,
		Document doc,
		String messageType) {
		this.rawXml = rawXml;
		this.xdoc = doc;
		this.msgType = messageType;
		this.filteredXml = filteredXml;
	}

	/**
	 * Returns raw response XML String
	 * 
	 * @see gov.va.med.foundations.adapter.record.VistaLinkResponseVO#getRawResponse()
	 */
	public String getRawResponse() {
		return rawXml;
	}

	/**
	 * Returns the DOM object xdoc representing filtered XML response.
	 * 
	 * @return Document
	 */
	public Document getDocument() {
		return xdoc;
	}

	/**
	 * Returns the filteredXml response String.
	 * 
	 * @return String
	 */
	public String getFilteredXml() {
		return filteredXml;
	}

}
