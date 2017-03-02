package org.openntf.xrest.xsp.model;

import org.openntf.xrest.xsp.model.strategy.AllByKey;
import org.openntf.xrest.xsp.model.strategy.AllByView;
import org.openntf.xrest.xsp.model.strategy.AllByViewPaged;
import org.openntf.xrest.xsp.model.strategy.GetByFT;
import org.openntf.xrest.xsp.model.strategy.GetByKey;
import org.openntf.xrest.xsp.model.strategy.GetBySelect;
import org.openntf.xrest.xsp.model.strategy.GetByUNID;
import org.openntf.xrest.xsp.model.strategy.SelectAttachment;
import org.openntf.xrest.xsp.model.strategy.StrategyModel;

public enum Strategy {
	SELECT_DOCUMENT_FROM_VIEW_BY_KEY(GetByKey.class),
	SELECT_DOCUMENT_BY_UNID(GetByUNID.class),
	SELECT_DOCUMENTS_BY_SEARCH_FT(GetByFT.class),
	SELECT_DOCUMENTS_BY_FORMULA(GetBySelect.class),
	SELECT_ALL_DOCUMENTS_BY_VIEW(AllByView.class),
	SELECT_ALL_DOCUMENTS_BY_VIEW_PAGED(AllByViewPaged.class),
	SELECT_ALL_DOCUMENTS_FROM_VIEW_BY_KEY(AllByKey.class),
	SELECT_ATTACHMENT(SelectAttachment.class),;

	private final Class<? extends StrategyModel<?,?>> strategyClass;

	private Strategy(final Class<? extends StrategyModel<?,?>> cl) {
		this.strategyClass = cl;
	}

	public StrategyModel<?,?> constructModel() throws InstantiationException, IllegalAccessException {
		return strategyClass.newInstance();
	}
}
