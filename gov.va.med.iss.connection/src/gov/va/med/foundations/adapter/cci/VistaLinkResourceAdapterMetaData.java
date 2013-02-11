package gov.va.med.foundations.adapter.cci;

import javax.resource.cci.ResourceAdapterMetaData;

/**
 * Implementation class provides info about the capabilities of a resource
 * adapter.
 * Sub-class must provide values for abstract methods for a particular adapter.
 * Also optionally overide supportXXX() methods when necessary.
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaLinkResourceAdapterMetaData
	implements ResourceAdapterMetaData {


	/**
	 * This adapter's vendor's name
	 */
	private static final String VENDOR_NAME =
		"VistA Migration - Foundations Team";

	/**
	 * The JCA spec this adapter adheres to
	 */
	private static final String JCA_SPEC_VERSION = "1.0";

	/**
	 * The version of this adapter
	 */
	private static final String VERSION = "0.001";

	/**
	 * The name of this adapter
	 */
	private static final String ADAPTERNAME = "VistALink J2M Adapter";

	/**
	 * The short description of this adapter
	 */
	private static final String SHORTDESCRIPTION = "TBD";

	/**
	 * Returns the version of this adapter.
	 *
	 * @return	the version of this adapter, in String
	 */
	public String getAdapterVersion() {
		return VERSION;
	}

	/**
	 * Returns the vendor name of this adapter.
	 *
	 * @return	the vendor name of this adapter, in String
	 */
	public String getAdapterVendorName() {
		return VENDOR_NAME;
	}

	/**
	 * Returns the name of this adapter.
	 *
	 * @return	the name of this adapter, in String
	 */
	public String getAdapterName() {
		return ADAPTERNAME;
	}

	/**
	 * Returns the short description of this adapter.
	 *
	 * @return	the short description of this adapter, in String
	 */
	public String getAdapterShortDescription() {
		return SHORTDESCRIPTION;
	}

	/**
	 * Returns the version of the Connector Architecture Specification
	 * that is spported by the adapter.
	 *
	 * @return	the version of the JCA spec supported by this adapter, in String
	 */
	public String getSpecVersion() {
		return JCA_SPEC_VERSION;
	}

	/**
	 * Returns the fully-qualified names of InteractionSpec types supported
	 * by the CCI implementation for this adapter.
	 *
	 * @return	the fully-qualified names of supported InteractionSpec types,
	 *		in String[]
	 */
	public String[] getInteractionSpecsSupported() {
		return null;
	}

	/**
	 * Returns if the Interaction implementation of this adapter supports the
	 *<p>
	 *	public boolean execute(InteractionSpec is, Record input, Record output)
	 *<p>
	 * method.
	 *
	 * @return	true => support the 3-argument execute() method; false otherwise
	 */
	public boolean supportsExecuteWithInputAndOutputRecord() {
		return false;
	}

	/**
	 * Returns if the Interaction implementation of this adapter supports the
	 *<p>
	 *	public Record execute(InteractionSpec iSpec, Record input)
	 *<p>
	 * method.
	 *
	 * @return	true => support the 2-argument execute() method; false otherwise
	 */
	public boolean supportsExecuteWithInputRecordOnly() {
		return false;
	}

	/**
	 * Returns if this adapter implements the LocalTransaction interface and
	 * supports local transaction demarcation on the underlying EIS instance
	 * through the LocalTransaction interface.
	 *
	 * @return	true => support local transaction; false => otherwise
	 */
	public boolean supportsLocalTransactionDemarcation() {
		return false;
	}
}
