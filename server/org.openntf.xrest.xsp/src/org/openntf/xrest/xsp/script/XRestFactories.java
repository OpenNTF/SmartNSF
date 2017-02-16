package org.openntf.xrest.xsp.script;
import com.ibm.xsp.library.XspContributor;

public class XRestFactories extends XspContributor {

	public XRestFactories() {
	}

	@Override
	public Object[][] getFactories() {
		
		return new Object[][]{new Object[] {"groovy", GroovyScriptBindingFactory.class}};
	}
}
