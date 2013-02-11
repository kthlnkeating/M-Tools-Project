package gov.va.med.iss.mdebugger;

import org.eclipse.ui.plugin.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MDebuggerPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static MDebuggerPlugin plugin;
	private static String PluginId = "gov.va.med.iss.mdebugger";
	public static ImageDescriptor IMG_RUN_COMMAND = AbstractUIPlugin.imageDescriptorFromPlugin(PluginId,"icons\\resume_co.gif");
	public static ImageDescriptor IMG_STEP_INTO = AbstractUIPlugin.imageDescriptorFromPlugin(PluginId,"icons\\stepinto_co.gif");
	public static ImageDescriptor IMG_STEP_OVER1 = AbstractUIPlugin.imageDescriptorFromPlugin(PluginId,"icons\\stepover_co.gif");
	public static ImageDescriptor IMG_STEP_OUT = AbstractUIPlugin.imageDescriptorFromPlugin(PluginId,"icons\\stepreturn_co.gif");
	public static ImageDescriptor IMG_STEP_LINE = AbstractUIPlugin.imageDescriptorFromPlugin(PluginId,"icons\\stepline_co.gif");
	public static ImageDescriptor IMG_TERMINATE = AbstractUIPlugin.imageDescriptorFromPlugin(PluginId,"icons\\terminate_co.gif");
	public static ImageDescriptor IMG_STEP_OVER2 = AbstractUIPlugin.imageDescriptorFromPlugin(PluginId,"icons\\stepover_co.gif");
	public static ImageDescriptor IMG_STEP_OVER3 = AbstractUIPlugin.imageDescriptorFromPlugin(PluginId,"icons\\stepover_co.gif");
	
	/**
	 * The constructor.
	 */
	public MDebuggerPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static MDebuggerPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("gov.va.med.iss.mdebugger", path);
	}
}
