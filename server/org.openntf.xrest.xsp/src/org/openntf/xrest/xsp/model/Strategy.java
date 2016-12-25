package org.openntf.xrest.xsp.model;

import org.openntf.xrest.xsp.model.strategy.AllByKey;
import org.openntf.xrest.xsp.model.strategy.AllByView;
import org.openntf.xrest.xsp.model.strategy.GetByFT;
import org.openntf.xrest.xsp.model.strategy.GetByKey;
import org.openntf.xrest.xsp.model.strategy.GetBySelect;
import org.openntf.xrest.xsp.model.strategy.GetByUNID;
import org.openntf.xrest.xsp.model.strategy.StrategyModel;

public enum Strategy {
	GET_FROM_VIEW_BY_KEY(GetByKey.class), GET_BY_UNID(GetByUNID.class), SEARCH_FT(GetByFT.class), SELECT_BY_FORMULA(GetBySelect.class), ALL_BY_VIEW(AllByView.class), ALL_FROM_VIEW_BY_KEY(AllByKey.class);
	
	private final Class<? extends StrategyModel> strategyClass;
	
	private Strategy(Class<? extends StrategyModel> cl ) {
		this.strategyClass = cl;
	}
	
	public StrategyModel constructModel() throws InstantiationException, IllegalAccessException {
		return strategyClass.newInstance();
	}
}
