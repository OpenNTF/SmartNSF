package org.openntf.xrest.xsp.testsuite;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.openntf.xrest.xsp.dsl.DSLBuilder;
import org.openntf.xrest.xsp.model.Router;

import com.ibm.commons.util.StringUtil;

public abstract class AbstractRouterBasics {

	private Router router;
	
	public AbstractRouterBasics() {
		super();
	}
	
	@Before
	public void setupTest() throws IOException {
		if (!StringUtil.isEmpty(getRouterDSLFileName())){
			router = buildRouter();
		}
			
	}

	protected Router buildRouter() throws IOException {
		String dsl = readFile();
		Router tmpRouter = DSLBuilder.buildRouterFromDSL(dsl, getClass().getClassLoader());
		Assert.assertNotNull(tmpRouter);
		return tmpRouter;
	}

	private String readFile() throws IOException {
		InputStream is = getClass().getResourceAsStream(getRouterDSLFileName());
		return IOUtils.toString(is, "utf-8");
	}

	abstract protected String getRouterDSLFileName();

	public Router getRouter() {
		return router;
	}

}