package gov.va.med.iss.meditor;
/*
 * "The Java Developer's Guide to Eclipse"
 *   by Shavor, D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
//import org.eclipse.jface.text.rules.DefaultPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import gov.va.med.iss.meditor.m.MPartitionScanner;


/**
 * This DocumentProvider class, which implements IDocumentProvider, creates and
 * manages the document content. It notifies the editors about changes applied 
 * to the document model. The document provider also creates an annotation model
 * on a document. Annotations on a document are seen in the vertical bar to the
 * left of the text window. Book marks and break points are examples of 
 * annotations. Document providers manage annotations. The provider also 
 * delivers the document input element's IAnnotationModel, the model represents
 * resource markers. The annotation model is used to control the editor's 
 * vertical ruler. A document is an abstraction, that is to say, it is not 
 * limited to representing text files. However, FileDocumentProvider extends 
 * DocumentProvider, and is specialized in that it connects to resource based 
 * (IFile) documents
 */
public class MEditorDocumentProvider extends FileDocumentProvider {
	private static MPartitionScanner scanner= null;
	private final static String[] TYPES= new String[] { MPartitionScanner.M_CODE,MPartitionScanner.M_MULTILINE_COMMENT };
	private static IDocument document = null;

	/**
	 * Constructor for SQLEditorDocumentProvider.
	 */
	public MEditorDocumentProvider() {
		super();
	}

	/**
	 * Creates the document for the given input element and then connects 
	 * the SQL document partitioner to the document.

	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#createDocument(Object)
	 */
	protected IDocument createDocument(Object element) throws CoreException {
		document= super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner = createSQLPartitioner();
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;		
	}

	/**
	 * Return a partitioner for SQL files.
	 */
//	 private DefaultPartitioner createSQLPartitioner() {
//		return new DefaultPartitioner(getSQLPartitionScanner(), TYPES);
		 private FastPartitioner createSQLPartitioner() {
				return new FastPartitioner(getMPartitionScanner(), TYPES);
	}
	
	/**
	 * Return a scanner for SQL partitions.
	 */
	 private MPartitionScanner getMPartitionScanner() {
		if (scanner == null)
			scanner= new MPartitionScanner();
		return scanner;
	}
	
	public static IDocument getDocument() {
		return document;
	}

}
