/*
 * EclipseLoginModule
 * Created on Mar 11, 2005
 *
 * A version of VistaLoginModule for use with Eclipse as a plugin
 */
package gov.va.med.foundations.security.vistalink;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.adapter.cci.VistaLinkConnectionFactory;
import gov.va.med.foundations.adapter.cci.VistaLinkResourceException;
import gov.va.med.foundations.adapter.record.LoginsDisabledFaultException;
import gov.va.med.foundations.adapter.record.NoJobSlotsAvailableFaultException;
import gov.va.med.foundations.adapter.record.VistaLinkFaultException;
import gov.va.med.foundations.net.VistaSocketException;
import gov.va.med.foundations.utilities.ExceptionUtils;
import gov.va.med.foundations.utilities.FoundationsException;
import gov.va.med.foundations.utilities.VistaKernelHashCountLimitExceededException;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.resource.ResourceException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


@SuppressWarnings("rawtypes")
/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EclipseLoginModule {
	
	/**
	 * map key to store/retrieve server IP address for the <code>VistaLoginModule</code> configuration option map. 
	 * You need these keys to pass login options from a JAAS configuration to the VistaLoginModule.
	 * Use the values of these keys as the keys in which to set the server IP address
	 * and server port into the configuration options that are passed back to <code>VistaLoginModule</code>.
	 */
	public static final String SERVER_ADDRESS_KEY = "gov.va.med.foundations.security.vistalink.ServerAddressKey";
	/**
	 * map key to store/retrieve server port for the <code>VistaLoginModule</code> configuration option map. 
	 * You need these keys to pass login options from a JAAS configuration to the VistaLoginModule.
	 * Use the values of these keys as the keys in which to set the server IP address
	 * and server port into the configuration options that are passed back to <code>VistaLoginModule</code>.
	 */
	public static final String SERVER_PORT_KEY = "gov.va.med.foundations.security.vistalink.ServerPortKey";

	/**
	 *  keep track of the JAAS subject passed in from the LoginContext
	 */
	private Subject subject;
	/**
	 * keep track of the principal created/returned by this loginmodule
	 */
	private VistaKernelPrincipalImpl userPrincipal;

	/**
	 * keep track of JAAS options passed in
	 */
	private Map options;

	/**
	 * keep track of JAAS callback handler passed in
	 */
	private CallbackHandler callbackHandler;

	@SuppressWarnings("unused")
	/** 
	 * keep track of JAAS shared state (not used so far)
	 */
	private Map sharedState;

	// Initialize Logger instance to be used by this class
	private static final Logger logger = Logger.getLogger(EclipseLoginModule.class);

	/*
	 * Connection factory -- needs to be global, used in several places
	 */
	private VistaLinkConnectionFactory myVCF;

	/*
	 * global timeout value
	 */
	private int timeoutInSeconds = 0;

	/**
	 * keeps track of whether the CCOW is being used for this login
	 */
	private boolean ccowLogonMode = false;

	/**
	 * timeout value to use when we don't have a timeout value from M (i.e., kernel auto-logon)
	 */
	private static final int AUTOLOGON_TIMEOUT_VALUE = 200;
	
	private boolean loginSucceeded = false;
	private boolean subjectProvided = false;
	public static String stringVal = "";
	
	public EclipseLoginModule() {
/*
		try {
			stringVal = "set";
		} catch (Exception e) {
			throw new RuntimeException("Error in EclipseLogin creation set value "+e.getMessage());

		}
*/
	}
	/**
	 * Should never be called by an application directly. Instead, this method is invoked behind the scenes by the proxy
	 * of the JAAS LoginContext.
	 * <p>Part of the JAAS interface for a login module; initializes the login module.
	 * @param subject the subject to be authenticated.
	 * @param callbackHandler a callback handler for communicating with the end user. The
	 * VistaLoginModule login module does not make use of this.
	 * @param sharedState state shared with other configuration login modules. Not used by
	 * the VistaLoginModule login module.
	 * @param options This is where the configuration options passed to the LoginContext are 
	 * then passed to the LoginModule.
	 */
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
		this.subject = subject;
		this.options = options;
		this.sharedState = sharedState;
		this.callbackHandler = callbackHandler;
	}
	
	// ven/smh -- let's see if that fixes the undef bug.
	public void initialize(Subject subject)
	{
		this.subject = subject;
	}
	
	public String getServer() {
		return (String) this.options.get(SERVER_ADDRESS_KEY);
	}
	
	public String getPort(){
		return (String) this.options.get(SERVER_PORT_KEY);
	}

	/**
	 * Should never be called by an application directly. Instead, this method is invoked behind the scenes by the proxy
	 * of the JAAS LoginContext.
	 * <p>When an application invokes login() on the LoginContext, the LoginContext calls this method to
	 * initiate a login to a VistaLink M server. Once a successful login has occurred, the authenticated connection will
	 * be stored in the JAAS subject, in a VistaKernelPrincipal.
	 * @return true if the authentication succeeded, or false if this LoginModule should be 
	 * ignored.
	 * @throws VistaLoginModuleException a VistaLoginModuleException is thrown if the login for this module fails.
	 * @throws VistaLoginModuleLoginsDisabledException thrown if logins are disabled
	 * @throws VistaLoginModuleNoJobSlotsAvailableException thrown if no job slots are available
	 * @throws VistaLoginModuleNoPathToListenerException thrown if the specified listener can't be reached
	 * @throws VistaLoginModuleTooManyInvalidAttemptsException thrown if too many bad login attempts are made
	 * @throws VistaLoginModuleUserCancelledException thrown if user cancels the login
	 * @throws VistaLoginModuleUserTimedOutException thrown if user times out of the login
	 */

	public boolean login()
		throws
			VistaLoginModuleException,
			VistaLoginModuleLoginsDisabledException,
			VistaLoginModuleNoJobSlotsAvailableException,
			VistaLoginModuleNoPathToListenerException,
			VistaLoginModuleTooManyInvalidAttemptsException,
			VistaLoginModuleUserCancelledException,
			VistaLoginModuleUserTimedOutException {
		VistaLinkConnection myConnection = null;
		SecurityResponseFactory securityResponseFactory = null;
		String serverAddress = "";
		int serverPort = -1;
		String exceptionMessage = "VistaLoginModule login method failed.";
		loginSucceeded = false;

		// get the server IP and port from the JAAS setup
		try {
			String str = (String) options.get(SERVER_ADDRESS_KEY);
			serverAddress = str;
			str = (String) options.get(SERVER_PORT_KEY);
			serverPort = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			String errMsg =
				"Error converting port string to integer from the login configuration; port string was '"
					+ ((String) options.get(SERVER_PORT_KEY));
			throw new VistaLoginModuleException(errMsg, e);
		}

		// get the connection factory
		try {

			myVCF = VistaLinkConnectionFactory.getVistaLinkConnectionFactory(serverAddress, serverPort);
			myConnection = (VistaLinkConnection) myVCF.getConnection();
			securityResponseFactory = new SecurityResponseFactory();
		} catch (VistaLinkResourceException e) {
			// throw special exception types if special embedded exception types is found
			// connector code already logged the exception
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			if (ExceptionUtils.getNestedExceptionByClass(e, NoJobSlotsAvailableFaultException.class) != null) {
				throw new VistaLoginModuleNoJobSlotsAvailableException(e);
			} else if (ExceptionUtils.getNestedExceptionByClass(e, LoginsDisabledFaultException.class) != null) {
				throw new VistaLoginModuleLoginsDisabledException(e);
			} else if (ExceptionUtils.getNestedExceptionByClass(e, ConnectException.class) != null) {
				throw new VistaLoginModuleNoPathToListenerException(e);
			}
			throw new VistaLoginModuleException(exceptionMessage, e);

		} catch (Exception e) { //(ResourceException e) {

			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(exceptionMessage, e);
			}
			throw new VistaLoginModuleException(exceptionMessage, e);
		}

		// do the complete authentication/logon
		// all non-VistaLoginModuleException exceptions thrown are caught/processed in lower level methods.
		//   usually the lower level method catches that exception and throws a VistaLoginModuleException,
		//   which is then passed through here up to LoginContext and the calling app.

		doLogon(myConnection, securityResponseFactory);
		loginSucceeded = true;
		
		return true;
	}

	/**
	 * Do the logon to the M system, including a/v code, division selection, and new verify code entry
	 * @param myConnection the connection to use
	 * @param securityResponseFactory factory to create responses from
	 * @throws VistaLoginModuleException a VistaLoginModuleException is thrown if the login for this module fails.
	 * @throws VistaLoginModuleTooManyInvalidAttemptsException thrown if too many bad login attempts are made
	 * @throws VistaLoginModuleUserCancelledException thrown if user cancels the login
	 * @throws VistaLoginModuleUserTimedOutException thrown if user times out of the login
	 */

	@SuppressWarnings("unchecked")
	private void doLogon(VistaLinkConnection myConnection, SecurityResponseFactory securityResponseFactory)
		throws
			VistaLoginModuleException,
			VistaLoginModuleTooManyInvalidAttemptsException,
			VistaLoginModuleUserCancelledException,
			VistaLoginModuleUserTimedOutException {

		Vector postSignInText = null;
		Callback[] calls = null;
		boolean enteredVerifyCodeWasNull = false; // used by CVC dialog to enable/disable prompt for old v/c 
		int tryCount = 0;

		// M-side setup, get intro text
		VistaSetupAndIntroTextInfo setupInfo = new VistaSetupAndIntroTextInfo();
		// set the port into the SetupInfo object for display with logon screen
		setupInfo.setPort(Integer.parseInt((String) options.get(SERVER_PORT_KEY)));
		// initialize a logonResponseData object in case auto-signon is returned when setup/intro text is called
		SecurityDataLogonResponse autoLogonResponseData =
			getIntroductoryTextAndSetupInfo(myConnection, setupInfo, securityResponseFactory);

		// if Kernel auto-signon occured, logonResponseData will be populated
		if (autoLogonResponseData != null) {
			timeoutInSeconds = AUTOLOGON_TIMEOUT_VALUE;
			if (logger.isDebugEnabled()) {
				logger.debug("Result autoLogonResponseData: " + autoLogonResponseData.getResultType());
			}
			// get post-sign-in text for later
			postSignInText = autoLogonResponseData.getPostSignInText();
			// if we need to change verify code or select a division, do it
			if (autoLogonResponseData.getResultType() == SecurityResponse.RESULT_PARTIAL) {

				doSelectDivisionAndOrChangeVc(
					myConnection,
					autoLogonResponseData,
					setupInfo.getLogonRetryCount(),
					securityResponseFactory,
					enteredVerifyCodeWasNull);
			}

		} else {
			// Kernel auto-signon did not occur, so, normal processing here.
			String exceptionMessage = "Kernel Sign-on Error during login: ";

			// get the non-user-specific timeout to use at first
			timeoutInSeconds = setupInfo.getTimeout();

			try {
				// do while true: means login will be repeated until an exception is thrown.
				// when we exceed the max signons on the M side, an exception is thrown. 
				do {
					tryCount++;
					// callback START (get access/verify codes)
					calls = new Callback[1];
					CallbackLogon avCbh = new CallbackLogon(setupInfo, timeoutInSeconds, tryCount);
					calls[0] = avCbh;
					callbackHandler.handle(calls);
					if (avCbh.getSelectedOption() == CallbackLogon.KEYPRESS_CANCEL) {
						logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
						throw new VistaLoginModuleUserCancelledException("User cancelled access/verify code sign on.");
					} else if (avCbh.getSelectedOption() == CallbackLogon.KEYPRESS_TIMEOUT) {
						logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
						throw new VistaLoginModuleUserTimedOutException("User timed out during access/verify code sign on.");
					} else if (avCbh.getSelectedOption() != CallbackLogon.KEYPRESS_OK) {
						logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
						throw new VistaLoginModuleUserCancelledException("Access/verify code sign on completed without user pressing OK.");
					}
					// callback END
					SecurityRequest requestVO = null;
					if (avCbh.getToken().length() > 0) {
						this.ccowLogonMode = true;
						requestVO = SecurityRequestFactory.getAVLogonRequest(avCbh.getToken());
					} else {
						String accessCode = new String(avCbh.getAccessCode());
						String verifyCode = new String(avCbh.getVerifyCode());
						boolean requestCvc = avCbh.getRequestCvc();
						if ((accessCode.indexOf(';') == -1) && (verifyCode.trim().length() == 0)) {
							enteredVerifyCodeWasNull = true;
						}
						requestVO = SecurityRequestFactory.getAVLogonRequest(accessCode, verifyCode, requestCvc);
					}

					if (logger.isDebugEnabled()) {
						logger.debug("-> sending " + SecurityRequestFactory.MSG_ACTION_LOGON);
					}

					SecurityDataLogonResponse logonResponseData =
						(SecurityDataLogonResponse) myConnection.executeInteraction(requestVO, securityResponseFactory);

					if (logger.isDebugEnabled()) {
						logger.debug("Result from logonResposeData: " + logonResponseData.getResultType());
					}

					postSignInText = logonResponseData.getPostSignInText();
					// need to change verify code or select a division
					if (logonResponseData.getResultType() == SecurityResponse.RESULT_PARTIAL) {

						doSelectDivisionAndOrChangeVc(
							myConnection,
							logonResponseData,
							setupInfo.getLogonRetryCount(),
							securityResponseFactory,
							enteredVerifyCodeWasNull);

						// if got here, change v/c and/or select division actions were successful
						break;

					} else if (logonResponseData.getResultType() == SecurityResponse.RESULT_SUCCESS) {
						// if got here, original signon was successful
						if (logger.isDebugEnabled()) {
							logger.debug("Result from logonResposeData was successful: " + logonResponseData.getResultType());
						}
						break;

					} else {
						// there was an error in the signon. Display error message.
						Vector errorMessages = new Vector();
						errorMessages.add(logonResponseData.getResultMessage());
						doCallbackConfirm(
							myConnection,
							securityResponseFactory,
							errorMessages,
							CallbackConfirm.ERROR_MESSAGE,
							"Error in signon Login Error");
						if (logger.isDebugEnabled()) {
							logger.debug("Result general error in the signon: ");
						}
					}
				} while (true);
			} catch (ParserConfigurationException e) {
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				if (logger.isEnabledFor(Level.ERROR)) {
					logger.error(exceptionMessage, e);
				}
				throw new VistaLoginModuleException(exceptionMessage, e);
			} catch (UnsupportedCallbackException e) {
				// don't need to log here, it's already logged lower down
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				throw new VistaLoginModuleException(exceptionMessage, e);
			} catch (IOException e) {
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				if (logger.isEnabledFor(Level.ERROR)) {
					logger.error(exceptionMessage, e);
				}
				throw new VistaLoginModuleException(exceptionMessage, e);
			} catch (VistaKernelHashCountLimitExceededException e) {
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				if (logger.isEnabledFor(Level.ERROR)) {
					logger.error(exceptionMessage, e);
				}
				throw new VistaLoginModuleException(exceptionMessage, e);
			} catch (SecurityTooManyInvalidLoginAttemptsFaultException e) {
				// logged by a lower level already
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				String errMsg = "Login failed due to too many invalid login attempts.";
				throw new VistaLoginModuleTooManyInvalidAttemptsException(errMsg, e);
			} catch (SecurityFaultException e) {
				// logged by a lower level already
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				String errMsg = "Security fault occured on the M system.";
				throw new VistaLoginModuleException(errMsg, e);
			} catch (VistaLinkFaultException e) {
				// logged by a lower level already
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				throw new VistaLoginModuleException(exceptionMessage, e);
			} catch (FoundationsException e) {
				// connector code already logged
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				throw new VistaLoginModuleException(exceptionMessage, e);
			}
		}

		// If got here, login was successful; do post-authentication/logon activities
		// 1. display post-sign-in text if there are lines of text to display
		if ((postSignInText.size() > 0) && (!this.ccowLogonMode)) {
			doCallbackConfirm(
				myConnection,
				securityResponseFactory,
				postSignInText,
				CallbackConfirm.POST_TEXT_MESSAGE,
				"Post-sign-in Text");
		}
		// 2. get user demographics
		getUserDemographicsAndBuildPrincipal(myConnection, securityResponseFactory);
		// 3. add authenticated connection to user principal
		userPrincipal.setAuthenticatedConnection(myConnection);
	}

	/**
	 * Perform pre-authentication setup and get the introductory text and other server information 
	 * from the M system. If Kernel auto-signon is triggered, however, the return type will be 
	 * for a successful logon. 
	 * @param myConnection the connection to use
	 * @param introTextAndServerInfo object to return introductory text / server info in
	 * @param securityResponseFactory factory to create responses from
	 * @return SecurityDataLogonResponse If Kernel auto-signon is triggered, a logon response object is returned.
	 * Otherwise (normal processing) null is returned.
	 * @throws VistaLoginModuleException thrown if an error is encountered
	 */
	private SecurityDataLogonResponse getIntroductoryTextAndSetupInfo(
		VistaLinkConnection myConnection,
		VistaSetupAndIntroTextInfo introTextAndServerInfo,
		SecurityResponseFactory securityResponseFactory)
		throws VistaLoginModuleException {

		SecurityDataLogonResponse returnVal = null;
		String exceptionMessage = "Error doing M setup/introduction text retrieval: ";

		try {
			SecurityRequest requestVO = SecurityRequestFactory.getAVSetupAndIntroTextRequest();

			if (logger.isDebugEnabled()) {
				logger.debug("-> sending " + SecurityRequestFactory.MSG_ACTION_SETUP_AND_INTRO_TEXT);
			}

			Object responseDataObj = myConnection.executeInteraction(requestVO, securityResponseFactory);

			if (responseDataObj instanceof SecurityDataSetupAndIntroTextResponse) {

				SecurityDataSetupAndIntroTextResponse responseData =
					(SecurityDataSetupAndIntroTextResponse) responseDataObj;
				if (logger.isDebugEnabled()) {
					logger.debug("Result SecurityDataSetupAndIntroTextResponse responseData: " + responseData.getResultType());
				}

				VistaSetupAndIntroTextInfo myInfo = responseData.getSetupAndIntroTextInfo();

				// copy results into output object
				introTextAndServerInfo.setDevice(myInfo.getDevice());
				introTextAndServerInfo.setIntroductoryText(myInfo.getIntroductoryText());
				introTextAndServerInfo.setServerName(myInfo.getServerName());
				introTextAndServerInfo.setUci(myInfo.getUci());
				introTextAndServerInfo.setVolume(myInfo.getVolume());
				introTextAndServerInfo.setLogonRetryCount(myInfo.getLogonRetryCount());
				introTextAndServerInfo.setTimeout(myInfo.getTimeout());

			} else if (responseDataObj instanceof SecurityDataLogonResponse) {

				// return the logon response to the caller
				returnVal = (SecurityDataLogonResponse) responseDataObj;

			} else {

				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				String errMessage =
					exceptionMessage
						+ "; Unexpected response class for "
						+ SecurityRequestFactory.MSG_ACTION_SETUP_AND_INTRO_TEXT
						+ " request: "
						+ responseDataObj.getClass().getName();
				if (logger.isEnabledFor(Level.ERROR)) {
					logger.error(errMessage);
				}
				throw new VistaLoginModuleException(errMessage);
			}
		} catch (ParserConfigurationException e) {
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(exceptionMessage, e);
			}
			throw new VistaLoginModuleException(exceptionMessage, e);
		} catch (SecurityFaultException e) {
			// logged lower down
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			String errMsg = "Security fault occured on the M system.";
			throw new VistaLoginModuleException(errMsg, e);
		} catch (VistaLinkFaultException e) {
			// logged lower down
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			throw new VistaLoginModuleException(exceptionMessage, e);
		} catch (FoundationsException e) {
			// connector code already logged
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			throw new VistaLoginModuleException(exceptionMessage, e);
		}

		return returnVal;
	}
	/*
	 * @author Sam Habiel
	 * Silent log-in, generally useful; but also good to support Mac OS as AWT in Eclipse
	 * doesn't work as of Java 7.
	 */
	public boolean loginSilent(String serverAddress, int serverPort, String ac, String vc)
			throws
				VistaLoginModuleException,
				VistaLoginModuleLoginsDisabledException,
				VistaLoginModuleNoJobSlotsAvailableException,
				VistaLoginModuleNoPathToListenerException
	{
			
		if(logger.isDebugEnabled()) 
		{
			logger.debug("Entered Silent Login");
		}

		VistaLinkConnection myConnection = null;
		SecurityResponseFactory securityResponseFactory = null;
		String exceptionMessage = "VistaLoginModule login method failed.";
		loginSucceeded = false;

		try {
			myVCF = VistaLinkConnectionFactory.getVistaLinkConnectionFactory(serverAddress, serverPort);
			myConnection = (VistaLinkConnection) myVCF.getConnection();
		} catch (ResourceException e) {
			throw new VistaLoginModuleException(exceptionMessage, e);
		}
		securityResponseFactory = new SecurityResponseFactory();
		
		try 
		{
			// Create message
			SecurityRequest requestVO = SecurityRequestFactory.getAVSetupAndIntroTextRequest();

			if (logger.isDebugEnabled()) 
			{
				logger.debug("-> sending " + SecurityRequestFactory.MSG_ACTION_SETUP_AND_INTRO_TEXT);
			}

			// Send to XUS SETUP and XUS INTRO TEXT
			Object responseDataObj = myConnection.executeInteraction(requestVO, securityResponseFactory);

			// If we didn't crash (vi exceptions), this must be true. 
			assert(responseDataObj instanceof SecurityDataSetupAndIntroTextResponse);

			// log for my entertainment
			if (logger.isDebugEnabled()) 
			{
			SecurityDataSetupAndIntroTextResponse responseData =
					(SecurityDataSetupAndIntroTextResponse) responseDataObj;
				{
					logger.debug("Result SecurityDataSetupAndIntroTextResponse responseData: " + responseData.getResultType());
				}
			}
			
			// Create log-in message for XUS AV CODE
			requestVO = SecurityRequestFactory.getAVLogonRequest(ac, vc, false);

			if (logger.isDebugEnabled()) {
				logger.debug("-> sending " + SecurityRequestFactory.MSG_ACTION_LOGON);
			}

			// and call XUS AV CODE
			SecurityDataLogonResponse logonResponseData =
					(SecurityDataLogonResponse) myConnection.executeInteraction(requestVO, securityResponseFactory);

			if (logger.isDebugEnabled()) {
				logger.debug("Result from logonResposeData: " + logonResponseData.getResultType());
			}
			
			// XUS AV CODE says we can't log-in or we need to change our verify code.
			if (logonResponseData.getResultType() == SecurityResponse.RESULT_PARTIAL ||
					logonResponseData.getResultType() == SecurityResponse.RESULT_FAILURE)
			{
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				Shell shell = window.getShell();
				MessageDialog.openError(shell, "Log-in Failed", logonResponseData.getResultMessage());
			}

			else
			{
				// Call XUS GET USER INFO
				assert(logonResponseData.getResultType() == SecurityResponse.RESULT_SUCCESS);
				getUserDemographicsAndBuildPrincipal(myConnection, securityResponseFactory);
				userPrincipal.setAuthenticatedConnection(myConnection);
				loginSucceeded = true;
			}
		}
		
		
		
		catch (ParserConfigurationException e) {
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(exceptionMessage, e);
			}
			throw new VistaLoginModuleException(exceptionMessage, e);
		} catch (SecurityFaultException e) {
			// logged lower down
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			String errMsg = "Security fault occured on the M system.";
			throw new VistaLoginModuleException(errMsg, e);
		} catch (VistaLinkFaultException e) {
			// logged lower down
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			throw new VistaLoginModuleException(exceptionMessage, e);
		} catch (FoundationsException e) {
			// connector code already logged
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			throw new VistaLoginModuleException(exceptionMessage, e);
		}
		
		return loginSucceeded;
	}

	/**
	 * Called by both the "auto-logon" and regular logon code to process a partially successful login
	 * (i.e., either the v/c needs to be changed or a division needs to be selected.)
	 * (nb: not called by silent log-on... b/c it's silent)
	 * @param myConnection connection to use
	 * @param responseData the logon response returned from the logon attempt that was partially successful
	 * @param retryLogonCount # of times to retry the login
	 * @param securityResponseFactory factory to create response objects from
	 * @throws VistaLoginModuleException
	 */

	private void doSelectDivisionAndOrChangeVc(
		VistaLinkConnection myConnection,
		SecurityDataLogonResponse responseData,
		int retryLogonCount,
		SecurityResponseFactory securityResponseFactory,
		boolean enteredVerifyCodeWasNull)
		throws VistaLoginModuleException {

		if (logger.isDebugEnabled()) {
			logger.debug(
				"Need new Verify Code: "
					+ responseData.getNeedNewVerifyCode()
					+ " Need to select Divisions: "
					+ responseData.getNeedDivisionSelection());
		}

		// change verify code					
		if (responseData.getNeedNewVerifyCode()) {
			doChangeVC(
				myConnection,
				responseData.getResultMessage(),
				responseData.getCvcHelpText(),
				retryLogonCount,
				securityResponseFactory,
				enteredVerifyCodeWasNull);

		} else if (responseData.getNeedDivisionSelection()) {
			// need to select a division
			doSelectDivision(
				(TreeMap) responseData.getDivisionList(),
				myConnection,
				retryLogonCount,
				securityResponseFactory);
		} else {
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			throw new VistaLoginModuleException("Logon response was partial success, but M provided no reason for why login is only a partial success!");
		}
	}

	/**
	 * Method to change the verify code.
	 * @param myConnection the connection to use
	 * @param changeVCMessage text message from M explaining why CVC is needed
	 * @param cvcHelpText text from M to display if help button is pressed on CVC dialog
	 * @param retryLogonCount number of times M says to retry logon
	 * @param securityResponseFactory factory to create responses from
	 * @throws VistaLoginModuleException a VistaLoginModuleException is thrown if the login for this module fails.
	 * @throws VistaLoginModuleTooManyInvalidAttemptsException thrown if too many bad login attempts are made
	 * @throws VistaLoginModuleUserCancelledException thrown if user cancels the login
	 * @throws VistaLoginModuleUserTimedOutException thrown if user times out of the login
	 */

	@SuppressWarnings("unchecked")
	private void doChangeVC(
		VistaLinkConnection myConnection,
		String changeVCMessage,
		String cvcHelpText,
		int retryLogonCount,
		SecurityResponseFactory securityResponseFactory,
		boolean enteredVerifyCodeWasNull)
		throws
			VistaLoginModuleException,
			VistaLoginModuleTooManyInvalidAttemptsException,
			VistaLoginModuleUserCancelledException,
			VistaLoginModuleUserTimedOutException {

		String vcOld = "", vcNew = "", vcNewCheck = "";
		int tryCount = 0;
		String exceptionMessage = "Change verify code failed: ";

		try {
			do {
				// callback START (get old, new and check vc)
				Callback[] calls = new Callback[1];
				CallbackChangeVc changeVcCbh =
					new CallbackChangeVc(changeVCMessage, cvcHelpText, timeoutInSeconds, enteredVerifyCodeWasNull);
				calls[0] = changeVcCbh;
				callbackHandler.handle(calls);
				vcOld = changeVcCbh.getOldVerifyCode();
				vcNew = changeVcCbh.getNewVerifyCode();
				vcNewCheck = changeVcCbh.getNewVerifyCodeCheck();
				if (changeVcCbh.getSelectedOption() == CallbackChangeVc.KEYPRESS_CANCEL) {
					logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
					throw new VistaLoginModuleUserCancelledException("User cancelled changing of verify code.");
				} else if (changeVcCbh.getSelectedOption() == CallbackChangeVc.KEYPRESS_TIMEOUT) {
					logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
					throw new VistaLoginModuleUserTimedOutException("User changing of verify code timed out.");
				} else if (changeVcCbh.getSelectedOption() != CallbackChangeVc.KEYPRESS_OK) {
					logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
					throw new VistaLoginModuleUserCancelledException("User did not press OK when changing verify code.");
				}
				// callback END

				if (!vcNew.equals(vcNewCheck)) {
					// error if confirmation doesn't equal new vc -- this is *not* checked by the M side call
					Vector errorMessages = new Vector();
					errorMessages.add("The confirmation code does not match.");
					doCallbackConfirm(
						myConnection,
						securityResponseFactory,
						errorMessages,
						CallbackConfirm.ERROR_MESSAGE,
						"Change Verify Code Error");
					tryCount++;
					if (tryCount >= retryLogonCount) {
						logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
						throw new VistaLoginModuleTooManyInvalidAttemptsException("Change verify code failed due to invalid user entry.");
					}

				} else {

					SecurityRequest requestVO =
						SecurityRequestFactory.getAVUpdateVCRequest(
							vcOld.toUpperCase(),
							vcNew.toUpperCase(),
							vcNewCheck.toUpperCase());

					if (logger.isDebugEnabled()) {
						logger.debug("-> sending " + SecurityRequestFactory.MSG_ACTION_UPDATE_VC);
					}

					SecurityDataChangeVcResponse responseData =
						(SecurityDataChangeVcResponse) myConnection.executeInteraction(requestVO, securityResponseFactory);

					if (logger.isDebugEnabled()) {
						logger.debug("Result: SecurityDataChangeVcResponse responseData: " + responseData.getResultType());
					}

					// now check if need to select divisions after vc changed
					if (responseData.getResultType() == SecurityResponse.RESULT_PARTIAL) {
						doCvcConfirm(myConnection, securityResponseFactory);
						// need to select a division
						if (responseData.getNeedDivisionSelection()) {

							if (logger.isDebugEnabled()) {
								logger.debug("Need to select divisions: " + responseData.getNeedDivisionSelection());
							}

							doSelectDivision(
								(TreeMap) responseData.getDivisionList(),
								myConnection,
								retryLogonCount,
								securityResponseFactory);
						}
						// if we made it here, division selection was successful, so break
						break;

					} else if (responseData.getResultType() == SecurityResponse.RESULT_SUCCESS) {
						// if we had success, then break
						doCvcConfirm(myConnection, securityResponseFactory);
						break;

					} else {
						// display error
						Vector errorMessages = new Vector();
						errorMessages.add("Change Verify Code failed: ");
						errorMessages.add(responseData.getResultMessage());
						doCallbackConfirm(
							myConnection,
							securityResponseFactory,
							errorMessages,
							CallbackConfirm.ERROR_MESSAGE,
							"Change Verify Code Error");
						tryCount++;
						if (tryCount >= retryLogonCount) {
							logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
							throw new VistaLoginModuleTooManyInvalidAttemptsException("Change verify code failed due to invalid user entry.");
						}
					}
				}
			} while (true);

		} catch (UnsupportedCallbackException e) {
			// don't need to log here, it's already logged lower down
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			throw new VistaLoginModuleException(exceptionMessage, e);
		} catch (IOException e) {
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(exceptionMessage, e);
			}
			throw new VistaLoginModuleException(exceptionMessage, e);
		} catch (ParserConfigurationException e) {
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(exceptionMessage, e);
			}
			throw new VistaLoginModuleException(exceptionMessage, e);
		} catch (SecurityTooManyInvalidLoginAttemptsFaultException e) {
			// logged lower down
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			throw new VistaLoginModuleTooManyInvalidAttemptsException(exceptionMessage, e);
		} catch (SecurityFaultException e) {
			// logged lower down
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			String errMsg = "Security fault occured on the M system.";
			throw new VistaLoginModuleException(errMsg, e);
		} catch (VistaLinkFaultException e) {
			// logged lower down
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			throw new VistaLoginModuleException(exceptionMessage, e);
		} catch (FoundationsException e) {
			// connector code already logged
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			throw new VistaLoginModuleException(exceptionMessage, e);
		}
	}

	/**
	 * Selects a division.
	 * @param divisionList list of divisions to select from
	 * @param myConnection the connection to use
	 * @param retryLogonCount how many times to retry any particular dialog
	 * @param securityResponseFactory factory to create responses from
	 * @throws VistaLoginModuleException a VistaLoginModuleException is thrown if the login for this module fails.
	 * @throws VistaLoginModuleTooManyInvalidAttemptsException thrown if too many bad login attempts are made
	 * @throws VistaLoginModuleUserCancelledException thrown if user cancels the login
	 * @throws VistaLoginModuleUserTimedOutException thrown if user times out of the login
	 */

	@SuppressWarnings("unchecked")
	private void doSelectDivision(
		TreeMap divisionList,
		VistaLinkConnection myConnection,
		int retryLogonCount,
		SecurityResponseFactory securityResponseFactory)
		throws
			VistaLoginModuleException,
			VistaLoginModuleTooManyInvalidAttemptsException,
			VistaLoginModuleUserCancelledException,
			VistaLoginModuleUserTimedOutException {

		// if logged on via CCOW token, DUZ(2) will be defined
		// when the token is used to logon, even if VALIDAV reports
		// that selecting a division is needed

		if (!this.ccowLogonMode) { 

			int tryCount = 0;
			String exceptionMessage = "Division selection failed: ";
			try {
				do {
					// callback START (select division)
					Callback[] calls = new Callback[1];
					CallbackSelectDivision divCbh = new CallbackSelectDivision(divisionList, timeoutInSeconds);
					calls[0] = divCbh;
					callbackHandler.handle(calls);
					String selectedDivision = divCbh.getSelectedDivisionIen();
					if (divCbh.getSelectedOption() == CallbackSelectDivision.KEYPRESS_CANCEL) {
						logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
						throw new VistaLoginModuleUserCancelledException("User cancelled division selection.");
					} else if (divCbh.getSelectedOption() == CallbackSelectDivision.KEYPRESS_TIMEOUT) {
						logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
						throw new VistaLoginModuleUserTimedOutException("User division selection timed out.");
					} else if (divCbh.getSelectedOption() != CallbackSelectDivision.KEYPRESS_OK) {
						logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
						throw new VistaLoginModuleUserCancelledException("Select Division failed without user pressing OK.");
					}
					// callback END

					SecurityRequest requestVO = SecurityRequestFactory.getAVLogonSelectDivisionRequest(selectedDivision);

					if (logger.isDebugEnabled()) {
						logger.debug("-> sending " + SecurityRequestFactory.MSG_ACTION_SELECT_DIVISION);
					}

					SecurityDataSelectDivisionResponse responseData =
						(SecurityDataSelectDivisionResponse) myConnection.executeInteraction(
							requestVO,
							securityResponseFactory);

					if (logger.isDebugEnabled()) {
						logger.debug("Result SecurityDataSelectDivisionResponse responseData: " + responseData.getResultType());
					}

					if (responseData.getResultType() == SecurityResponse.RESULT_SUCCESS) {

						// we're successful, break out of loop.
						break;

					} else {
						// display error
						Vector errorMessages = new Vector();
						errorMessages.add(responseData.getResultMessage());
						doCallbackConfirm(
							myConnection,
							securityResponseFactory,
							errorMessages,
							CallbackConfirm.ERROR_MESSAGE,
							"Select Division Error");
						// check retries
						tryCount++;
						if (tryCount >= retryLogonCount) {
							// logout to clear up the connection that has DUZ set up on the other end
							logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
							throw new VistaLoginModuleException("Division selection failed due to too many invalid user entries.");
						}
					}
				} while (true);
			} catch (ParserConfigurationException e) {
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				if (logger.isEnabledFor(Level.ERROR)) {
					logger.error(exceptionMessage, e);
				}
				throw new VistaLoginModuleException(exceptionMessage, e);
			} catch (UnsupportedCallbackException e) {
				// don't need to log here, it's already logged lower down
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				throw new VistaLoginModuleException(exceptionMessage, e);
			} catch (IOException e) {
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				if (logger.isEnabledFor(Level.ERROR)) {
					logger.error(exceptionMessage, e);
				}
				throw new VistaLoginModuleException(exceptionMessage, e);
			} catch (VistaSocketException e) {
				// don't need to log here, connector code already already logged
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				throw new VistaLoginModuleException(exceptionMessage, e);
			} catch (SecurityFaultException e) {
				// logged lower down
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				String errMsg = "Security Fault exception " + exceptionMessage;
				throw new VistaLoginModuleException(errMsg, e);
			} catch (VistaLinkFaultException e) {
				// logged lower down
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				throw new VistaLoginModuleException(exceptionMessage, e);
			} catch (FoundationsException e) {
				// connector code already logged
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				throw new VistaLoginModuleException(exceptionMessage, e);
			}
		}
	}

	/**
	 * Method to standardize the "change verify code" dialog confirmation
	 * @param myConnection the connection to use
	 * @param securityResponseFactory factory to create responses from
	 * @throws VistaLoginModuleException a VistaLoginModuleException is thrown if the login for this module fails.
	 * @throws VistaLoginModuleUserCancelledException thrown if user cancels the login
	 * @throws VistaLoginModuleUserTimedOutException thrown if user times out of the login
	 */

	@SuppressWarnings("unchecked")
	private void doCvcConfirm(VistaLinkConnection myConnection, SecurityResponseFactory securityResponseFactory)
		throws VistaLoginModuleException, VistaLoginModuleUserCancelledException, VistaLoginModuleUserTimedOutException {

		Vector infoMessages = new Vector();
		infoMessages.add("Change of Verify Code succeeded.");
		doCallbackConfirm(
			myConnection,
			securityResponseFactory,
			infoMessages,
			CallbackConfirm.INFORMATION_MESSAGE,
			"Change Verify Code Confirmation");
	}

	/**
	 * Method to call the confirm dialog for any kind of confirmation
	 * @param myConnection the connection to use
	 * @param securityResponseFactory the security response factory to use
	 * @param messageText the text of the message to display
	 * @param messageType the type of message (see types in CallbackConfirm class)
	 * @param contextDescription Describes the context of the dialog display. Used for window title (verbatim)
	 * and also pre-pended to exception text if an exception is returned.
	 * @param timeout timeout to use.
	 * @throws VistaLoginModuleException a VistaLoginModuleException is thrown if the login for this module fails.
	 * @throws VistaLoginModuleUserCancelledException thrown if user cancels the login
	 * @throws VistaLoginModuleUserTimedOutException thrown if user times out of the login
	 */

	private void doCallbackConfirm(
		VistaLinkConnection myConnection,
		SecurityResponseFactory securityResponseFactory,
		Vector messageText,
		int messageType,
		String contextDescription)
		throws VistaLoginModuleException, VistaLoginModuleUserCancelledException, VistaLoginModuleUserTimedOutException {

		String exceptionMessage = contextDescription + " error: ";
		try {
			Callback[] calls = new Callback[1];
			CallbackConfirm ccCbh = new CallbackConfirm(messageText, messageType, contextDescription, timeoutInSeconds);
			calls[0] = ccCbh;
			callbackHandler.handle(calls);
			if (ccCbh.getSelectedOption() == CallbackConfirm.KEYPRESS_CANCEL) {
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				throw new VistaLoginModuleUserCancelledException(contextDescription + ": User cancelled.");
			} else if (ccCbh.getSelectedOption() == CallbackConfirm.KEYPRESS_TIMEOUT) {
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				throw new VistaLoginModuleUserTimedOutException(contextDescription + ": User timed out.");
			} else if (ccCbh.getSelectedOption() != CallbackConfirm.KEYPRESS_OK) {
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				throw new VistaLoginModuleUserCancelledException(
					contextDescription + ": User did not press OK to close dialog.");
			}
		} catch (UnsupportedCallbackException e) {
			// don't need to log here, it's already logged lower down
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			throw new VistaLoginModuleException(exceptionMessage, e);
		} catch (IOException e) {
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(exceptionMessage, e);
			}
			throw new VistaLoginModuleException(exceptionMessage, e);
		}
	}

	/**
	 * Build a VistaKernelPrincipal that holds user demographics of the authenticated user
	 * 
	 * @param myConnection the connection to use
	 * @param securityResponseFactory factory to create responses from
	 * @throws VistaLoginModuleException
	 */

	private void getUserDemographicsAndBuildPrincipal(
		VistaLinkConnection myConnection,
		SecurityResponseFactory securityResponseFactory)
		throws VistaLoginModuleException {

		String exceptionMessage = "User Demographic retrieval failure: ";
		try {
			SecurityRequest requestVO = SecurityRequestFactory.getAVGetUserDemographicsRequest();

			if (logger.isDebugEnabled()) {
				logger.debug("-> sending " + SecurityRequestFactory.MSG_ACTION_USER_DEMOGRAPHICS);
			}

			SecurityDataUserDemographicsResponse responseData =
				(SecurityDataUserDemographicsResponse) myConnection.executeInteraction(requestVO, securityResponseFactory);

			if (logger.isDebugEnabled()) {
				logger.debug("Result: " + responseData.getResultType());
			}

			if (responseData.getResultType() == SecurityResponse.RESULT_SUCCESS) {
				userPrincipal = new VistaKernelPrincipalImpl(responseData.getUserDemographicsHashtable());
			} else {
				logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
				throw new VistaLoginModuleException(exceptionMessage + responseData.getResultMessage());
			}
		} catch (ParserConfigurationException e) {
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(exceptionMessage, e);
			}
			throw new VistaLoginModuleException(exceptionMessage, e);
		} catch (VistaSocketException e) {
			// don't need to log here, connector code already already logged
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			throw new VistaLoginModuleException(exceptionMessage, e);
		} catch (SecurityFaultException e) {
			// logged lower down
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			String errMsg = exceptionMessage + "Security fault occured on the M system.";
			throw new VistaLoginModuleException(errMsg, e);
		} catch (VistaLinkFaultException e) {
			// logged lower down
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			throw new VistaLoginModuleException(exceptionMessage, e);
		} catch (FoundationsException e) {
			// connector code already logged
			logoutConnectionBeforeLoginComplete(myConnection, securityResponseFactory);
			throw new VistaLoginModuleException(exceptionMessage, e);
		}
	}

	/**
	 * Should never be called by an application directly. Instead, this method is invoked behind the scenes by the proxy
	 * of the JAAS LoginContext.<p> Part of the JAAS interface for a login module. Since we don't have a two-phase
	 * login, this always returns true (and is irrelevant to the success or failure of a login).
	 * @throws LoginException this is never thrown by this implementation of commit().
	 * @return this implementation of commit() always returns true.
	 */
	@SuppressWarnings("unchecked")
	public boolean commit() throws LoginException {
		// Now it's time to add the principal to the subject
		if (!subject.getPrincipals().contains(userPrincipal)) {
			subject.getPrincipals().add(userPrincipal);
		}

		// hook for CCOW commit actions
		Callback[] calls = new Callback[1];
		CallbackCommit ccomCbh =
			new CallbackCommit(
				userPrincipal.getUserDemographicValue(VistaKernelPrincipal.KEY_NAME_NEWPERSON01),
				userPrincipal.getAuthenticatedConnection());
		calls[0] = ccomCbh;
		String exceptionMessage = "Failure during CallbackCommit phase of Login: ";
		try {
			callbackHandler.handle(calls);
		} catch (IOException e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(exceptionMessage, e);
			}
			throw new VistaLoginModuleException(exceptionMessage, e);
		} catch (UnsupportedCallbackException e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(exceptionMessage, e);
			}
			throw new VistaLoginModuleException(exceptionMessage, e);
		}

		return true;
	}

	/* VEN/SMH I overloaded the previous method so that I can
	 * get a new signature for just doing a commit with no UI work
	 */
	
	@SuppressWarnings("unchecked")
	public boolean commit(boolean silent) throws LoginException {
		if (!subject.getPrincipals().contains(userPrincipal)) {
			subject.getPrincipals().add(userPrincipal);
		}
		return true;
	}
	
	/**
	 * Should never be called by an application directly. Instead, this method is invoked behind the scenes by the proxy
	 * of the JAAS LoginContext.<p> Part of the JAAS interface for a login module. This loginmodule's implementation of
	 * this method calls M to cleanly shut down the connection to M. If we were to support an environment with multiple
	 * login modules, and the login for one of them failed, this method would be called to do any cleanup to back out of
	 * a partial login, which in the case of VistaLink, means clean up/tear down the existing connection to M.
	 * @return true if cleanup/logout on the M side succeeded
	 * @throws VistaLoginModuleException thrown if logging out on the M side fails.
	 */
/*
	public boolean abort() throws VistaLoginModuleException {

		if (userPrincipal != null) {

			if (userPrincipal.getAuthenticatedConnection() != null) {
				logout();
			}
		}
		// if logout() successful -- or no connection present -- return true
		return true;
	}
*/
	/**
	 * Log out before an authenticated connection has been placed in the userPrincipal
	 * (clears up the M side, useful when we're about to throw an exception). Don't want to throw any
	 * errors here, because we're calling this method immediately BEFORE throwing a LoginException
	 * in most cases.
	 * @param myConnection needed to shutdown the connection.
	 * @param mySecurityRequest A request object with an open raw connection to the M system.
	 */
	private void logoutConnectionBeforeLoginComplete(
		VistaLinkConnection myConnection,
		SecurityResponseFactory securityResponseFactory) {
		String exceptionMessage = "Error logging out connection before login was complete: ";
		try {
			if ((myConnection != null) && (securityResponseFactory != null)) {

				SecurityRequest requestVO = SecurityRequestFactory.getAVLogoutRequest();
				if (logger.isDebugEnabled()) {
					logger.debug("-> sending " + SecurityRequestFactory.MSG_ACTION_LOGOUT);
				}
				SecurityDataLogoutResponse responseData =
					(SecurityDataLogoutResponse) myConnection.executeInteraction(requestVO, securityResponseFactory);
				if (logger.isDebugEnabled()) {
					logger.debug("Result: " + responseData.getResultType());
				}
				if (responseData.getResultType() != SecurityResponse.RESULT_SUCCESS) {
					if (logger.isEnabledFor(Level.ERROR)) {
						logger.error("Logout failure: " + responseData.getResultMessage());
					}
				}
			}
		} catch (ParserConfigurationException e) {
			// other than logging, swallow this exception
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(exceptionMessage, e);
			}
		} catch (FoundationsException e) {
			// connector code already logged
			// swallow this exception
		} finally {

			// either way, close the connection in the factory/connection object too
			try {
				disposeConnection(myConnection);
			} catch (Exception err) {//(ResourceException err) {
				// other than logging, swallow this exception
				if (logger.isEnabledFor(Level.ERROR)) {
					logger.error(exceptionMessage, err);
				}
			}
		}

	}

	/**
	 * Should never be called by an application directly. Instead, this method is invoked behind the scenes by the proxy
	 * of the JAAS LoginContext.<p> For applications to call, to logout a user from an open connection/session to a
	 * VistaLink M server. Doing this drops the connection, freeing up resources on the M server.
	 * @return  true if the logout was successful
	 * @throws VistaLoginModuleException thrown if the logout fails on the M side.
	 */
	public boolean logout() throws VistaLoginModuleException {
		VistaLinkConnection myConnection = null;
		String exceptionMessage = "Could not contact the M server during logout: ";
		try {
			if (userPrincipal != null) {
				if (userPrincipal.getAuthenticatedConnection() != null) {
					// logout request to update the M side
					myConnection = userPrincipal.getAuthenticatedConnection();
					SecurityResponseFactory securityResponseFactory = new SecurityResponseFactory();
					SecurityRequest requestVO = SecurityRequestFactory.getAVLogoutRequest();

					if (logger.isDebugEnabled()) {
						logger.debug("-> sending " + SecurityRequestFactory.MSG_ACTION_LOGOUT);
					}

					SecurityDataLogoutResponse logoutResponse =
						(SecurityDataLogoutResponse) myConnection.executeInteraction(requestVO, securityResponseFactory);

					if (logger.isDebugEnabled()) {
						logger.debug("Result: " + logoutResponse.getResultType());
					}

					if (logoutResponse.getResultType() != SecurityResponse.RESULT_SUCCESS) {
						if (logger.isDebugEnabled()) {
							logger.debug("logout failed: " + logoutResponse.getResultMessage());
						}
						throw new VistaLoginModuleException("logout failed: " + logoutResponse.getResultMessage());
					}
				}
			}

		} catch (VistaSocketException e) {

			// don't need to log here, connector code already already logged
			throw new VistaLoginModuleException(exceptionMessage, e);

		} catch (FoundationsException e) {

			// connector code already logged
			throw new VistaLoginModuleException(exceptionMessage, e);

		} catch (ParserConfigurationException e) {

			throw new VistaLoginModuleException(exceptionMessage, e);

		} finally {

			// either way, close the connection in the factory/connection object too
			try {
				disposeConnection(myConnection);
			} catch (Exception err) { //(ResourceException err) {
				// other than logging, swallow this exception
				if (logger.isEnabledFor(Level.ERROR)) {
					logger.error(exceptionMessage, err);
				}
			}
		}
		return true;
	}

	/**
	 * Call to close a raw connection. 
	 * @param connectionToClose
	 * @throws ResourceException
	 */
		private void disposeConnection(VistaLinkConnection connectionToClose) throws Exception { //ResourceException {

		if (connectionToClose != null) {
			connectionToClose.close();
			connectionToClose = null;
		}
		myVCF = null;
	}

    public Subject getSubject() {
	if (!loginSucceeded && !subjectProvided)
	    return null;
	return subject;
    }
}