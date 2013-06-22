package gov.va.mumps.debug.ui.model;

import gov.va.med.iss.meditor.editors.MEditor;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;

public class MBreakpointAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof MEditor) {
			return new MLineBreakpointAdapter();
//			ITextEditor editorPart = (ITextEditor) adaptableObject;
//			IResource resource = (IResource) editorPart.getEditorInput().getAdapter(IResource.class);
//			if (resource != null) {
//				String extension = resource.getFileExtension();
//				System.out.println("extension: "+ extension);
//				if (extension != null && extension.equals("pda")) {
//					return new PDALineBreakpointAdapter();
//				}
//			}			
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[]{IToggleBreakpointsTarget.class};
	}

}
