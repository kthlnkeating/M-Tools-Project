//package gov.va.mumps.debug.ui.console;
//
//import org.eclipse.swt.custom.StyledText;
//import org.eclipse.swt.events.KeyEvent;
//import org.eclipse.swt.events.KeyListener;
//
//public class ConsoleKeyTypingListener implements KeyListener {
//
//	private 
//	private MDevConsole console;
//	
//	public ConsoleKeyTypingListener(StyledText textWidget, MDevConsole console) {
//		super(textWidget);
//
//		this.console = console; 
//	}
//	
//	@Override
//	public void keyPressed(KeyEvent ke) {
//		if (console.isAcceptingUserInput() && bufferPos+1 < console.getMaxCharInput())
//			super.keyPressed(ke);
//	}
//
//	@Override
//	public void keyReleased(KeyEvent arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//}
