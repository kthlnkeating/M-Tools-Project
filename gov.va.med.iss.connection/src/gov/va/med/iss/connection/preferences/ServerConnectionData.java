package gov.va.med.iss.connection.preferences;

public class ServerConnectionData {
    public String serverAddress;
    public String serverName;
    public String port;
    public String serverProject;

    public ServerConnectionData() {
		super();
		this.serverAddress = "";
		this.serverName = "";
		this.port = "";
		this.serverProject = "";
    }
    
    public ServerConnectionData(String serverAddress, String serverName, String port, String serverProject) {
		super();
		this.serverAddress = serverAddress;
		this.serverName = serverName;
		this.port = port;
		this.serverProject = serverProject;
	}
    
    public ServerConnectionData(ServerConnectionData rhs) {
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
    
}
