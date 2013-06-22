/*
 * Created on Aug 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor;

//import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.QualifiedName;

/**
 * @author VHAISFIVEYJ
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MEditorBasicData {

	public static final QualifiedName UNIT_TEST_PROPKEY = 
		new QualifiedName(MEditorPlugin.getDefault().getBundle().getSymbolicName()
				,"UnitTestName");
	public static final QualifiedName UPDATE_ROUTINE_FILE_PROPKEY =
		new QualifiedName(MEditorPlugin.getDefault().getBundle().getSymbolicName(),"UpdateRoutineFile");
	public static final QualifiedName READ_ONLY_PROPKEY = 
		new QualifiedName(MEditorPlugin.getDefault().getBundle().getSymbolicName()
				,"ReadOnlyState");
	public static final QualifiedName WRAP_LINES_PROPKEY = 
		new QualifiedName(MEditorPlugin.getDefault().getBundle().getSymbolicName()
				,"WrapLines");
}
