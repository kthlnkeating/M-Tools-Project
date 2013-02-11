package gov.va.med.foundations.adapter.record;

import org.w3c.dom.Document;

/**
 * Base interface for response objects.  
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 *
 */
public interface VistaLinkResponseVO {

	/**
	 * Method getRawResponse. Returns raw xml response string. <br>
	 * 
	 * @return String
	 */
	public String getRawResponse();

	/**
	 * Method getDocument. Returns response as DOM Document.
	 * <br>
	 * 
	 * @return String
	 */
	public Document getDocument();

}
