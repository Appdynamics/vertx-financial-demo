package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.util.MarketScorecard;

/**
 * Created by trader on 3/11/15.
 */
public class PremierClientsScorecard extends MarketScorecard {

    public PremierClientsScorecard(double fixedCostPerTrade,
                                   double perShareCost,
                                   double perTradeCostForMarket,
                                   double custSatCostPerDroppedTrade) {
        super(fixedCostPerTrade, perShareCost, perTradeCostForMarket, custSatCostPerDroppedTrade);
    }

    @Override
    protected void recordTradeRevenue(int quantity) {
        double shareBasedRevenue = getPerShareCost() * quantity;
        addTradeRevenue(getFixedFeePerTrade() + shareBasedRevenue);
    }
}
