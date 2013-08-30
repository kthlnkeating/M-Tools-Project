/*
 * Created on Aug 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.preferences;


import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.editors.MEditor;
import gov.va.med.iss.meditor.m.MCodeScanner;
import gov.va.med.iss.meditor.utils.MColorProvider;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */


public class MEditorPreferencesPage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	public static final String P_PATH = "pathPreference";
	public static final String P_CHOICE = "choicePreference";
	public static final String P_STRING = "stringPreference";
	
	public MEditorPreferencesPage() {
		super(GRID);
		setPreferenceStore(MEditorPlugin.getDefault().getPreferenceStore());
		setDescription("MEditor Preferences");
		initializeDefaults();
		MEditorPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(preferenceChangeListener);
	}

	/**
	 * Sets the initializes all the preferences to a default value.
	 * That is, if these preferences are not in eclipses persistence store, this
	 * method shall create them and set them to their default value.
	 */
	public void initializeDefaults() {
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(MEditorPlugin.P_AUTO_SAVE_TO_SERVER, true);
		store.setDefault(MEditorPlugin.P_WRAP_LINES, false);

		store.setDefault(MEditorPlugin.P_MULTI_LINE_COMMENT_COLOR, "64,128,128");
		store.setDefault(MEditorPlugin.P_COMMENT_COLOR, "128,128,0");
		
		store.setDefault(MEditorPlugin.P_DEFAULT_COLOR, "0,0,0");
		store.setDefault(MEditorPlugin.P_KEYWORD_COLOR, "127,0,85"); ///  <====
		store.setDefault(MEditorPlugin.P_TYPE_COLOR, "64,0,200");  /// <====
		store.setDefault(MEditorPlugin.P_STRING_COLOR, "0,0,255");

		store.setDefault(MEditorPlugin.P_FUNCS_COLOR, "255,0,0");
		store.setDefault(MEditorPlugin.P_OPS_COLOR, "155,50,50");
		store.setDefault(MEditorPlugin.P_TAGS_COLOR, "0,127,0");
		store.setDefault(MEditorPlugin.P_VARS_COLOR, "170,0,170");
		store.setDefault(MEditorPlugin.P_COMMAND_COLOR, "155,50,50");  // 97,162,97
		store.setDefault(MEditorPlugin.P_CONDITIONS_COLOR,"0,0,0");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		
		Composite parent = getFieldEditorParent();
		
		addField(new BooleanFieldEditor(MEditorPlugin.P_AUTO_SAVE_TO_SERVER, "Automatically save files onto server", parent));
		addField(new BooleanFieldEditor(MEditorPlugin.P_WRAP_LINES,"&Wrap lines",parent));
		addField(new ColorFieldEditor(MEditorPlugin.P_VARS_COLOR,"Variables",parent));
		addField(new ColorFieldEditor(MEditorPlugin.P_COMMAND_COLOR,"Commands",parent));
		addField(new ColorFieldEditor(MEditorPlugin.P_STRING_COLOR,"Strings",parent));
		addField(new ColorFieldEditor(MEditorPlugin.P_COMMENT_COLOR,"Comments",parent));
		addField(new ColorFieldEditor(MEditorPlugin.P_FUNCS_COLOR,"Functions",parent));
		addField(new ColorFieldEditor(MEditorPlugin.P_CONDITIONS_COLOR,"Conditions",parent));
		addField(new ColorFieldEditor(MEditorPlugin.P_TAGS_COLOR,"Tags && Routines",parent));
		
		adjustGridLayout();
	}

	public void init(IWorkbench workbench) {
	}
	
	private final IPropertyChangeListener preferenceChangeListener = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			boolean seenFlag = false;
			IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			for (int i=0; i<MEditorPlugin.preferenceColors.length; i++) {
				if (event.getProperty().equals(MEditorPlugin.preferenceColors[i])) {
					seenFlag = true;
					MColorProvider.updateColorTable(MEditorPlugin.preferenceColors[i],MEditorPrefs.getPrefs(MEditorPlugin.preferenceColors[i]));
				}
			}
			if (! seenFlag) {
				if (event.getProperty().equals(MEditorPlugin.P_WRAP_LINES)) {
					seenFlag = true;
					boolean wordWrap;
					if (MEditorPrefs.getPrefs(MEditorPlugin.P_WRAP_LINES).equals("true")) {
						wordWrap = true;
					}
					else wordWrap = false;
					MEditor.setWordWrapValue(wordWrap);
					((MEditor) part).setWordWrap();
				}
			}
			if (seenFlag) {
				MCodeScanner.setTokens();
				if (part instanceof MEditor) {
					((MEditor) part).updateSourceViewerConfiguration();
					((MEditor) part).sourceViewer.invalidateTextPresentation();
				}
			}
		}
	};
}