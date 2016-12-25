package org.openntf.xrest.xsp.dsl;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.openntf.xrest.xsp.model.Router;
import org.openntf.xrest.xsp.model.Strategy;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;

public class DSLBuilder {

	public static Router buildRouterFromDSL(String dsl, ClassLoader cl) {
		Router router = new Router();
		// BasicGlobalSettings settings = new BasicGlobalSettings();
		Map<String, Object> bindings = new HashMap<String, Object>();
		// bindings.put(BINDING_VAR_SETTINGS, settings);
		bindings.put("router", router);
		evaluateScript(dsl, bindings, cl);
		return router;
	}

	public static void evaluateScript(String dsl, Map<String, Object> bindings, ClassLoader cl) {

		// Establish the compiler configuration - namely, the base class to use
		// for the context
		final CompilerConfiguration compilerConfig = new CompilerConfiguration();
		// compilerConfig.setScriptBaseClass(baseClass.getName());

		// Automatically import some enum references
		ImportCustomizer importCustomizer = new ImportCustomizer();
		importCustomizer.addStaticStars(Strategy.class.getCanonicalName());

		compilerConfig.addCompilationCustomizers(importCustomizer);

		// Create a new shell per run for safety
		Binding binding = new Binding();
		// binding.setVariable(BINDING_VAR_SCRIPTLOADER, scriptLoader);
		for (Map.Entry<String, Object> entry : bindings.entrySet()) {
			binding.setVariable(entry.getKey(), entry.getValue());
		}

		// Set stdout to be the Darwino platform output stream
		PrintStream out = System.out;
		binding.setProperty("out", out); //$NON-NLS-1$
		out.flush();

		GroovyShell shell = new GroovyShell(cl, binding, compilerConfig);
		shell.evaluate(dsl);
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
