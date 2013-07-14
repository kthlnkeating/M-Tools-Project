package gov.va.med.foundations.security.vistalink;

import java.awt.Frame;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * Implements the JAAS CallbackHandler interface. Use with the <code>VistaLoginModule</code> to invoke a Swing-based
 * interactive logon. Input values (access code, verify code, division selection, and other "user input") is collected
 * via a set of GUI dialogs when this callback handler is used.
 * 
 * <ol>
 * <li>Create an instance of CallbackHandlerSwing. No parameters are needed.
 * <li>Create the JAAS <code>LoginContext</code> instance, passing the instance of the callback handler as one of the
 * parameters.
 * <li>Invoke the JAAS login context's <code>login</code> method. The callback handler will invoke Swing dialogs to
 * collect user input wherever required for login.
 * </ol>
 * 
 * For example:
 * <pre>
 * String cfgName = "RpcSampleServer";
 * 
 * // create the callback handler
 * CallbackHandlerSwing cbhSwing = new CallbackHandlerSwing(myFrame);
 * 
 * // create the LoginContext
 * loginContext = new LoginContext(cfgName, cbhSwing);
 * 
 * // login to server
 * loginContext.login(); 
 * 
 * </pre>
 * @see VistaLoginModule
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public final class CallbackHandlerSwing implements CallbackHandler {

	private static final Logger LOGGER = Logger.getLogger(CallbackHandlerSwing.class);
	@SuppressWarnings("unused")
	private Frame windowParent;

	/**
	* Instantiates a JAAS callback handler for Swing applications. 
	* @param windowParent Allows dialogs launched during login to know about the parent window
	* that launched them. This may be useful in several areas, one of which is accessibility;
	* screen readers can verbally link a dialog to the parent window that launched the
	* dialog. While null could be passed in, it is recommended to pass in the parent frame.
	*/
	public CallbackHandlerSwing(Frame windowParent) {
		this.windowParent = windowParent;
	}

	/**
	 * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback)
	 */
	public void handle(Callback[] callbacks) throws UnsupportedCallbackException {

		for (int i = 0; i < callbacks.length; i++) {

			if (callbacks[i] instanceof CallbackChangeVc) {

				CallbackChangeVc cvcCbh = (CallbackChangeVc) callbacks[i];
//				DialogChangeVc.showVistaAVSwingChangeVC(windowParent, cvcCbh);
				DialogChangeVcForm.showVistaAVChangeVC(cvcCbh);

			} else if (callbacks[i] instanceof CallbackConfirm) {

				CallbackConfirm ccCbh = (CallbackConfirm) callbacks[i];
//				DialogConfirm.showDialogConfirm(windowParent, ccCbh);
				DialogConfirmForm.showDialogConfirm(ccCbh);

			} else if (callbacks[i] instanceof CallbackLogon) {

				CallbackLogon avCbh = (CallbackLogon) callbacks[i];
//				DialogLogon.showVistaAVSwingGetAV(windowParent, avCbh);
				DialogLogonForm.showVistaAVGetAV(avCbh);

			} else if (callbacks[i] instanceof CallbackSelectDivision) {

				CallbackSelectDivision divCbh = (CallbackSelectDivision) callbacks[i];
//				DialogSelectDivision.showVistaAVSwingSelectDivision(windowParent, divCbh);
				DialogDivisionForm.showVistaAVSwingSelectDivision(divCbh);
			
			} else if (callbacks[i] instanceof CallbackCommit) {
				
				// do nothing

			} else {
				String errMsg = "Unsupported callback: '" + callbacks[i].getClass() + "'";
				UnsupportedCallbackException e = new UnsupportedCallbackException(callbacks[i], errMsg);
				if (LOGGER.isEnabledFor(Level.ERROR)) {
					LOGGER.error(errMsg, e);
				}
				throw e;

			}
		}
	}
}
