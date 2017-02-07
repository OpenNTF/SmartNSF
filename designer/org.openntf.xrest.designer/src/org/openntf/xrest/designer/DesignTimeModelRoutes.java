package org.openntf.xrest.designer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, "org.openntf.xrest.designer.editors.RoutesDSLEdtior", true);
						return true;
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return super.openDesign(designerProject, b1, b2);
	}
	
	private void addBuilder(IProject project, String id) throws CoreException {
	      IProjectDescription desc = project.getDescription();
	      ICommand[] commands = desc.getBuildSpec();
	      for (int i = 0; i < commands.length; ++i)
	         if (commands[i].getBuilderName().equals(id))
	            return;
	      //add builder to project
	      ICommand command = desc.newCommand();
	      command.setBuilderName(id);
	      ICommand[] nc = new ICommand[commands.length + 1];
	      // Add it before other builders.
	      System.arraycopy(commands, 0, nc, 1, commands.length);
	      nc[0] = command;
	      desc.setBuildSpec(nc);
	      project.setDescription(desc, null);
	   }
}
