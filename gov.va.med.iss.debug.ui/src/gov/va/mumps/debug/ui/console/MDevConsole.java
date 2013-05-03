package gov.va.mumps.debug.ui.console;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.AbstractConsole;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.part.IPageBookViewPage;

public class MDevConsole extends AbstractConsole {
	
	private boolean acceptingUserInput;

	public MDevConsole(String name, String consoleType,
			ImageDescriptor imageDescriptor, boolean autoLifecycle) {
		super(name, consoleType, imageDescriptor, autoLifecycle);
	}

	@Override
	public IPageBookViewPage createPage(IConsoleView view) {
		return new MDevConsolePage(this);
	}

	public boolean isAcceptingUserInput() {
		return acceptingUserInput;
	}
	
	

}
