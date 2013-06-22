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
import gov.va.med.iss.meditor.editors.MEditor;
import gov.va.med.iss.meditor.m.MCompletionProcessor;
import gov.va.med.iss.meditor.m.MDoubleClickStrategy;
import gov.va.med.iss.meditor.m.MPartitionScanner;
import gov.va.med.iss.meditor.m.MWordStrategy;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.ContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.IFormattingStrategy;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.RGB;

/**
 * This class defines the editor add-ons; content assist, content formatter,
 *  highlighting, auto-indent strategy, double click strategy. 
 * 
 */
public class MEditorSourceViewerConfiguration
	extends SourceViewerConfiguration {

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentAssistant(ISourceViewer)
	 */
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = new ContentAssistant();
	
		assistant.setContentAssistProcessor(
			new MCompletionProcessor(),
			IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setContentAssistProcessor(
			new MCompletionProcessor(),
			MPartitionScanner.M_CODE);
	
		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(500);
		assistant.setProposalPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
		assistant.setContextInformationPopupOrientation(
			IContentAssistant.CONTEXT_INFO_BELOW);
		//Set to Carolina blue
		assistant.setContextInformationPopupBackground(
			MEditorPlugin.getDefault().getColorProvider().getColor(
				new RGB(0, 191, 255)));
		
		return assistant;
	}

	/**
	 * Configure the double click strategy here.
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getDoubleClickStrategy(ISourceViewer, String)
	 */
	
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		return new MDoubleClickStrategy();
	}

	/**
	 * Configure a presentation reconciler for syntax highlighting  
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(ISourceViewer)
	 */
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

		PresentationReconciler reconciler= new PresentationReconciler();

		// rule for default text
		DefaultDamagerRepairer dr= new DefaultDamagerRepairer(MEditor.getMCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		// rule for multiline comments
		// We jsut need a scanner that does nothing but returns a token with the corrresponding text attributes
		RuleBasedScanner multiLineScanner = new RuleBasedScanner();
		multiLineScanner.setDefaultReturnToken(new Token(new TextAttribute(MEditorPlugin.getDefault().getColorProvider().getPreferenceColor(MEditorPlugin.P_MULTI_LINE_COMMENT_COLOR))));
		dr= new DefaultDamagerRepairer(multiLineScanner);
		reconciler.setDamager(dr, MPartitionScanner.M_MULTILINE_COMMENT);
		reconciler.setRepairer(dr, MPartitionScanner.M_MULTILINE_COMMENT);

		// rule for SQL comments for documentation
		dr= new DefaultDamagerRepairer(MEditor.getMCodeScanner());
		reconciler.setDamager(dr, MPartitionScanner.M_CODE);
		reconciler.setRepairer(dr, MPartitionScanner.M_CODE);
		
		return reconciler;
	}

	/**
	 * Configure the content formatter with two formatting strategies
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentFormatter(ISourceViewer)
	 */
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
	ContentFormatter formatter = new ContentFormatter();
	IFormattingStrategy keyword = new MWordStrategy();
	formatter.setFormattingStrategy(keyword, IDocument.DEFAULT_CONTENT_TYPE);
	
	return formatter;
	}

}
