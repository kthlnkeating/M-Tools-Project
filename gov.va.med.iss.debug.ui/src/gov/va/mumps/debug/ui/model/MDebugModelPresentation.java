package gov.va.mumps.debug.ui.model;

import gov.va.mumps.debug.core.model.MTagBreakpoint;
import gov.va.mumps.debug.core.model.MWatchpoint;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

public class MDebugModelPresentation extends LabelProvider implements
		IDebugModelPresentation {
	
	@Override
	public Image getImage(Object element) {
		//TODO: also implement this for MTagBreakpoint so it returns a nice fancy picture in the breakpoint view instead of the line breakpoint
		
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof MTagBreakpoint)
			return ((MTagBreakpoint)element).getBreakpointAsTag();
		
		if (element instanceof MWatchpoint)
			return ((MWatchpoint)element).getWatchpointVariable();
		
		return null;
	}

	@Override
	public String getEditorId(IEditorInput input, Object element) {
		if (element instanceof IFile || element instanceof ILineBreakpoint)
			return "gov.va.med.iss.meditor.editors.MEditor";
		return null;
	}

	@Override
	public IEditorInput getEditorInput(Object element) {
		if (element instanceof IFile) {
			return new FileEditorInput((IFile)element);
		}
		if (element instanceof ILineBreakpoint) {
			return new FileEditorInput((IFile)((ILineBreakpoint)element).getMarker().getResource());
		}
		return null;
	}

	@Override
	public void computeDetail(IValue value, IValueDetailListener listener) {
		String detail = "";
		try {
			detail = value.getValueString();
		} catch (DebugException e) {
		}
		listener.detailComputed(value, detail);
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
	}

}
