/*
 * Created on Aug 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.editors;

/*
 * 051006 corrected a problem when the current line had text at the beginning
 *        but did not contain a tab or opening parenthesis, resulting in a
 *        invalid index error, which on the screen frequently had characters
 *        at the beginning of the line entering in backwards order as they were
 *        entered.
 */

//import gov.va.med.iss.meditor.utils.MEditorUtilities;
import gov.va.med.iss.meditor.utils.RoutineLoad;
import gov.va.med.iss.connection.actions.VistaConnection;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

//import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * A content outline page which displays the content of the
 * connected editor with names of TAGS and at intervals of
 * 10 lines between TAGS segments.
 */
public class MContentOutlinePage extends ContentOutlinePage implements IDocumentListener {

	/**
	 * A segment element.
	 */
	protected static class Segment implements IAdaptable {
		private IFile file;
		public String name;
		public Position position;

		public Segment(IFile file, String name, Position position) {
			this.name= name;
			this.position= position;
			this.file = file;
		}

		public String toString() {
			return name;
		}
		
		@Override
		public Object getAdapter(Class adapter) {
			if (adapter == IFile.class) {
				return this.file;
			} else {
				return null;
			}
		}
	}

	/**
	 * Divides the editor's document into segments based on tags and intervals of 10 lines 
	 * between them and provides elements for them.
	 */
	protected class ContentProvider implements ITreeContentProvider {

		protected final static String SEGMENTS= "__M_segments"; //$NON-NLS-1$
		protected IPositionUpdater fPositionUpdater= new DefaultPositionUpdater(SEGMENTS);
		protected List fContent= new ArrayList(100);
		private IFile file;
		
		public ContentProvider(IFile file) {
			this.file = file;
		}
		
		protected void parse(IDocument document) {
			int lines= document.getNumberOfLines();
			int lineNum = 0;
			String tagName = "";
			
			for (int line= 0; line < lines; line++) { // += increment) {
				int length = 1;

				try {

					int offset= document.getLineOffset(line);
					int end= document.getLineOffset(line + length);
					String str = document.get(offset, end-offset-1);
					String str1 = "";
					int ichar = 0;
					if (str.length() > 0) {
						// JLI 100824 added check for ' ', since at least some code in ClearCase uses space instead of tab 
						while ((ichar < str.length()) && (! ((str.charAt(ichar) == '\t') || (str.charAt(ichar) == ' ') || (str.charAt(ichar) == '(')))) {
							str1 = str1 + str.charAt(ichar);
							ichar = ichar + 1;
						}
					}
					if (! (str1.compareTo("") == 0)) {
						Position p= new Position(offset, end - offset);
						document.addPosition(SEGMENTS, p);
						fContent.add(new Segment(file, str1, p)); //MessageFormat.format(MEditorMessages.getString("OutlinePage.segment.title_pattern"), new Object[] { new Integer(offset) }), p)); //$NON-NLS-1$
						lineNum = 0;
						tagName = str1;
					} else {
						lineNum = lineNum + 1;
						if ((lineNum % 10) == 0) {
							Position p= new Position(offset, end-offset);
							document.addPosition(SEGMENTS, p);
							fContent.add(new Segment(file, "      "+tagName+"+"+lineNum, p)); //MessageFormat.format(MEditorMessages.getString("OutlinePage.segment.title_pattern"), new Object[] { new Integer(offset) }), p)); //$NON-NLS-1$
						}
					}

				} catch (BadPositionCategoryException x) {
				} catch (BadLocationException x) {
				}
			}
			try {
				int val1 = document.getLength();
				Position p = new Position(val1, 0);
				document.addPosition(SEGMENTS, p);
				fContent.add(new Segment(file, "<<END>>", p));
			} catch (Exception e) {
			}
		}

		/*
		 * @see IContentProvider#inputChanged(Viewer, Object, Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (oldInput != null) {
				IDocument document= fDocumentProvider.getDocument(oldInput);
				if (document != null) {
					try {
						document.removePositionCategory(SEGMENTS);
					} catch (BadPositionCategoryException x) {
					}
					document.removePositionUpdater(fPositionUpdater);
				}
			}

			fContent.clear();

			if (newInput != null) {
				IDocument document= fDocumentProvider.getDocument(newInput);
				if (document != null) {
					document.addPositionCategory(SEGMENTS);
					document.addPositionUpdater(fPositionUpdater);

					parse(document);
				}
			}
		}

		/*
		 * @see IContentProvider#dispose
		 */
		public void dispose() {
			if (fContent != null) {
				fContent.clear();
				fContent= null;
			}
		}

		/*
		 * @see IContentProvider#isDeleted(Object)
		 */
		public boolean isDeleted(Object element) {
			return false;
		}

		/*
		 * @see IStructuredContentProvider#getElements(Object)
		 */
		public Object[] getElements(Object element) {
			return fContent.toArray();
		}

		/*
		 * @see ITreeContentProvider#hasChildren(Object)
		 */
		public boolean hasChildren(Object element) {
			return element == fInput;
		}

		/*
		 * @see ITreeContentProvider#getParent(Object)
		 */
		public Object getParent(Object element) {
			if (element instanceof Segment)
				return fInput;
			return null;
		}

		/*
		 * @see ITreeContentProvider#getChildren(Object)
		 */
		public Object[] getChildren(Object element) {
			if (element == fInput)
				return fContent.toArray();
			return new Object[0];
		}
	}

	protected Object fInput;
	protected IDocumentProvider fDocumentProvider;
	protected ITextEditor fTextEditor;
	protected MEditor mEditor;

	/**
	 * Creates a content outline page using the given provider and the given editor.
	 * 
	 * @param provider the document provider
	 * @param editor the editor
	 */
	public MContentOutlinePage(IDocumentProvider provider, ITextEditor editor, MEditor mEditor) {
		fDocumentProvider= provider;
		fTextEditor= editor;
		this.mEditor = mEditor;
		IDocument fDocument = provider.getDocument(editor.getEditorInput());
		fDocument.addDocumentListener(this);
	}
	
	private void createContextMenuFor(StructuredViewer viewer) {
		MenuManager contextMenu = new MenuManager("#PopUpMenu","com.com.com");
		contextMenu.add(new Separator("additions"));
		contextMenu.setRemoveAllWhenShown(true);
		Menu menu = contextMenu.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		IWorkbenchPart part = this.mEditor.getEditorSite().getPart();
		this.mEditor.getEditorSite().registerContextMenu("com.com.com", contextMenu, this.mEditor.getEditorSite().getSelectionProvider());

	}

	/* (non-Javadoc)
	 * Method declared on ContentOutlinePage
	 */
	public void createControl(Composite parent) {

		super.createControl(parent);

		TreeViewer viewer= getTreeViewer();
		IEditorInput input = this.mEditor.getEditorInput();
		IFile file = (IFile) input.getAdapter(IFile.class);
		viewer.setContentProvider(new ContentProvider(file));
		viewer.setLabelProvider(new LabelProvider());
		viewer.addSelectionChangedListener(this);
		createContextMenuFor(viewer);
		
		if (fInput != null)
			viewer.setInput(fInput);
	}
	
	/* (non-Javadoc)
	 * Method declared on ContentOutlinePage
	 */
	public void selectionChanged(SelectionChangedEvent event) {
				
		super.selectionChanged(event);

		ISelection selection= event.getSelection();
		if (selection.isEmpty())
			fTextEditor.resetHighlightRange();
		else {
			Segment segment= (Segment) ((IStructuredSelection) selection).getFirstElement();
			int start= segment.position.getOffset();
			int length= segment.position.getLength();
			try {
				fTextEditor.setHighlightRange(start, length, true);
			} catch (IllegalArgumentException x) {
				fTextEditor.resetHighlightRange();
			}
		}
	}
	
	/**
	 * Sets the input of the outline page
	 * 
	 * @param input the input of this outline page
	 */
	public void setInput(Object input) {
		fInput= input;
		update();
	}
	
	/**
	 * Updates the outline page.
	 */
	public void update() {
		TreeViewer viewer= getTreeViewer();

		if (viewer != null) {
			Control control= viewer.getControl();
			if (control != null && !control.isDisposed()) {
				control.setRedraw(false);
				viewer.setInput(fInput);
				viewer.expandAll();
				control.setRedraw(true);
			}
		}
	}
	
	private int fNumberOfLines = 0;
	public void documentChanged(DocumentEvent event) {
		IDocument doc = fDocumentProvider.getDocument(fTextEditor.getEditorInput());
		int nLines = doc.getNumberOfLines();
		if (fNumberOfLines == 0) {
			fNumberOfLines = nLines;
            //  JLI start insert 060203
			//  code added to correct problem with routine on workstation disk
			//  edited on a prior date when compared to routine on server gave
			//  indication it was not the same -- actually didn't have last
			//  saved file with current date, so copy working copy to that file
			//
			//  This only works if Outline is showing and active
			try {
//				if (VistaConnection.getPrimaryProject().compareTo("") == 0) {
					String routineName = fTextEditor.getTitle();
					routineName = routineName.substring(0,routineName.length()-2);
					String location = RoutineLoad.getFullFileLocation(routineName);
					String dstFilename = RoutineLoad.getLastLoadFileName(routineName, location);
					File f = new File(dstFilename);
					if (! f.exists()) {
						try {
							String srcFilename = location + "/" + routineName + ".m";
							// Create channel on the source
							FileChannel srcChannel = new FileInputStream(srcFilename).getChannel();
						
							// Create channel on the destination
							FileChannel dstChannel = new FileOutputStream(dstFilename).getChannel();
			    
							// Copy file contents from source to destination
							dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
			    
							// Close the channels
							srcChannel.close();
							dstChannel.close();
						} catch (IOException e) {
						}
					}
//				}
			} catch (Exception e) {
				
			}  
			// JLI end of insertion 060203
		}
		if (! (nLines == fNumberOfLines)) {
			TreeViewer viewer= getTreeViewer();
			IContentProvider cp = viewer.getContentProvider();
			cp.inputChanged(viewer, null, fTextEditor.getEditorInput());
			fTextEditor.getEditorInput();
			update();
		}
	}
	
	public void documentAboutToBeChanged(DocumentEvent event) {
		
	}
}
