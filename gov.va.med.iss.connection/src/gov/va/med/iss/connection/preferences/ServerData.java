package gov.va.med.iss.connection.preferences;

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
    
}
