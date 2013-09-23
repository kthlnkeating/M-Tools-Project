//---------------------------------------------------------------------------
// Copyright 2013 PwC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//---------------------------------------------------------------------------

package gov.va.mumps.debug.core;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import gov.va.mumps.debug.core.model.MDebugPreference;

public class MDebugSettings {
	private static final String DEBUG_PREFERENCE = "debugPreference";
	private static final String VISTA_NAMESPACE = "vistaNamespace";

	private static final String TELNET_HOST = "telnetHost";
	private static final String TELNET_PORT = "telnetPort";
	private static final String TELNET_TIMEOUT = "telnetTimeout";

	private static final String SSH_HOST = "sshHost";
	private static final String SSH_PORT = "sshPort";
	private static final String SSH_TIMEOUT = "sshTimeout";
	private static final String SSH_USER = "sshUser";
	private static final String SSH_PASSWORD = "sshPassword";
	private static final String SSH_KEEP_ALIVE = "sshKeepAlive";

	public static MDebugPreference getDebugPreference() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		String storedValue = preferences.get(DEBUG_PREFERENCE, null);
		if (storedValue == null) {
			return MDebugPreference.GENERIC;
		} else {			
			return MDebugPreference.valueOf(storedValue);
		}
	}
	
	public static void setDebugPreference(MDebugPreference preference) {
		IEclipsePreferences preferences =  InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		String valueToBeStored = preference.toString();
		preferences.put(DEBUG_PREFERENCE, valueToBeStored);
		try {
			preferences.flush();
		}  catch (Throwable t) {			
		}
	}
	
	public static String getNamespace() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		String namespace = preferences.get(VISTA_NAMESPACE, null);
		if (namespace == null) {
			return "VISTA";
		} else {			
			return namespace;
		}
	}
	
	public static void setNamespace(String namespace) {
		IEclipsePreferences preferences =  InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		preferences.put(VISTA_NAMESPACE, namespace);
		try {
			preferences.flush();
		}  catch (Throwable t) {			
		}
	}	

	public static String getTelnetHost() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		String host = preferences.get(TELNET_HOST, null);
		if (host == null) {
			return "localhost";
		} else {			
			return host;
		}
	}
	
	public static void setTelnetHost(String host) {
		IEclipsePreferences preferences =  InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		preferences.put(TELNET_HOST, host);
		try {
			preferences.flush();
		}  catch (Throwable t) {			
		}
	}	

	public static String getTelnetPort() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		String port = preferences.get(TELNET_PORT, null);
		if (port == null) {
			return "23";
		} else {			
			return port;
		}
	}
	
	public static void setTelnetPort(String port) {
		IEclipsePreferences preferences =  InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		preferences.put(TELNET_PORT, port);
		try {
			preferences.flush();
		}  catch (Throwable t) {			
		}
	}	

	public static String getTelnetTimeout() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		String timeout = preferences.get(TELNET_TIMEOUT, null);
		if (timeout == null) {
			return "10";
		} else {			
			return timeout;
		}
	}
	
	public static void setTelnetTimeout(String timeout) {
		IEclipsePreferences preferences =  InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		preferences.put(TELNET_TIMEOUT, timeout);
		try {
			preferences.flush();
		}  catch (Throwable t) {			
		}
	}	

	
	public static String getSSHHost() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		String host = preferences.get(SSH_HOST, null);
		if (host == null) {
			return "127.0.0.1";
		} else {			
			return host;
		}
	}
	
	public static void setSSHHost(String host) {
		IEclipsePreferences preferences =  InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		preferences.put(SSH_HOST, host);
		try {
			preferences.flush();
		}  catch (Throwable t) {			
		}
	}	

	public static String getSSHPort() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		String port = preferences.get(SSH_PORT, null);
		if (port == null) {
			return "3022";
		} else {			
			return port;
		}
	}
	
	public static void setSSHPort(String port) {
		IEclipsePreferences preferences =  InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		preferences.put(SSH_PORT, port);
		try {
			preferences.flush();
		}  catch (Throwable t) {			
		}
	}	

	public static String getSSHTimeout() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		String timeout = preferences.get(SSH_TIMEOUT, null);
		if (timeout == null) {
			return "0";
		} else {			
			return timeout;
		}
	}
	
	public static void setSSHTimeout(String timeout) {
		IEclipsePreferences preferences =  InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		preferences.put(SSH_TIMEOUT, timeout);
		try {
			preferences.flush();
		}  catch (Throwable t) {			
		}
	}	
	
	public static String getSSHUser() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		String user = preferences.get(SSH_USER, null);
		if (user == null) {
			return "softhat";
		} else {			
			return user;
		}
	}
	
	public static void setSSHUser(String user) {
		IEclipsePreferences preferences =  InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		preferences.put(SSH_USER, user);
		try {
			preferences.flush();
		}  catch (Throwable t) {			
		}
	}	

	public static String getSSHPassword() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		String password = preferences.get(SSH_PASSWORD, null);
		if (password == null) {
			return "softhat";
		} else {			
			return password;
		}
	}
	
	public static void setSSHPassword(String password) {
		IEclipsePreferences preferences =  InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		preferences.put(SSH_PASSWORD, password);
		try {
			preferences.flush();
		}  catch (Throwable t) {			
		}
	}	

	public static String getSSHKeepAlive() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		String keepAlive = preferences.get(SSH_KEEP_ALIVE, null);
		if (keepAlive == null) {
			return "300";
		} else {			
			return keepAlive;
		}
	}
	
	public static void setSSHKeepAlive(String keepAlive) {
		IEclipsePreferences preferences =  InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		preferences.put(SSH_KEEP_ALIVE, keepAlive);
		try {
			preferences.flush();
		}  catch (Throwable t) {			
		}
	}	
}
