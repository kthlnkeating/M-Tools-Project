//package gov.va.mumps.debug.ui.console;
//
//import org.eclipse.swt.custom.StyledText;
//import org.eclipse.swt.events.KeyEvent;
//import org.eclipse.swt.events.KeyListener;
//
////not needed anymout really, key press is okay as it is
//
//public class KeyTypingListener implements KeyListener { //TODO: implement delete key
//
//	protected StyledText textWidget;
//	protected char[] keyBuffer = new char[2048]; 
//	protected int bufferPos = -1;
//	
//	public KeyTypingListener(StyledText textWidget) {
//		this.textWidget = textWidget;
//	}
//	
//	@Override
//	public void keyPressed(KeyEvent ke) {
//		if (ke.character != 0)//&& console.isAcceptingUserInput())
//			keyBuffer[++bufferPos] = ke.character;
//	}
//
//	@Override
//	public void keyReleased(KeyEvent ke) { //TODO: this could be improved slightly, right now all the keys in the buffer appear on screen at once. it makes more sense to put them onto the screen sooner
//		StringBuffer contents = new StringBuffer();
//		for (int i = 0; i <= bufferPos; i++)
//			contents.append(keyBuffer[i]);
//		textWidget.setText(textWidget.getText() + contents.toString());
//		keyBuffer = new char[2048];
//		bufferPos = -1;
//		
//		//update cursor to end pos
//		//textWidget.setCaretOffset(textWidget.getText().length());
//
//		//if (ke.keyCode == ) handle page up, page down.
//	}
//	
//	
//}
