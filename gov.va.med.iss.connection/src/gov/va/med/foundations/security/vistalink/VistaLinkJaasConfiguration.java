package gov.va.med.foundations.security.vistalink;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VistaLinkJaasConfiguration extends Configuration {
	
	private AppConfigurationEntry[] vistaConfig = new AppConfigurationEntry[1];

	public VistaLinkJaasConfiguration(AppConfigurationEntry vistaConfig) {
		this.vistaConfig[0] = vistaConfig;
	}

	public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
		// don't care about the name... always return same entry
		return vistaConfig;
	}

	public void refresh() {
		// nada
	}

}
/*
private AppConfigurationEntry getAppConfigurationEntry(String ip, String port) {

	Map optionMap = new HashMap();
	optionMap.put("gov.va.med.vistalink.security.ServerAddressKey", ip);
	optionMap.put("gov.va.med.vistalink.security.ServerPortKey", port);
	AppConfigurationEntry myEntry = new AppConfigurationEntry("gov.va.med.vistalink.security.VistaLoginModule",
			AppConfigurationEntry.LoginModuleControlFlag.REQUISITE, optionMap);
	return myEntry;
}
*/