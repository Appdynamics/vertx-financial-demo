package com.appdynamics.reactivetrade.util;

/**
 * Created by trader on 3/11/15.
 */
public class GenericScorecard extends MarketScorecard {

    public GenericScorecard() {
        super(0.0, 0.0, 0.0, 0.0);
    }

    public void subsume(MarketScorecard other) {
        adjustCustomerSatisfactionScore(other.getCustomerSatisfactionScore());
        addTradeCost(other.getTradeCost());
        addTradeRevenue(other.getTradeRevenue());
        addSpreadRevenue(other.getSpreadRevenue());
        adjustCustomerSatisfactionScore(other.getCustomerSatisfactionScore());
    }

    @Override
    protected void recordTradeRevenue(int quantity) {
    }
}
