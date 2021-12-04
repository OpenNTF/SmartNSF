package org.openntf.xrest.designer.builder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

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
import org.openntf.xrest.designer.utils.DesignerProjectClassLoaderFactory;
import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.log.SmartNSFLoggerFactory;

import com.ibm.commons.util.io.StreamUtil;

import groovy.lang.GroovyRuntimeException;
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
				ClassLoader cl = DesignerProjectClassLoaderFactory.buildDesignerClassLoader(getProject());
				removeMarkers(resource);
				String dsl = StreamUtil.readString(((IFile) resource).getContents());
				Script sc =DSLBuilder.parseDSLScript(dsl, cl,"routes.groovy");
				sc.run();
			} catch (MultipleCompilationErrorsException cfe) {
				handleMultipleCompilationErrorEx(resource, cfe);
			} catch (IOException e) {
				SmartNSFLoggerFactory.DDE.errorp(this, "testFileForGroovy", e, "Unexpected IO Problem for (0)", resource.getProjectRelativePath().toPortableString());
			} catch (CoreException e) {
				SmartNSFLoggerFactory.DDE.errorp(this, "testFileForGroovy", e, "Unexpected Core Problem for (0)", resource.getProjectRelativePath().toPortableString());
			}
			catch (Exception e) {
				Throwable rc = StackTraceUtils.extractRootCause(e);
				addExceptionToMarker(rc, resource);
			}
		}
	}

	private void handleMultipleCompilationErrorEx(IResource resource, MultipleCompilationErrorsException cfe) {
		ErrorCollector errorCollector = cfe.getErrorCollector();
		processErrors(errorCollector, resource);
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
			System.out.println(err.getClass());
			Message msg = (Message) err;
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			msg.write(pw);
			addMarker(sw.toString() + err.getClass().getCanonicalName(), resource);
		}

	}
	
	private void addExceptionToMarker(Throwable e, IResource resource) {
		if (e instanceof GroovyRuntimeException ) {
			GroovyRuntimeException gre = (GroovyRuntimeException)e;
			if (gre.getNode() != null) {
				int lineNumber = gre.getNode().getLineNumber();
				addMarkerWithLineNumber(gre.getMessage(), lineNumber, resource);
				return;
			} 
			if(gre.getModule() != null) {
				int lineNumber = gre.getModule().getLineNumber();
				addMarkerWithLineNumber(gre.getMessage(), lineNumber, resource);
				return;
			}				
			addMarker(gre.getMessage(), resource);							
		} else {
			addMarker(e.getMessage()+" e: "+e.getClass().getCanonicalName(), resource);			
		}
	}

	private void addMarkerWithLineNumber(String message, int lineNumber, IResource resource) {
		try {
			IMarker marker = resource.createMarker("org.openntf.xrest.dsl.routes");

			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		} catch (CoreException e) {
			SmartNSFLoggerFactory.DDE.errorp(this, "addMarker", e, "Unexpected Problem for (0)", resource.getProjectRelativePath().toPortableString());
		}
		
	}
	private void addMarker(String message, IResource resource) {
		int lineNumber = getLineNumber(message);
		addMarkerWithLineNumber(message, lineNumber, resource);
	}

	private int getLineNumber(String message) {
		int startPoint = message.indexOf(" @ line");
		if (startPoint < 0) {
			return 1;
		}
		int endPoint = message.indexOf(',', startPoint);
		return Integer.parseInt(message.substring(startPoint + 7, endPoint).trim());
	}
}
