package gov.va.med.foundations.security.vistalink;

/**
 * Represents a Vista Institution, including IEN, Station Name and Station Number.
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class VistaInstitution {
	private String institutionIen;
	private String institutionName;
	private String institutionNumber;
	private boolean isDefaultLogonDivision;

	/**
	 * Instantiates a VistaInstitution with all fields set to the null string.
	 */
	VistaInstitution() {
		institutionIen = "";
		institutionName = "";
		institutionNumber = "";
		isDefaultLogonDivision = false;
	}

	/**
	 * Instantiates a VistaInstitution with the fields set to the parameters you specify:
	 * @param divIen the IEN on Vista of the Institution
	 * @param divName the Station Name of the Institution
	 * @param divNumber the Station Number of the Institution
	 * @param isDefaultLogonDivision set to true if this is the default logon division. Defaults to false.
	 */
	VistaInstitution(String divIen, String divName, String divNumber, boolean isDefaultLogonDivision) {
		this.institutionIen = divIen;
		this.institutionName = divName;
		this.institutionNumber = divNumber;
		this.isDefaultLogonDivision = isDefaultLogonDivision;
	}

	/**
	 * Instantiates a VistaInstitution with the fields set to the parameters you specify:
	 * @param divIen the IEN on Vista of the Institution
	 * @param divName the Station Name of the Institution
	 * @param divNumber the Station Number of the Institution
	 */
	VistaInstitution(String divIen, String divName, String divNumber) {
		institutionIen = divIen;
		institutionName = divName;
		institutionNumber = divNumber;
	}

	/**
	 * returns the IEN of the institution, presumably from the VistA Institution file entry
	 * (depending on the source of the information the instance contains)
	 * @return the IEN of the institution
	 */
	String getIen() {
		return institutionIen;
	}
	/**
	 * returns the station name of the institution, presumably from the VistA Institution file entry
	 * (depending on the source of the information the instance contains)
	 * @return the name of the institution
	 */
	String getName() {
		return institutionName;
	}
	/**
	 * returns the station number of the institution, presumably from the VistA Institution file entry
	 * (depending on the source of the information the instance contains)
	 * @return the station number of the institution
	 */
	String getNumber() {
		return institutionNumber;
	}

	/**
	 * If used during part of the logon process, stores whether this is the "default" division
	 * @return boolean true if this is the default division, otherwise false.
	 */
	boolean getIsDefaultLogonDivision() {
		return isDefaultLogonDivision;
	}
	/**
	 * returns a string representation of the institution information
	 * @return a string with IEN, name and number labels and values.
	 */
	public String toString() {
		return "VistaAVInstitution IEN: "
			+ institutionIen
			+ " Station Name: "
			+ institutionName
			+ " Station Number: "
			+ institutionNumber;
	}
}
