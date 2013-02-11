package gov.va.med.iss.meditor;
/*
 * "The Java Developer's Guide to Eclipse"
 *   by Shavor, D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */

//import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.plugin.*;
//import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
//import org.eclipse.jface.preference.ColorFieldEditor;
//import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

import gov.va.med.iss.meditor.utils.MColorProvider;

import java.util.*;

/**
 * The main plugin class.
 */
public class MEditorPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static MEditorPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;

	MColorProvider colorProvider;
	
	private static String PluginId = "gov.va.med.iss.meditor";
	public static ImageDescriptor IMG_ERROR = AbstractUIPlugin.imageDescriptorFromPlugin(PluginId,"icons\\error.gif");
	public static ImageDescriptor IMG_SUCCESS = AbstractUIPlugin.imageDescriptorFromPlugin(PluginId,"icons\\yes1a.gif");
	public static ImageDescriptor IMG_POST_TEXT = AbstractUIPlugin.imageDescriptorFromPlugin(PluginId,"icons\\yes1a.gif");
	public static ImageDescriptor IMG_HELP = AbstractUIPlugin.imageDescriptorFromPlugin(PluginId,"icons\\helpbook07.gif");
	public static ImageDescriptor IMG_VA_LOGO = AbstractUIPlugin.imageDescriptorFromPlugin(PluginId,"icons\\VAlogo.gif");

	public static final String P_BACKGROUND_COLOR = "BackgroundColor";
	public static final String P_MULTI_LINE_COMMENT_COLOR = "MultiLineCommentColor";
	public static final String P_COMMENT_COLOR = "CommentColor";
	public static final String P_DEFAULT_COLOR = "DefaultColor";
	public static final String P_KEYWORD_COLOR = "KeywordColor";
	public static final String P_TYPE_COLOR = "TypeColor";
	public static final String P_STRING_COLOR = "StringColor";
	public static final String P_FUNCS_COLOR = "FuncsColor";
	public static final String P_OPS_COLOR = "OpsColor";
	public static final String P_TAGS_COLOR = "TagColor";
	public static final String P_VARS_COLOR = "VarsColor";
	public static final String P_COMMAND_COLOR = "CommandColor";
	public static final String P_CONDITIONS_COLOR = "ConditionsColor";
	public static final String P_DEFAULT_UPDATE = "defaultUpdatePreference";
	public static final String P_PROJECT_NAME = "ProjectName";
	public static final String P_SAVE_BY_SERVER = "SaveByServer";
	public static final String P_SAVE_BY_NAMESPACE = "SaveByNamespace";
	public static final String P_SAVE_DIR_EXAMPLE = "SaveByDirExample";
	public static final String P_WRAP_LINES = "WrapLines";
	public static final int P_SACC_MAX_LABEL_LENGTH = 8;

	public static String[] preferenceColors = {
			P_VARS_COLOR,
			P_COMMAND_COLOR,
			P_STRING_COLOR,
			P_COMMENT_COLOR,
			P_FUNCS_COLOR,
			P_CONDITIONS_COLOR,
			P_TAGS_COLOR
	};
	/**
	 * The constructor.
	 */
	public MEditorPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("gov.va.med.iss.meditor.MEditorPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		colorProvider = new MColorProvider();
	}
/*
//	/**
//	 * The constructor.
//	 * /
//	public MEditorPlugin(IPluginDescriptor descriptor) { //deprecated
	public MEditorPlugin() {
//		super(descriptor);
		super(bundle);
		plugin = this;
		try {
			resourceBundle =
				ResourceBundle.getBundle(
					"gov.va.med.iss.meditor.MEditorPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		colorProvider = new MColorProvider();

	}
*/
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
	}


	/**
	 * Returns the shared instance of the Plugin.
	 */
	public static MEditorPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle =
			MEditorPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	public void disposeColorProvider() {
		colorProvider.dispose();

	}

	/**
	 * Returns the colorProvider.
	 * @return MColorProvider
	 */
	public MColorProvider getColorProvider() {
		return colorProvider;
	}

}
