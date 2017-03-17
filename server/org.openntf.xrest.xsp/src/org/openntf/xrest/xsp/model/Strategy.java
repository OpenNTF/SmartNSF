package org.openntf.xrest.xsp.model;

import org.openntf.xrest.xsp.model.strategy.AllByKey;
import org.openntf.xrest.xsp.model.strategy.AllByKeyPaged;
import org.openntf.xrest.xsp.model.strategy.AllByView;
import org.openntf.xrest.xsp.model.strategy.AllByViewPaged;
import org.openntf.xrest.xsp.model.strategy.GetByFT;
import org.openntf.xrest.xsp.model.strategy.GetByFTPaged;
import org.openntf.xrest.xsp.model.strategy.GetByKey;
import org.openntf.xrest.xsp.model.strategy.GetBySelect;
import org.openntf.xrest.xsp.model.strategy.GetBySelectPaged;
import org.openntf.xrest.xsp.model.strategy.GetByUNID;
import org.openntf.xrest.xsp.model.strategy.SelectAttachment;
import org.openntf.xrest.xsp.model.strategy.StrategyModel;

public enum Strategy {
	DOCUMENT_FROM_VIEW_BY_KEY(GetByKey.class),
	DOCUMENT_BY_UNID(GetByUNID.class),
	DOCUMENTS_BY_SEARCH_FT(GetByFT.class),
	DOCUMENTS_BY_SEARCH_FT_PAGED(GetByFTPaged.class),
	DOCUMENTS_BY_FORMULA(GetBySelect.class),
	DOCUMENTS_BY_FORMULA_PAGED(GetBySelectPaged.class),
	DOCUMENTS_BY_VIEW(AllByView.class),
	DOCUMENTS_BY_VIEW_PAGED(AllByViewPaged.class),
	DOCUMENTS_FROM_VIEW_BY_KEY(AllByKey.class),
	DOCUMENTS_FROM_VIEW_BY_KEY_PAGED(AllByKeyPaged.class),
	ATTACHMENT(SelectAttachment.class);

	private final Class<? extends StrategyModel<?, ?>> strategyClass;

	private Strategy(final Class<? extends StrategyModel<?, ?>> cl) {
		this.strategyClass = cl;
	}

	public StrategyModel<?, ?> constructModel() throws InstantiationException, IllegalAccessException {
		return strategyClass.newInstance();
	}
}
