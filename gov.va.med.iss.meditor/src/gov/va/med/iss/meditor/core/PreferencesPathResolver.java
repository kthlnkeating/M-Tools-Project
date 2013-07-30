package gov.va.med.iss.meditor.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class PreferencesPathResolver implements RoutinePathResolver {
	private String serverName;
	private int namespace;
	
	public PreferencesPathResolver(String serverName, int namespace) {
		super();
		this.serverName = serverName;
		this.namespace = namespace;
	}

	@Override
	public IPath getRelativePath(String routineName) {
		IPath path = null;
		if (this.serverName != null) {
			path = new Path(this.serverName);
		}
		if (this.namespace != 0) {
			String folderName = routineName.substring(0, this.namespace);
			if (path == null) {
				path = new Path(folderName);
			} else {
				path = path.append(folderName);			
			}
		}
		return path; 
	}
}
