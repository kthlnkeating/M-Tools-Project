package gov.va.med.iss.connection;


import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class VLConnectionPlugin extends AbstractUIPlugin {
	// The plug-in ID
	public static String PLUGIN_ID = "gov.va.med.iss.connection";
	
	// The shared instance.
	private static VLConnectionPlugin plugin;
	
	private ConnectionManager connectionManager;
	
	public static ImageDescriptor IMG_ERROR = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID,"icons/error.gif");
	public static ImageDescriptor IMG_SUCCESS = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID,"icons/yes1a.gif");
	public static ImageDescriptor IMG_POST_TEXT = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID,"icons/yes1a.gif");
	public static ImageDescriptor IMG_HELP = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID,"icons/helpbook07.gif");
	public static ImageDescriptor IMG_VA_LOGO = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID,"icons/VAlogo.gif");
	
	/**
	 * The constructor.
	 */
	public VLConnectionPlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		this.connectionManager = new ConnectionManager();
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		this.connectionManager.removeAllConnections();
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static VLConnectionPlugin getDefault() {
		return plugin;
	}
	
	public static ConnectionManager getConnectionManager() {
		return getDefault().connectionManager;
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
