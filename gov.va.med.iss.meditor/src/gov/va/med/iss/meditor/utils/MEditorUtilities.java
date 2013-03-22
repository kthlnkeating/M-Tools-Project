/*
 * Created on Aug 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.utils;

import java.io.FileReader;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbench;

import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.preferences.MEditorPrefs;

/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MEditorUtilities {

	private static String strval = "";

	public static String fileToString(String fileName) {
		try {
			FileReader fr = new FileReader(fileName);
			char[] charbuf = new char[128000];
			int val = fr.read(charbuf,0,128000);
			String strvala = new String(charbuf,0,val);
			strval = strvala;
		} catch (Exception e) {
			strval = "";
		}
		return strval;
	}

	public static IResource getProject(String name) throws Exception {
		if (name.compareTo("") == 0) {
			VistaConnection.getDefaultPrefs();
			name = VistaConnection.getCurrentProject();
			if (name == null || name.equals(""))
				name = MEditorPrefs.getPrefs(MEditorPlugin.P_PROJECT_NAME);
		}
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject(name);
		try {
			project.create(null);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		try {
			project.open(null);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		IResource resource = root.findMember(new Path(name));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			resource = root.findMember(new Path(name));
			if (!resource.exists() || !(resource instanceof IContainer)) {
				throw new Exception("Project \"" + name + "\" does not exist.");
			}
		}
		return resource;
	}
	
	public static IWorkbenchPage getIWorkbenchPage() {
		IWorkbenchWindow win = getIWorkbenchWindow();
		return win.getActivePage();
	}
		
	public static IWorkbenchWindow getIWorkbenchWindow() {
		IWorkbench wb = PlatformUI.getWorkbench();
		return wb.getActiveWorkbenchWindow();
	}
}
