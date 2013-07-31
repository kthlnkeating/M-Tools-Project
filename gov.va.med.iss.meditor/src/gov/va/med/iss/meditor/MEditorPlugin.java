package gov.va.med.iss.meditor;

import gov.va.med.iss.meditor.utils.MColorProvider;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

/**
 * The main plugin class.
 */
public class MEditorPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static MEditorPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;

	MColorProvider colorProvider;
	
	public static String PLUGIN_ID = "gov.va.med.iss.meditor";

	public static final String P_AUTO_SAVE_TO_SERVER = "autoToSaveServer";
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

	public String getPluginId() {
		Bundle bundle = this.getBundle();
		if (bundle != null) {
			String result = bundle.getSymbolicName();
			if (result != null) return result;
		}
		return PLUGIN_ID;
	}

	public void logError(String message) {
		this.logError(message, null);
	}
	
	public void logError(String message, Throwable t) {
		this.log(Status.ERROR, message, t);
	}
	
	public void logWarning(String message) {
		this.logWarning(message, null);
	}
	
	public void logWarning(String message, Throwable t) {		
		this.log(Status.WARNING, message, t);
	}
	
	public void logInfo(String message) {
		this.logError(message, null);
	}
	
	public void logInfo(String message, Throwable t) {		
		this.log(Status.INFO, message, t);
	}
	
	public void log(int severity, String message, Throwable t) {
		String pluginId =  this.getPluginId();
		Status status = new Status(severity, pluginId, message, t);
		this.getLog().log(status);
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
