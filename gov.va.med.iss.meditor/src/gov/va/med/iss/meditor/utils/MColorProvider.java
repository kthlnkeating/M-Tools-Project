package gov.va.med.iss.meditor.utils;
/*
 * "The Java Developer's Guide to Eclipse"
 *   by Shavor, D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */
import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.preferences.MEditorPrefs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Colors used in the SQL editor
 */
public class MColorProvider {

	/*
	public static final RGB BACKGROUND = new RGB(255, 255, 255);

	public static final RGB MULTI_LINE_COMMENT = new RGB(64, 128, 128);
	public static final RGB SINGLE_LINE_COMMENT = new RGB(64, 128, 128);
	
	public static final RGB DEFAULT = new RGB(0, 0, 0);
	public static final RGB KEYWORD = new RGB(127, 0, 85);
	public static final RGB TYPE = new RGB(64, 0, 200);
	public static final RGB STRING = new RGB(0, 0, 255);

	public static final RGB SQL_CODE_DEFAULT = new RGB(63, 95, 191);
	public static final RGB SQL_CODE_KEYWORD = new RGB(200, 100, 100);
	public static final RGB SQL_CODE_TAG = new RGB(127, 159, 191);
	public static final RGB FUNCS = new RGB(255, 0, 0);
	public static final RGB OPS = new RGB(155, 50, 50);
	public static final RGB TAGS = new RGB(0, 127, 0);
	public static final RGB VARS = new RGB(243, 150, 24);
	public static final RGB COMMAND = new RGB(255, 255, 0);
*/
	protected static Map fColorTable = new HashMap(20);

	/**
	 * Method disposes of the colors.
	 */
	public void dispose() {
		Iterator e= fColorTable.values().iterator();
		while (e.hasNext())
			((Color) e.next()).dispose();
	}
	/**
	 * A getter method that returns a color.
	 * @param rgb
	 * @return Color
	 */
	public Color getColor(RGB rgb) {
		Color color = (Color) fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
	
	public Color getPreferenceColor(String prefId) {		
		String prefValue = MEditorPrefs.getPrefs(prefId);
		RGB rgb;
		try {
			rgb = StringConverter.asRGB(prefValue);
		} catch (Exception e) {
			rgb = new RGB(0, 0, 0);
		}
		Color color= (Color) fColorTable.get(rgb);
		if (color == null) {
			color= new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
	
	public static void updateColorTable(String prefId, String prefValue) {
		RGB rgb;
		if (prefId.compareTo(MEditorPlugin.P_COMMAND_COLOR) == 0) {
//			int nval = 5;
		}
		try {
			rgb = StringConverter.asRGB(prefValue);
		} catch (Exception e) {
			rgb = new RGB(0, 0, 0);
		} 
		Color color = new Color(Display.getCurrent(), rgb);
		fColorTable.put(rgb, color);
	}
}
