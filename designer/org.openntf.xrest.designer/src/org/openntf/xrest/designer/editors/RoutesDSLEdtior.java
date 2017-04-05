package org.openntf.xrest.designer.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class RoutesDSLEdtior extends TextEditor {

	private ColorManager colorManager;

	public RoutesDSLEdtior() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new RoutesDSLConfiguration(colorManager, this));
		// setDocumentProvider(new XMLDocumentProvider());
	}

	@Override
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
