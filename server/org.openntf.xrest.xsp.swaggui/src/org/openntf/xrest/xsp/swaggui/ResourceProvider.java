package org.openntf.xrest.xsp.swaggui;

import java.net.URL;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.ibm.xsp.webapp.resources.BundleResourceProvider;

public class ResourceProvider extends BundleResourceProvider {
	public static final String RESOURCES_WEB_XPT = "/resources/web/swagger-ui/";
	public static final String SWAGGER_PREFIX = ".swaggerui";

	public ResourceProvider() {
		super(SwaggerUIActivator.getInstance().getBundle(), SWAGGER_PREFIX);
	}

	protected URL getResourceURL(HttpServletRequest arg0, String name) {
		String path = RESOURCES_WEB_XPT + name; // $NON-NLS-1$
		int fileNameIndex = path.lastIndexOf('/');
		String fileName = path.substring(fileNameIndex + 1);
		path = path.substring(0, fileNameIndex + 1);
		// see http://www.osgi.org/javadoc/r4v42/org/osgi/framework/Bundle.html
		// #findEntries%28java.lang.String,%20java.lang.String,%20boolean%29
		Enumeration<?> urls = getBundle().findEntries(path, fileName, false/* recursive */);
		if (null != urls && urls.hasMoreElements()) {
			URL url = (URL) urls.nextElement();
			if (null != url) {
				return url;
			}
		}
		return null; // no match, 404 not found.

	}

}
