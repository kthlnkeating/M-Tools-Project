package gov.va.mumps.debug.ui.model;

import gov.va.med.iss.meditor.editors.MEditor;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;

public class MBreakpointAdapterFactory implements IAdapterFactory {

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof MEditor) {
			return new MLineBreakpointAdapter();
		}
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return new Class[]{IToggleBreakpointsTarget.class};
	}

}
