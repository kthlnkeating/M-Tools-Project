package us.pwc.eclipse.vista.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class AssumedVariablesTags extends AbstractHandler {
	
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "AA", "AV Tags");
		return null;
	}
}
