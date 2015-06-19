//
// @Authors: Ufsin A, JD Spivey, SMA Habiel
//

package us.pwc.vista.eclipse.core;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ServerData {
    private String name;
    private String address;
    private String port;
    private String ac; // access code
    private String vc; // verify code

	public ServerData() {
		this.name = "";
		this.address = "";
		this.port = "";
		this.ac = "";
		this.vc = "";
    }
    
    public ServerData(String name, String address, String port) {
		this.name = name;
		this.address = address;
		this.port = port;
		this.ac = "";
		this.vc = "";
	}
    
    public ServerData(String name, String address, String port, String ac, String vc) {
    	this.name = name;
		this.address = address;
		this.port = port;
		this.ac = ac;
		this.vc = vc;
    }
    	
    public ServerData(ServerData rhs) {
    	this(rhs.name, rhs.address, rhs.port, rhs.ac, rhs.vc);
    }
    
	public String getName() {
		return name;
	}

    public String getAddress() {
		return address;
	}

	public String getPort() {
		return port;
	}
	
	public String getAC() {
		return ac;
	}
	
	public String getVC() {
		return vc;
	}

    public void reset() {
		this.name = "";
		this.address = "";
		this.port = "";
		this.ac = "";
		this.vc = "";
   }
    
    @Override
    public String toString() {
    	return this.name + ";" + this.address + ";" + this.port + ";" + this.ac + ";" + this.vc;
    }
    
    public String toUIString() {
    	return this.name + " (" + this.address + ";" + this.port + ") " + "AC: " + this.ac;    	
    }
    
    public static LabelProvider getLabelProvider() {
    	LabelProvider result = new LabelProvider() {
    		@Override
    		public Image getImage(Object element) {
    			return null;
    		}
    		
    		@Override
    		public String getText(Object element) {
    			if(element instanceof ServerData) {
    				ServerData sd = (ServerData) element;
    				return sd.toUIString();
    			}
    			return null;
    		}
    	};
    	return result;
    }

    public static ServerData valueOf(String s) {
    	String[] fields = s.split(";");
    	
    	ServerData serverData = null;
    	
    	if (fields.length == 3)
    	{
    		serverData = new ServerData(fields[0], fields[1], fields[2]);
    	}
    	if (fields.length == 5)
    	{
    		serverData = new ServerData(fields[0], fields[1], fields[2], fields[3], fields[4]);
    	}
    	
    	// Just in case our internal user decided to use 4 args. Only 3 or 5 is okay.
    	assert (serverData != null);
    	
    	return serverData;
    }
    
    public boolean isComplete() {
    	// Are we on a Mac? Eclipse doesn't support AWT on a Mac
    	// since Java 1.7. Causes a dead lock.
    	// Ref: https://developer.apple.com/legacy/library/technotes/tn2147/_index.html
    	// Ref: https://community.oracle.com/thread/1394443
    	boolean onMac = System.getProperty("os.name").toUpperCase().contains("MAC");
    	
    	// Version 1: no ac/vc (but only on Non-Macs
    	if (this.ac.isEmpty() && this.vc.isEmpty() && !onMac) 
    	{
    		return ! (this.name.isEmpty() || this.address.isEmpty() || this.port.isEmpty());
    	}
    	// Version 2: has to have both ac and vc
    	else 
    	{
    		return ! (this.name.isEmpty() || this.address.isEmpty() || this.port.isEmpty() || this.ac.isEmpty() || this.vc.isEmpty());
    	}
    }
}
