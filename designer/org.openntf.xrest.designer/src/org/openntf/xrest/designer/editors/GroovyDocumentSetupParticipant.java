package org.openntf.xrest.designer.editors;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;

public class GroovyDocumentSetupParticipant implements IDocumentSetupParticipant {

	/*
	 * @see
	 * org.eclipse.core.filebuffers.IDocumentSetupParticipant#setup(org.eclipse.
	 * jface.text.IDocument)
	 */
	public void setup(IDocument document) {
		setupJavaDocumentPartitioner(document, IJavaPartitions.JAVA_PARTITIONING);
	}

	/**
	 * Sets up the Java document partitioner for the given document for the
	 * given partitioning.
	 *
	 * @param document
	 *            the document to be set up
	 * @param partitioning
	 *            the document partitioning
	 * @since 3.0
	 */
	private void setupJavaDocumentPartitioner(IDocument document, String partitioning) {
		IDocumentPartitioner partitioner = createDocumentPartitioner();
		if (document instanceof IDocumentExtension3) {
			IDocumentExtension3 extension3 = (IDocumentExtension3) document;
			extension3.setDocumentPartitioner(partitioning, partitioner);
		} else {
			document.setDocumentPartitioner(partitioner);
		}
		partitioner.connect(document);
	}

	private IDocumentPartitioner createDocumentPartitioner() {
		return new FastPartitioner(new GroovyPartitionScanner(), GroovyPartitionScanner.LEGAL_CONTENT_TYPES);
	}
}