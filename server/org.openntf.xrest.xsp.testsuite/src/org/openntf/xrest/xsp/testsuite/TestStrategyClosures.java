package org.openntf.xrest.xsp.testsuite;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.model.AttachmentSelectionType;
import org.openntf.xrest.xsp.model.AttachmentUpdateType;
import org.openntf.xrest.xsp.model.RouteProcessor;
import org.openntf.xrest.xsp.model.Router;
import org.openntf.xrest.xsp.model.strategy.SelectAttachment;
import org.openntf.xrest.xsp.model.strategy.StrategyModel;

public class TestStrategyClosures extends AbstractRouterBasics {

	@Override
	protected String getRouterDSLFileName() {
		return "router.groovy";
	}

	@Test
	public void testStrategyClosureGET() throws ExecutorException {
		Router router = getRouter();
		RouteProcessor rpGET = router.find("GET", "customers/1292/contract/Contract.pdf");
		RouteProcessor rpPOST = router.find("POST", "customers/1292/contract");
		assertNotNull(rpPOST);
		assertNotNull(rpGET);
		StrategyModel<?,?> modelGET = rpGET.getStrategyModel();
		assertTrue(modelGET instanceof SelectAttachment);
		StrategyModel<?,?> modelPOST = rpPOST.getStrategyModel();
		assertTrue(modelPOST instanceof SelectAttachment);
		SelectAttachment saModelGET = (SelectAttachment) modelGET;
		SelectAttachment saModelPOST = (SelectAttachment) modelPOST;
		assertEquals("Body", saModelGET.getFieldName(null));
		assertEquals("{attachmentName}", saModelGET.getAttachmentNameVariableName(null));
		assertEquals(AttachmentSelectionType.BY_NAME, saModelGET.getSelectionType());
		assertEquals(AttachmentUpdateType.REPLACE_ALL, saModelPOST.getUpdateType());
	}
}
