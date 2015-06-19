package us.pwc.vista.eclipse.server;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class VistAServerPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "us.pwc.vista.eclipse.server"; //$NON-NLS-1$

	// The shared instance
	private static VistAServerPlugin plugin;
	
	/**
	 * The constructor
	 */
	public VistAServerPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static VistAServerPlugin getDefault() {
		return plugin;
	}

    /**
     * Convenience method to return the dialog settings for a particular 
     * <code>Dialog</code>.  Class name is used as section and a subsection 
     * can also be used.  If the dialog settings is not found it is created.
     *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#getDialogSettings
     * @param clazz dialog class
     * @param subsection
     * @return the dialog settings
     */
	public IDialogSettings getDialogSettings(Object object, String subsection) {
		IDialogSettings settings = this.getDialogSettings();
		String section = object.getClass().getName() + "_" + subsection;
		IDialogSettings result = settings.getSection(section);
		if (result == null) {
			result = settings.addNewSection(section);
		}
		return result;		
	}
}
