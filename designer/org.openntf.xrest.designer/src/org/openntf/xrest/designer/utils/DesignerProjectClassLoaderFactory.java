package org.openntf.xrest.designer.utils;

import org.eclipse.core.resources.IProject;

import com.ibm.commons.vfs.VFSFolder;
import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;
import com.ibm.designer.domino.ide.resources.NsfException;
import com.ibm.designer.domino.ide.resources.project.DesignerExecutionContext;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.designer.runtime.ApplicationException;
import com.ibm.designer.runtime.server.util.DynamicClassLoaderVFS;

public class DesignerProjectClassLoaderFactory {

	private DesignerProjectClassLoaderFactory(){}
	
	public static ClassLoader buildDesignerClassLoader(IProject project) throws NsfException, ApplicationException {
		if (project == null) {
			return Thread.currentThread().getContextClassLoader();
		}
		
		IDominoDesignerProject ddProject = DominoResourcesPlugin.getDominoDesignerProject(project);
		DesignerExecutionContext dex = new DesignerExecutionContext("??", ddProject);
		VFSFolder classesFolder = dex.getVFS().getFolder("WebContent/WEB-INF/classes");
		ClassLoader cl;
		if (classesFolder.isDirectory()) {
			cl = new DynamicClassLoaderVFS(dex.getContextClassLoader(), classesFolder);
		} else {
			cl = dex.getContextClassLoader();
		}
		return cl;
	}
}
