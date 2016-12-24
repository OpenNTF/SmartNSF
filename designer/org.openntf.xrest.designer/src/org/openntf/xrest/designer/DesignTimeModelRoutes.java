package org.openntf.xrest.designer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.ibm.designer.domino.ide.resources.metamodel.DesignTimeModelController;
import com.ibm.designer.domino.ui.commons.extensions.DesignerFileEditorInput;
import com.ibm.designer.prj.resources.commons.ICommonDesignerProject;
import com.ibm.designer.prj.resources.commons.IDesignElement;

public class DesignTimeModelRoutes extends DesignTimeModelController {

	@Override
	public void actionSelected(Shell arg0, IActionDescriptor arg1, ICommonDesignerProject arg2, IDesignElement[] arg3) {
	}

	@Override
	public void createDesignElement(ICommonDesignerProject arg0, String arg1) {
	}

	@Override
	public boolean openDesign(ICommonDesignerProject designerProject, boolean b1, boolean b2) {
		if (designerProject != null) {
			IProject project = designerProject.getProject();
			if (project != null) {
				IFile file = project.getFile("WebContent/WEB-INF/routes.groovy");
				if (file == null || !file.exists()) {
					try {
						System.out.println("file doenst exist... try to create");
						byte[] bytes = "//XPages REST API Routes - please activate org.openntf.xrest.library".getBytes();
						InputStream source = new ByteArrayInputStream(bytes);
						file.create(source, IResource.NONE, null);
					} catch (Exception ex) {
						ex.printStackTrace();
					}					
				}
 				if ((file != null) && (file.exists())) {
					DesignerFileEditorInput editorInput = new DesignerFileEditorInput(file);
					try {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, org.eclipse.ui.editors.text.EditorsUI.DEFAULT_TEXT_EDITOR_ID, true);
						return true;
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return super.openDesign(designerProject, b1, b2);
	}
}
