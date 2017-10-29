package org.openntf.xrestapi.designer.testsuite.contentassist;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import org.easymock.EasyMock;
import org.eclipse.jface.resource.ImageRegistry;

public class AbstractContentAssistProposalTest extends AbstractGroovyParserTest {

	public AbstractContentAssistProposalTest() {
		super();
	}

	protected ImageRegistry mockImageRegistry() {
		ImageRegistry imgRegistry = EasyMock.createNiceMock(ImageRegistry.class);
		expect(imgRegistry.get("bullet_green.png")).andReturn(null);
		replay(imgRegistry);
		return imgRegistry;
	}

}