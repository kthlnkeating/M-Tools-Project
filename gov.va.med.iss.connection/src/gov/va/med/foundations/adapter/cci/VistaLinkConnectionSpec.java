package gov.va.med.foundations.adapter.cci;

import javax.resource.cci.ConnectionSpec;

/**
 * This implementation class is used by an application component to pass
 * connection-specific info/properties to the getConnection() method in
 * VistaLinkConnectionFactory class.
 *
 * It supports the most common properties: userName, password and url. The
 * sub-classes should extend if there are more properties.
 *
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaLinkConnectionSpec implements ConnectionSpec {
	private String userName;
	private String password;
	private String url;

	/**
	 * Constructs a VistaLinkConnectionSpec object.
	 * 
	 * @see java.lang.Object#Object()
	 */
	public VistaLinkConnectionSpec() {
		userName = null;
		password = null;
		url = null;
	}

	/**
	 * Returns the user name of this ConnectionSpec.
	 *
	 * @return	the user name of this ConnectionSpec, in String
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Sets the user name of this ConnectionSpec.
	 *
	 * @param	userName		the user name to be set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Returns the password of this ConnectionSpec.
	 *
	 * @return	the password of this ConnectionSpec, in String
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password of this ConnectionSpec.
	 *
	 * @param	password 		password to be set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * Returns the url of this ConnectionSpec.
	 *
	 * @return	 url of this ConnectionSpec, in String
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the url of this ConnectionSpec.
	 *
	 * @param	url 		url to be set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}
