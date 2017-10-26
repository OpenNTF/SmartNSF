package org.openntf.xrest.designer.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.ibm.designer.runtime.DesignerRuntime;

public class DominoDesignClassLoaderWrapper extends ClassLoader {

	private static final char BACKSLASH = '\\';
	private static final char FORWARDSLASH = '/';
	private static final String PROTOCOL_FILE = "file:///";
	private static final String JAR_EXTENSION = ".jar";
	private static final String DIRECTORY_MARKER = "/";

	public DominoDesignClassLoaderWrapper( ClassLoader mainClassLoader) throws CoreException {
		super(mainClassLoader);
	}

	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		System.out.println("search for...."+ name);
		try {
			Class<?> rc = DesignerRuntime.getJSContext().loadClass(name);
			if (rc != null) {
				System.out.println("FOND: "+name);
				return rc;
			}
		} catch (Exception e) {
			// NothingTODO
		}
		return super.findClass(name);
	}
}
