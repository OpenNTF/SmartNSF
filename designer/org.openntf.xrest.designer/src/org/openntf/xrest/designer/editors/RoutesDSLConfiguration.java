package org.openntf.xrest.designer.editors;

import org.eclipse.jdt.internal.ui.text.JavaPartitionScanner;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProcessor;
import org.eclipse.jdt.internal.ui.text.javadoc.JavadocCompletionProcessor;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;
import org.openntf.xrest.designer.codeassist.GroovyContentAssistProcessor;

public class RoutesDSLConfiguration extends SourceViewerConfiguration {
	private DSLScanner scanner;
	private final ColorManager colorManager;
	private final ITextEditor editor;

	public RoutesDSLConfiguration(ColorManager colorManager, ITextEditor editor) {
		// super()
		// super(colorManager,
		// XRestUIActivator.getDefault().getPreferenceStore(), editor,
		// IJavaPartitions.JAVA_PARTITIONING);
		this.colorManager = colorManager;
		this.editor = editor;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		System.out.println("TYËE");
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, IJavaPartitions.JAVA_DOC, IJavaPartitions.JAVA_MULTI_LINE_COMMENT, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT, IJavaPartitions.JAVA_STRING,
				IJavaPartitions.JAVA_CHARACTER };
	}

	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new DefaultAnnotationHover();
	}

	protected DSLScanner getDSLScanner() {
		if (scanner == null) {
			scanner = new DSLScanner(colorManager);
			scanner.setDefaultReturnToken(new Token(new TextAttribute(colorManager.getColor(IDSLColorConstants.DEFAULT))));
		}
		return scanner;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getDSLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		return reconciler;
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		System.out.println("CONTENASSIST....");
		
		//ContentAssistant assistant = (ContentAssistant) super.getContentAssistant(sourceViewer);
		//ContentAssistProcessor stringProcessor = new JavaCompletionProcessor(getEditor(), assistant, GroovyPartitionScanner.GROOVY_MULTILINE_STRINGS);
		//assistant.setContentAssistProcessor(stringProcessor, GroovyPartitionScanner.GROOVY_MULTILINE_STRINGS);
		// remove Java content assist processor category
		// do a list copy so as not to disturb globally shared list
		//IContentAssistProcessor processor = assistant.getContentAssistProcessor(IDocument.DEFAULT_CONTENT_TYPE);
		//System.out.println(processor.getClass());
		/*
		 * List<CompletionProposalCategory> categories =
		 * (List<CompletionProposalCategory>)
		 * ReflectionUtils.getPrivateField(ContentAssistProcessor.class,
		 * "fCategories", processor); List<CompletionProposalCategory>
		 * newCategories = new
		 * ArrayList<CompletionProposalCategory>(categories.size()-1); for
		 * (CompletionProposalCategory category : categories) { if
		 * (!category.getId().equals(
		 * "org.eclipse.jdt.ui.javaTypeProposalCategory") &&
		 * !category.getId().equals(
		 * "org.eclipse.jdt.ui.templateProposalCategory") &&
		 * !category.getId().equals("org.eclipse.ajdt.ui.templateCategory") &&
		 * !category.getId().equals("org.eclipse.jdt.ui.swtProposalCategory") &&
		 * !category.getId().equals(
		 * "org.eclipse.jdt.ui.javaNoTypeProposalCategory") &&
		 * !category.getId().equals(
		 * "org.eclipse.jdt.ui.javaAllProposalCategory") &&
		 * !category.getId().equals(
		 * "org.eclipse.mylyn.java.ui.javaAllProposalCategory")) {
		 * 
		 * newCategories.add(category); } }
		 * ReflectionUtils.setPrivateField(ContentAssistProcessor.class,
		 * "fCategories", processor, newCategories);
		 */
		//return assistant;
		ContentAssistant assistant= new ContentAssistant();
		//assistant.setContentAssistProcessor(new JavaCompletionProcessor(editor,assistant,IDocument.DEFAULT_CONTENT_TYPE), IDocument.DEFAULT_CONTENT_TYPE);
		//assistant.setContentAssistProcessor(new JavadocCompletionProcessor(editor,assistant), JavaPartitionScanner.JAVA_DOC);
		assistant.setContentAssistProcessor(new GroovyContentAssistProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(500);
		assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
		//assistant.setContextInformationPopupBackground(JavaEditorEnvironment.getJavaColorProvider().getColor(new RGB(150, 150, 0)));

		return assistant;
	}

}