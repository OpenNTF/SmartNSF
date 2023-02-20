package org.openntf.xrest.xsp.servlet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.openntf.xrest.xsp.authendpoint.TokenFactory;
import org.openntf.xrest.xsp.command.AuthorizationHandler;
import org.openntf.xrest.xsp.command.CommandDefinition;
import org.openntf.xrest.xsp.command.MetricsHandler;
import org.openntf.xrest.xsp.command.SwaggerHandler;
import org.openntf.xrest.xsp.command.UsersHandler;
import org.openntf.xrest.xsp.command.WhoAmIHandler;
import org.openntf.xrest.xsp.command.YamlHandler;
import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.model.Router;

import com.ibm.designer.runtime.domino.adapter.ComponentModule;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;

public class RouterFactory {

	private final ComponentModule module;
	private Router router;
	private Throwable error;
	private final CollectorRegistry collectorRegistry;
	private Histogram histogram;
	private List<CommandDefinition> commands = new ArrayList<CommandDefinition>();
	private final TokenFactory tokenFactory = new TokenFactory();

	
	public RouterFactory(ComponentModule module) {
		this.module = module;
		this.router = buildRouter();
		this.collectorRegistry = new CollectorRegistry();
		this.histogram = buildHistogram();
		registerCommands();
	}

	public void refresh() {
		error = null;
		this.router = buildRouter();
	}
	
	public void startup() {
		try {
			if (this.router.getAuthorizationEndpoint() != null ) {
				this.router.getAuthorizationEndpoint().startup();
			}
		} catch(Exception e) {
			e.printStackTrace();
			error =e;
		}
	}
	public Histogram getHistogram() {
		return histogram;
	}

	private Router buildRouter() {
		//CharsetProviderICU provider =  new CharsetProviderICU();
		try {
			InputStream is = module.getResourceAsStream("/WEB-INF/routes.groovy");
			if (is != null) {
				String dsl = new BufferedReader(
					      new InputStreamReader(is, StandardCharsets.ISO_8859_1))
				        .lines()
				        .collect(Collectors.joining("\n"));
				return DSLBuilder.buildRouterFromDSL(dsl, Thread.currentThread().getContextClassLoader());
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = e;
		}
		return null;
	}

	public Router getRouter() {
		return router;
	}

	public boolean hasError() {
		return error != null;
	}

	public Throwable getError() {
		return error;
	}

	private Histogram buildHistogram() {
		this.collectorRegistry.clear();
		Histogram.Builder builder =  Histogram.build()
				.labelNames("path", "method");
		builder.help("SmartNSF Execution Data");
		builder.name("http_xrest_request_duration_seconds");
		builder.buckets(new double[]{0.005,0.01,0.025,0.05,0.075,0.1,0.25,0.5,0.75,1,2.5,5,7.5,10});
		return builder.register(this.collectorRegistry);
	}
	private void registerCommands() {
		commands.add(new CommandDefinition(request -> "yaml".equals(request.getQueryString()), new YamlHandler()));
		commands.add(
				new CommandDefinition(request -> "swagger".equals(request.getQueryString()), new SwaggerHandler()));
		commands.add(new CommandDefinition(request -> "login".equals(request.getQueryString()), new WhoAmIHandler()));
		commands.add(new CommandDefinition(request -> "whoami".equals(request.getQueryString()), new WhoAmIHandler()));
		commands.add(
				new CommandDefinition(request -> request.getQueryString().startsWith("users"), new UsersHandler()));
		commands.add(new CommandDefinition(request -> "authorization".equals(request.getQueryString()),
				new AuthorizationHandler(this.tokenFactory)));
		commands.add(new CommandDefinition(request -> "metrics".equals(request.getQueryString()), new MetricsHandler(this.collectorRegistry)));
	}
	public Optional<CommandDefinition> findCommand(HttpServletRequest request) {
		return commands.stream().filter(cmd -> cmd.match(request)).findFirst();
	}

}
