package gov.va.med.iss.connection.preferences;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ServerData {
    public String serverAddress;
    public String serverName;
    public String port;
    public String serverProject;

    public ServerData() {
		super();
		this.serverAddress = "";
		this.serverName = "";
		this.port = "";
		this.serverProject = "";
    }
    
    public ServerData(String serverAddress, String serverName, String port, String serverProject) {
		super();
		this.serverAddress = serverAddress;
		this.serverName = serverName;
		this.port = port;
		this.serverProject = serverProject;
	}
    
    public ServerData(ServerData rhs) {
    	this(rhs.serverAddress, rhs.serverName, rhs.port, rhs.serverProject);
    }
    
    public void reset() {
		this.serverAddress = "";
		this.serverName = "";
		this.port = "";
		this.serverProject = "";    	
    }
    
    @Override
    public String toString() {
    	return this.serverName + ";" + this.serverAddress + ";" + this.port + ";" + this.serverProject;
    }
    
    public String toUIString() {
    	return this.serverName + " (" + this.serverAddress + ";" + this.port + ")";    	
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
    
}
