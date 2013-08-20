package gov.va.med.iss.connection.preferences;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ServerData {
    private String name;
    private String address;
    private String port;

	public ServerData() {
		super();
		this.name = "";
		this.address = "";
		this.port = "";
    }
    
    public ServerData(String name, String address, String port) {
		super();
		this.name = name;
		this.address = address;
		this.port = port;
	}
    
    public ServerData(ServerData rhs) {
    	this(rhs.name, rhs.address, rhs.port);
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

    public void reset() {
		this.name = "";
		this.address = "";
		this.port = "";
   }
    
    @Override
    public String toString() {
    	return this.name + ";" + this.address + ";" + this.port;
    }
    
    public String toUIString() {
    	return this.name + " (" + this.address + ";" + this.port + ")";    	
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
    	ServerData serverData = new ServerData(fields[0], fields[1], fields[2]);
    	return serverData;
    }
    
    public boolean isComplete() {
    	return ! (this.name.isEmpty() || this.address.isEmpty() || this.port.isEmpty());
    }
}
