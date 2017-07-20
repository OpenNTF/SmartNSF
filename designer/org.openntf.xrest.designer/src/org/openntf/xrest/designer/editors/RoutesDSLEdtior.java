package org.openntf.xrest.designer.editors;

import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;

public class RoutesDSLEdtior extends CompilationUnitEditor {

	private ColorManager colorManager;

	public RoutesDSLEdtior() {
		super();
		colorManager = new ColorManager();
		System.out.println("INIT SVC");
		setSourceViewerConfiguration(new RoutesDSLConfiguration(colorManager, this));
	}

	@Override
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
