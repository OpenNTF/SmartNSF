package org.openntf.xrest.designer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.openntf.xrest.xsp.log.SmartNSFLoggerFactory;

import com.ibm.designer.domino.ide.resources.metamodel.DesignTimeModelController;
import com.ibm.designer.domino.ui.commons.extensions.DesignerFileEditorInput;
import com.ibm.designer.prj.resources.commons.ICommonDesignerProject;
import com.ibm.designer.prj.resources.commons.IDesignElement;

public class DesignTimeModelRoutes extends DesignTimeModelController {

	@Override
	public void actionSelected(Shell arg0, IActionDescriptor arg1, ICommonDesignerProject arg2, IDesignElement[] arg3) {
		// Not used
	}

	@Override
	public void createDesignElement(ICommonDesignerProject arg0, String arg1) {
		// Not used
	}

	@Override
	public boolean openDesign(ICommonDesignerProject designerProject, boolean b1, boolean b2) {
		if (designerProject == null || designerProject.getProject() == null) {
			return super.openDesign(designerProject, b1, b2);
		}
		IProject project = designerProject.getProject();
		IFile file = project.getFile("WebContent/WEB-INF/routes.groovy");
		if (file == null) {
			SmartNSFLoggerFactory.DDE.errorp(this, "openDesign", "Could not initialize File, this should not happen!");
			return false;
		}
		if (!file.exists()) {
			try {
				byte[] bytes = "//XPages REST API Routes - please activate org.openntf.xrest.library".getBytes();
				InputStream source = new ByteArrayInputStream(bytes);
				file.create(source, IResource.NONE, null);
			} catch (Exception ex) {
				SmartNSFLoggerFactory.DDE.errorp(this, "openDesign", ex, "Could not create routers.groovy in project (0)", designerProject);
			}
		}
		if (file.exists()) {
			DesignerFileEditorInput editorInput = new DesignerFileEditorInput(file);
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, "org.openntf.xrest.designer.editors.RoutesDSLEdtior", true);
				return true;
			} catch (PartInitException e) {
				SmartNSFLoggerFactory.DDE.errorp(this, "openDesign", e, "Could not create editor for routers.groovy in project (0)", designerProject);
			}
		}
		return false;
	}
}
