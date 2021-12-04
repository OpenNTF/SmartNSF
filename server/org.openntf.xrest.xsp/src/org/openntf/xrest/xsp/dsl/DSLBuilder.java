package org.openntf.xrest.xsp.dsl;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.openntf.xrest.xsp.model.AttachmentSelectionType;
import org.openntf.xrest.xsp.model.AttachmentUpdateType;
import org.openntf.xrest.xsp.model.EventException;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.Router;
import org.openntf.xrest.xsp.model.Strategy;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class DSLBuilder {

	public static Router buildRouterFromDSL(String dsl, ClassLoader cl) {
		Router router = new Router();
		Map<String, Object> bindings = new HashMap<String, Object>();
		bindings.put("router", router);
		evaluateScript(dsl, bindings, cl);
		return router;
	}

	private static void evaluateScript(String dsl, Map<String, Object> bindings, ClassLoader cl) {
		GroovyShell shell = prepareDSLShell(bindings, cl);
		shell.evaluate(dsl);
	}
	
	public static Script parseDSLScript(String dsl, ClassLoader cl, String fileName) {
		Router router = new Router();
		Map<String, Object> bindings = new HashMap<String, Object>();
		bindings.put("router", router);
		GroovyShell shell = prepareDSLShell(bindings, cl);
		return shell.parse(dsl,fileName);
	}
	
	private static GroovyShell prepareDSLShell(Map<String, Object> bindings, ClassLoader cl) {
		final CompilerConfiguration compilerConfig = new CompilerConfiguration();
		// Automatically import some enum references
		ImportCustomizer importCustomizer = new ImportCustomizer();
		importCustomizer.addStaticStars(Strategy.class.getCanonicalName());
		importCustomizer.addStaticStars(EventType.class.getCanonicalName());
		importCustomizer.addStaticStars(EventException.class.getCanonicalName());
		importCustomizer.addStaticStars(AttachmentSelectionType.class.getCanonicalName());
		importCustomizer.addStaticStars(AttachmentUpdateType.class.getCanonicalName());

		compilerConfig.addCompilationCustomizers(importCustomizer);
		// Create a new shell per run for safety
		Binding binding = new Binding();
		for (Map.Entry<String, Object> entry : bindings.entrySet()) {
			binding.setVariable(entry.getKey(), entry.getValue());
		}

		PrintStream out = System.out;
		binding.setProperty("out", out); //$NON-NLS-1$
		out.flush();
		
		GroovyShell shell = cl != null ?new GroovyShell(cl, binding, compilerConfig) : new GroovyShell(binding, compilerConfig);
		
		return shell;
	}

	public static void applyClosureToObject(Closure<Void> cl, Object obj) {
		int prevStrategy = cl.getResolveStrategy();
		Object prevDelegate = cl.getDelegate();

		cl.setDelegate(obj);
		cl.setResolveStrategy(Closure.DELEGATE_ONLY);
		cl.call();

		cl.setDelegate(prevDelegate);
		cl.setResolveStrategy(prevStrategy);

	}

	public static <T> T callClosure(Closure<T> closure, Object... params) {
		Object[] closureParams = new Object[closure.getMaximumNumberOfParameters()];
		for (int i = 0; i < closureParams.length; i++) {
			if (params.length > i) {
				closureParams[i] = params[i];
			}
		}
		return closure.call(closureParams);
	}
}
