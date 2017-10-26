package org.openntf.xrest.designer.builder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.messages.Message;
import org.codehaus.groovy.runtime.StackTraceUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.openntf.xrest.xsp.dsl.DSLBuilder;

import com.ibm.commons.util.io.StreamUtil;
import com.ibm.commons.vfs.VFSFolder;
import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;
import com.ibm.designer.domino.ide.resources.project.DesignerExecutionContext;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.designer.runtime.server.util.DynamicClassLoaderVFS;

import groovy.lang.MissingPropertyException;
import groovy.lang.Script;

public class GroovyDSLBuilder extends IncrementalProjectBuilder {

	public GroovyDSLBuilder() {
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected IProject[] build(int buildType, Map arg1, IProgressMonitor arg2) throws CoreException {

		IProject project = getProject();
		if (buildType == FULL_BUILD) {
			fullBuild(project);
		} else {
			incrementalBuild(getDelta(project));
		}
		return null;
	}

	private void incrementalBuild(IResourceDelta delta) {
		try {
			delta.accept(new IResourceDeltaVisitor() {

				@Override
				public boolean visit(IResourceDelta arg0) throws CoreException {
					testFileForGoovy(arg0.getResource());
					return true;
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fullBuild(IProject project) {
		try {
			project.accept(new IResourceVisitor() {

				@Override
				public boolean visit(IResource arg0) throws CoreException {
					testFileForGoovy(arg0);
					return true;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void testFileForGoovy(IResource resource) {
		if ("WebContent/WEB-INF/routes.groovy".equalsIgnoreCase(resource.getProjectRelativePath().toPortableString())) {
			try {
				IDominoDesignerProject ddProject = DominoResourcesPlugin.getDominoDesignerProject(getProject());
				System.err.println(ddProject.getDatabaseName());
				DesignerExecutionContext dex = new DesignerExecutionContext("??", ddProject);
				VFSFolder classesFolder = dex.getVFS().getFolder("WebContent/WEB-INF/classes");
				ClassLoader cl;
				if (classesFolder.isDirectory()) {
					cl = new DynamicClassLoaderVFS(dex.getContextClassLoader(), classesFolder);
				} else {
					cl = dex.getContextClassLoader();
				}
				System.out.println("CL"+dex.getContextClassLoader());
				removeMarkers(resource);
				CompilationUnit cu = new CompilationUnit();
				String dsl = StreamUtil.readString(((IFile) resource).getContents());
				Script sc =DSLBuilder.parseDSLScript(dsl, cl);
				System.out.println(sc.toString());
				sc.run();
				System.out.println("run done");
			} catch (MultipleCompilationErrorsException cfe) {
				ErrorCollector errorCollector = cfe.getErrorCollector();
				processErrors(errorCollector, resource);
			} catch (IOException e) {
				System.out.println("IO EX");
				e.printStackTrace();
			} catch (CoreException e) {
				System.out.println("CORE EX");
				e.printStackTrace();
			}
			catch (Exception e) {
				Throwable rc = StackTraceUtils.extractRootCause(e);
				addMarker(rc.getMessage(), resource);
				System.out.println("GEN EX");
				System.out.println("Message:" +rc.getMessage());
				System.out.println("Class: "+rc.getClass());
				if (rc instanceof MissingPropertyException) {
					MissingPropertyException grc = (MissingPropertyException)rc;
					System.out.println(grc.getNode());
					System.out.println(grc.getModule());
					grc.printStackTrace();
				}
				
				//System.out.println(rc.getCause().getMessage());
				//rc.printStackTrace();
			}
		}

	}

	private void removeMarkers(IResource resource) throws CoreException {
		IMarker[] problems = null;
		int depth = IResource.DEPTH_INFINITE;
		problems = resource.findMarkers(IMarker.PROBLEM, true, depth);
		for (IMarker problem : problems) {
			problem.delete();
		}

	}

	private void processErrors(ErrorCollector errorCollector, IResource resource) {
		for (Object err : errorCollector.getErrors()) {
			Message msg = (Message) err;
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			msg.write(pw);
			addMarker(sw.toString(), resource);
		}

	}

	private void addMarker(String message, IResource resource) {
		try {
			IMarker marker = resource.createMarker("org.openntf.xrest.dsl.routes");

			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.LINE_NUMBER, getLineNumber(message));
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private int getLineNumber(String message) {
		int startPoint = message.indexOf(" @ line");
		if (startPoint < 0) {
			return 1;
		}
		int endPoint = message.indexOf(",", startPoint);
		return Integer.parseInt(message.substring(startPoint + 7, endPoint).trim());
	}

}
