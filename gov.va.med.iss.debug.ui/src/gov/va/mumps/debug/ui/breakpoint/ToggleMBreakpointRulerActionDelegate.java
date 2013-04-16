package gov.va.mumps.debug.ui.breakpoint;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.AbstractRulerActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;

public class ToggleMBreakpointRulerActionDelegate extends AbstractRulerActionDelegate  {

	@Override
	protected IAction createAction(ITextEditor editor, IVerticalRulerInfo rulerInfo) {
		return new ToggleMBreakpointAction(editor, rulerInfo);
	}

}
