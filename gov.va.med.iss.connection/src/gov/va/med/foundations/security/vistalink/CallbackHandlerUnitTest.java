package gov.va.med.foundations.security.vistalink;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * Implements the JAAS CallbackHandler interface. Use with the <code>VistaLoginModule</code> to invoke a silent signon.
 * Intended for use in unit testing environments where logins must be called repetitively without user interaction.
 * *Not* for use in production environments, where users should be interactively prompted for signon credentials.
 * <ol>
 * <li>Pass access code, verify code and division as parameters when you create an instance of this callback
 * handler. 
 * <li>Pass the instance of the callback handler to the login context when you create the login context.
 * <li>Then, when <code>VistaLoginModule</code>'s <code>login</code> method (via the indirection of the
 * <code>LoginContext</code>) invokes this callback handler to collect user input for (access code, verify code, select
 * division), these values are already present and are handed back to the login module without any user interation.
 * </ol>
 * For example:
 * <pre>
 *     String cfgName = "RpcSampleServer";
 *     String accessCode = "joe.123";
 *     String verifyCode = "ebony.23";
 *     String division = "";
 * 
 *     // create the callbackhandler for JAAS login
 *     CallbackHandlerUnitTest cbhSilentSimple = 
 *        new CallbackHandlerUnitTest(accessCode, verifyCode, division);
 * 
 *     // create the JAAS LoginContext for login
 *     lc = new LoginContext(cfgName, cbhSilentSimple);
 * 
 *     // login to server
 *     lc.login();
 * </pre>
 * 
 * @see VistaLoginModule
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public final class CallbackHandlerUnitTest implements CallbackHandler {

	private static final Logger LOGGER = Logger.getLogger(CallbackHandlerUnitTest.class);

	private char[] accessCode;
	private char[] verifyCode;
	private String divisionIen = "";

	/**
	 * Creates a simple callback handler that handles the callbacks for
	 * logon (w/av code) and post-sign-in text only. It does not handle
	 * callbacks for error, select division, or change verify code..
	 * @param accessCode Access Code to use for logon
	 * @param verifyCode Verify Code to use for logon
	 * @param divisionIen IEN of division to select for multidivisional logins. If not needed, pass ah empty string.
	 */
	public CallbackHandlerUnitTest(String accessCode, String verifyCode, String divisionIen) {

		this.accessCode = accessCode.toCharArray();
		this.verifyCode = verifyCode.toCharArray();
		this.divisionIen = divisionIen;

	}

	/**
	 * The LoginModule calls this method to process callbacks
	 * @see javax.security.auth.callback.CallbackHandler#handle(Callback[])
	 */
	public void handle(Callback[] callbacks) throws UnsupportedCallbackException {

		for (int i = 0; i < callbacks.length; i++) {

			if (callbacks[i] instanceof CallbackLogon) {

				CallbackLogon avCbh = (CallbackLogon) callbacks[i];
				VistaSetupAndIntroTextInfo setupInfo = avCbh.getSetupAndIntroTextInfo();
// JLI 050228				LOGGER.debug(setupInfo);
				Vector introText = setupInfo.getIntroductoryTextLines();
				for (int lineCount = 0; lineCount < introText.size(); lineCount++) {
					LOGGER.debug((String) introText.get(lineCount));
				}
				avCbh.setAccessCode(accessCode);
				avCbh.setVerifyCode(verifyCode);
				avCbh.setSelectedOption(CallbackLogon.KEYPRESS_OK);

			} else if (callbacks[i] instanceof CallbackSelectDivision) {

				CallbackSelectDivision divCbh = (CallbackSelectDivision) callbacks[i];
				LOGGER.debug("Returned divisions: ");
				TreeMap divisionList = (TreeMap) divCbh.getDivisionList();
				for (Iterator it = divisionList.keySet().iterator(); it.hasNext();) {
					String divisionNumber = (String) it.next();
					VistaInstitution myDivision = (VistaInstitution) divisionList.get(divisionNumber);
					LOGGER.debug(
						"Division IEN: "
							+ myDivision.getIen()
							+ " Name: "
							+ myDivision.getName()
							+ " Number: "
							+ myDivision.getNumber());
				}
				divCbh.setSelectedDivisionIen(divisionIen);
				divCbh.setSelectedOption(CallbackSelectDivision.KEYPRESS_OK);

			} else if (callbacks[i] instanceof CallbackConfirm) {

				CallbackConfirm ecCbh = (CallbackConfirm) callbacks[i];
				StringBuffer sb = new StringBuffer();
				Vector vectorPostText = ecCbh.getDisplayMessages();
				for (int j = 0; j < vectorPostText.size(); j++) {
					sb.append((String) vectorPostText.get(j));
					sb.append("\n");
				}
				// since it's an error, let's display it for quicker troubleshooting 
				// (assuming error priority logging is enabled)
				LOGGER.error(sb.toString());
				ecCbh.setSelectedOption(CallbackConfirm.KEYPRESS_OK);

			} else if (callbacks[i] instanceof CallbackCommit) {
				
				// do nothing

			} else {

				String errMsg = "Unsupported callback: '" + callbacks[i].getClass() + "'";
				UnsupportedCallbackException e = new UnsupportedCallbackException(callbacks[i], errMsg);
				if (LOGGER.isEnabledFor(Priority.ERROR)) {
					LOGGER.error(errMsg, e);
				}
				throw e;
			}
		}
	}
}
